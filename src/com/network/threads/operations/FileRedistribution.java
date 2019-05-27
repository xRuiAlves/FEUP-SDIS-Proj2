package com.network.threads.operations;

import com.network.ChordNode;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.protocol.Backup;
import com.network.storage.io.AsyncFileHandler;
import com.network.storage.state.BackupState;
import com.network.storage.state.FileBackupInfo;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class FileRedistribution implements Runnable {

    private ChordNode node;
    private ConcurrentLinkedQueue<BigInteger> idsToCheck = new ConcurrentLinkedQueue<>();

    public FileRedistribution(ChordNode node) {
        this.node = node;
    }

    public void addIdToCheck(BigInteger id) {
        idsToCheck.add(id);
    }

    @Override
    public void run() {
        String folder_name = this.node.getBaseFolderName();
        File folder = new File(folder_name);
        if (!folder.exists()) {
            folder.mkdirs();
        }


        Iterator<BigInteger> iter = idsToCheck.iterator();
        while (iter.hasNext()) {
            final BigInteger id = iter.next();

            if (BackupState.getInstance().isBackedUp(id) && this.node.getPredecessor() instanceof NodeInfo && this.needsRedistribution(id)) {
                NetworkLogger.printLog(Level.WARNING, "File with id " + id + " belongs to the predecessor");
                final NodeInfo predecessor = (NodeInfo) this.node.getPredecessor();
                final FileBackupInfo info = BackupState.getInstance().get(id);

                if (info == null)
                    continue;

                try {
                    final String filename = folder_name + "/" + id;
                    AsyncFileHandler.readFile(filename, (success, bytes_read, data) -> {
                        try {
                            if (!success) {
                                return;
                            }
                            byte[] file_data = new byte[data.remaining()];
                            data.get(file_data, 0, file_data.length);
                            predecessor.getListener().getInternal().sendMessage(new Backup(file_data, info.getId(), info.getName()));

                            BackupState.getInstance().unregisterBackup(id);
                            File unnecessaryFile = new File(filename);
                            if (!unnecessaryFile.delete()){
                                NetworkLogger.printLog(Level.WARNING, "Failed to delete file - " + filename);
                            }

                            NetworkLogger.printLog(Level.INFO, "Redistribution of " + id + " successful");

                        } catch (IOException e) {
                            NetworkLogger.printLog(Level.WARNING, "Error redistributing files - " + e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    NetworkLogger.printLog(Level.WARNING, "Error reading file in redistribution " + e.getMessage());
                }
            }

            iter.remove();
        }
    }

    private boolean needsRedistribution(BigInteger fileId) {
        BigInteger currId = this.node.getId();
        BigInteger predId = this.node.getPredecessor().getId();
        return (predId.compareTo(currId) < 0 && (predId.compareTo(fileId) >= 0 || fileId.compareTo(currId) > 0))
                || (predId.compareTo(currId) > 0 && predId.compareTo(fileId) >= 0 && currId.compareTo(fileId) < 0);
    }
}
