package com.network.connections.listeners;

import com.network.ChordNode;
import com.network.connections.ConnectionHandler;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.info.BasicInfo;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.*;
import com.network.messages.protocol.*;
import com.network.storage.io.AsyncFileHandler;
import com.network.storage.state.BackupState;
import com.network.storage.state.FileBackupInfo;
import com.network.storage.state.RemoteBackupInfo;
import com.network.threads.ThreadPool;
import com.network.threads.operations.SendMessage;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class ListenerVisitor extends DefaultListener {
    private final Listener l;

    ListenerVisitor(Listener l) {
        this.l = l;
    }

    @Override
    public void visit(LookUpAnswer msg) {
        ConnectionHandler.getInstance().notify(msg);
    }

    @Override
    public void visit(GetPredecessor getPredecessor) throws UnknownHostException {
        ConcurrentLinkedQueue<BasicInfo> successors = new ConcurrentLinkedQueue<>();
        successors.add(new BasicInfo(l.node.getSuccessor().getIp(), l.node.getSuccessor().getPort()));

        ConcurrentLinkedQueue<BasicInfo> nodeSuccessors = l.node.getSuccessors();
        for (BasicInfo info:  nodeSuccessors) {
            if (successors.size() >= ChordNode.fault) {
                break;
            }

            successors.add(info);
        }
        ThreadPool.getInstance().submit(new SendMessage(new Predecessor(l.node, successors), l.ci));
    }

    @Override
    public void visit(LookUp lookUp) {
        l.node.lookup(lookUp);
    }

    @Override
    public void visit(Notify notify) {
        l.node.setPredecessor(new NodeInfo(l.node, notify.getId(), notify.getHostname(), notify.getPort()));
    }

    @Override
    public void visit(Predecessor predecessor) {
        l.node.setSuccessor(new NodeInfo(l.node, predecessor.getId(), predecessor.getHostname(), predecessor.getPort()), predecessor.getSuccessors());
    }

    @Override
    public void visit(RequestBackup requestBackup) throws IOException {
        if (BackupState.getInstance().canStore(requestBackup.getSize())) {
            l.ci.sendMessage(new Yes());
        } else {
            l.ci.sendMessage(new No());
        }
    }

    @Override
    public void visit(Backup backup) throws IOException {
        String folder_name = l.node.getBaseFolderName();
        File folder = new File(folder_name);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!BackupState.getInstance().registerBackup(new FileBackupInfo(backup.getName(), backup.getFileData().length, backup.getId()))) {
            l.ci.sendMessage(new No());
            return;
        }

        try {
            AsyncFileHandler.writeToFile(folder_name + "/" + backup.getId(), ByteBuffer.wrap(backup.getFileData()), (boolean success, int bytes_written) -> {
                try {
                    if (success) {
                        l.node.getFileRedistribution().addIdToCheck(backup.getId());
                        l.ci.sendMessage(new Yes());
//                        NetworkLogger.printLog(Level.INFO, String.format("%d bytes were written successfully!", bytes_written));
                    } else {
                        BackupState.getInstance().unregisterBackup(backup.getId());
                        l.ci.sendMessage(new No());
                        NetworkLogger.printLog(Level.WARNING, "File writing was not successful.");
                    }
                } catch (Exception e) {
                    NetworkLogger.printLog(Level.WARNING, "Error sending reply - " + e.getMessage());
                }

            });
        } catch (IOException e) {
            BackupState.getInstance().unregisterBackup(backup.getId());
            l.ci.sendMessage(new No());
        }
    }

    @Override
    public void visit(RetrieveIfExists retrieveIfExists) throws IOException {
        String folder_name = l.node.getBaseFolderName();
        File folder = new File(folder_name);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!BackupState.getInstance().isBackedUp(retrieveIfExists.getId())) {
            l.ci.sendMessage(new No());
            return;
        }

        FileBackupInfo info = BackupState.getInstance().get(retrieveIfExists.getId());
        if (info instanceof RemoteBackupInfo) {
            RemoteBackupInfo remote = (RemoteBackupInfo) info;
            l.ci.sendMessage(new Redirect(remote.getHostname(), remote.getPort()));
        }

        try {
            AsyncFileHandler.readFile(folder_name + "/" + retrieveIfExists.getId(), (success, bytes_read, data) -> {
                try {
                    if (!success) {
                        l.ci.sendMessage(new No());
                        return;
                    }

                    byte[] file_data = new byte[data.remaining()];
                    data.get(file_data, 0, file_data.length);

                    l.ci.sendMessage(new Retrieved(file_data));
                } catch (IOException e) {
                    NetworkLogger.printLog(Level.WARNING, "Error retrieving requested file - " + e.getMessage());
                }
            });
        } catch (IOException e) {
            l.ci.sendMessage(new No());
        }
    }

    @Override
    public void visit(Delete delete) {
        String folder_name = l.node.getBaseFolderName();
        if (BackupState.getInstance().isBackedUp(delete.getId())) {
            File f = new File(folder_name + "/" + delete.getId());
            if (f.delete()) {
                BackupState.getInstance().unregisterBackup(delete.getId());
                NetworkLogger.printLog(Level.INFO, String.format("Deleted file with id %s successfully", delete.getId()));
            } else {
                NetworkLogger.printLog(Level.INFO, String.format("File with id %s could not be deleted", delete.getId()));
            }
        } else {
            NetworkLogger.printLog(Level.INFO, String.format("Received Delete for File with id %s that is not backed up", delete.getId()));
        }
    }

    @Override
    public void visit(Reclaimed reclaimed) throws IOException {

        if (l.node.getId().equals(reclaimed.getSenderId()) || reclaimed.alreadyVisited(l.node.getId())) {
            NetworkLogger.printLog(Level.SEVERE, "Failed to save reclaimed file - " + reclaimed.getId());
            return;
        }

        reclaimed.visited(l.node.getId());
        
        String folder_name = l.node.getBaseFolderName();
        File folder = new File(folder_name);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!BackupState.getInstance().registerBackup(new FileBackupInfo(reclaimed.getName(), reclaimed.getFileData().length, reclaimed.getId(), false))) {
            l.node.getSuccessor().getListener().getInternal().sendMessage(reclaimed);
            return;
        }

        try {
            AsyncFileHandler.writeToFile(folder_name + "/" + reclaimed.getId(), ByteBuffer.wrap(reclaimed.getFileData()), (boolean success, int bytes_written) -> {
                try {
                    if (success) {
                        ConnectionInterface connection = new JSSETCPConnection(reclaimed.getHostname(), reclaimed.getPort());
                        connection.sendMessage(new SaveReclaimed(l.node, reclaimed.getName(), reclaimed.getId()));
                    } else {
                        BackupState.getInstance().unregisterBackup(reclaimed.getId());
                        l.node.getSuccessor().getListener().getInternal().sendMessage(reclaimed);
                        NetworkLogger.printLog(Level.WARNING, "File writing was not successful.");
                    }

                } catch (Exception e) {
                    NetworkLogger.printLog(Level.WARNING, "Error sending reply - " + e.getMessage());
                }

            });
        } catch (IOException e) {
            BackupState.getInstance().unregisterBackup(reclaimed.getId());
            l.node.getSuccessor().getListener().getInternal().sendMessage(reclaimed);
        }
    }


    @Override
    public void visit(SaveReclaimed saveReclaimed) {
        BackupState.getInstance().registerBackup(new RemoteBackupInfo(saveReclaimed.getName(), saveReclaimed.getId(), saveReclaimed.getHostname(), saveReclaimed.getPort()));
    }

    @Override
    public void visit(RemoteSave remoteSave) {
        BackupState.getInstance().registerBackup(new RemoteBackupInfo(remoteSave.getName(), remoteSave.getId(), remoteSave.getHostname(), remoteSave.getPort()));
    }
}
