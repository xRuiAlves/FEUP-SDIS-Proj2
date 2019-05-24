package com.network.connections.listeners;

import com.network.connections.ConnectionHandler;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.*;
import com.network.messages.protocol.*;
import com.network.storage.io.AsyncFileHandler;
import com.network.storage.state.BackupState;
import com.network.storage.state.FileBackupInfo;
import com.network.threads.ThreadPool;
import com.network.threads.operations.SendMessage;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
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
        // The same socket is live until the node that asks closes it
        ThreadPool.getInstance().submit(new SendMessage(new Predecessor(l.node), l.ci));
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
        l.node.setSuccessor(new NodeInfo(l.node, predecessor.getId(), predecessor.getHostname(), predecessor.getPort()));
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
        // TODO: Change folder name to file system
        String folder_name = l.node.getBaseFolderName();
        File folder = new File(folder_name);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!BackupState.getInstance().registerBackup(new FileBackupInfo(backup.getName(), backup.getFileData().length, backup.getId()))) {
            l.ci.sendMessage(new No());
        }

        try {
            AsyncFileHandler.writeToFile(folder_name + "/" + backup.getId(), ByteBuffer.wrap(backup.getFileData()), (boolean success, int bytes_written) -> {
                try {
                    if (success) {
                        l.ci.sendMessage(new Yes());
                        NetworkLogger.printLog(Level.INFO, String.format("%d bytes were written successfully!\n", bytes_written));
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
                    e.printStackTrace();
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
                NetworkLogger.printLog(Level.INFO, String.format("Deleted file with id %s successfully", delete.getId()));
            } else {
                NetworkLogger.printLog(Level.INFO, String.format("File with id %s could not be deleted", delete.getId()));
            }
        } else {
            NetworkLogger.printLog(Level.INFO, String.format("Received Delete for File with id %s that is not backed up", delete.getId()));
        }
    }
}
