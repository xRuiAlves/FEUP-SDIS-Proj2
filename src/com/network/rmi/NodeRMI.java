package com.network.rmi;

import com.network.ChordNode;
import com.network.connections.ConnectionHandler;
import com.network.info.BasicInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.LookUp;
import com.network.messages.chord.Reclaimed;
import com.network.messages.protocol.Retrieved;
import com.network.storage.io.AsyncFileHandler;
import com.network.storage.state.BackupState;
import com.network.storage.state.FileBackupInfo;
import com.network.storage.state.RemoteBackupInfo;
import com.network.subscriptions.LookUpHandler;
import com.network.threads.ThreadPool;
import com.network.threads.operations.LookUpOperation;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class NodeRMI implements NodeRMIInterface {
    private ChordNode node;

    public NodeRMI(ChordNode node) {
        this.node = node;
    }

    @Override
    public BasicInfo lookup(BigInteger lookup_id) throws InterruptedException {
        LookUpHandler handler = new LookUpHandler();
        ConnectionHandler.getInstance().subscribeLookUp(lookup_id, handler);
        ThreadPool.getInstance().submit(new LookUpOperation(this.node, new LookUp(this.node, lookup_id)));
        handler.await();
        return handler.getInfo();
    }

    @Override
    public void reclaim(long size) {
        size = size >= 0 ? size : 0;
        BackupState.getInstance().setMaxDiskSizeKbs(size);
        Set<Map.Entry<BigInteger, FileBackupInfo>> savedFiles = BackupState.getInstance().getBackups().entrySet();
        while (BackupState.getInstance().isOversized()) {
            Map.Entry<BigInteger, FileBackupInfo> entry = getNextForReclaim(savedFiles);
            if (entry == null) return;

            final String filename = node.getBaseFolderName() + "/" + entry.getValue().getId();
            try {
                AsyncFileHandler.readFile(filename, (success, bytes_read, data) -> {
                    try {

                        if (!success) {
                            NetworkLogger.printLog(Level.WARNING, "Error retrieving file for redistribution");
                            return;
                        }

                        byte[] file_data = new byte[data.remaining()];
                        data.get(file_data, 0, file_data.length);

                        node.getSuccessor().getListener().getInternal().sendMessage(new Reclaimed(this.node,file_data, entry.getKey(), entry.getValue().getName()));
                    } catch (IOException e) {
                        NetworkLogger.printLog(Level.WARNING, "Error sending message for redistribution " + e.getMessage());
                    }
                    new File(filename).delete();
                });
            } catch (IOException e) {
                NetworkLogger.printLog(Level.WARNING, "Error reading file for reclaiming - " + entry.getKey());
                new File(filename).delete();
            }
            BackupState.getInstance().unregisterBackup(entry.getKey());
        }
    }

    private Map.Entry<BigInteger, FileBackupInfo> getNextForReclaim(Set<Map.Entry<BigInteger, FileBackupInfo>> savedFiles) {
        final Iterator<Map.Entry<BigInteger, FileBackupInfo>> iter = savedFiles.iterator();
        Map.Entry<BigInteger, FileBackupInfo> entry = iter.next();
        while (iter.hasNext() && entry.getValue() instanceof RemoteBackupInfo) {
            entry = iter.next();
        }

        if (!iter.hasNext() && entry.getValue() instanceof RemoteBackupInfo) {
            return null;
        }
        return entry;
    }
}
