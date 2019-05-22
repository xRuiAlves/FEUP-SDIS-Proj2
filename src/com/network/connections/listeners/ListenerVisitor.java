package com.network.connections.listeners;

import com.network.connections.ConnectionHandler;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.*;
import com.network.messages.protocol.Backup;
import com.network.messages.protocol.No;
import com.network.messages.protocol.RequestBackup;
import com.network.messages.protocol.Yes;
import com.network.storage.io.AsyncFileHandler;
import com.network.storage.state.BackupState;
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
        String folder_name = "node_" + l.node.getId();
        File folder = new File(folder_name);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        AsyncFileHandler.writeToFile(folder_name + "/" + backup.getName(), ByteBuffer.wrap(backup.getFileData()), (boolean success, int bytes_written) -> {
            try {
                if (success) {
                    l.ci.sendMessage(new Yes());
                    NetworkLogger.printLog(Level.INFO,String.format("%d bytes were written successfully!\n", bytes_written));
                } else {
                    l.ci.sendMessage(new No());
                    NetworkLogger.printLog(Level.WARNING, "File writing was not successful.");
                }
            } catch (Exception e) {
                NetworkLogger.printLog(Level.WARNING, "Error sending reply - " + e.getMessage());
            }

        });
    }
}
