package com.network.storage.state;

import com.network.log.NetworkLogger;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class BackupState {
    private static BackupState instance = new BackupState();

    public static BackupState getInstance() {
        return instance;
    }

    private BackupState() {
    }

    private ConcurrentHashMap<BigInteger, FileBackupInfo> backups = new ConcurrentHashMap<>();

    private static final long MAX_DISK_SIZE_KBS = 1000000;

    private long occupied_space_bytes = 0;

    public void registerBackup(FileBackupInfo info) {
        if (!this.canStore(info.getSize())) {
            NetworkLogger.printLog(Level.SEVERE, "Space Limit Reached! Unable to store file!");
            return;
        }

        this.updateOccupied(info.getSize());
        this.backups.put(info.getId(), info);

        NetworkLogger.printLog(Level.INFO, String.format("Backed up file with name %s and id %s", info.getName(), info.getId()));
    }

    public void unregisterBackup(BigInteger id) {
        FileBackupInfo removed = this.backups.remove(id);
        this.updateOccupied(-removed.getSize());

        NetworkLogger.printLog(Level.INFO, String.format("Removed backed up file with name %s and id %s", removed.getName(), removed.getId()));
    }

    public boolean isBackedUp(BigInteger id) {
        return this.backups.containsKey(id);
    }

    public boolean canStore(long file_size) {
        return occupied_space_bytes + file_size <= MAX_DISK_SIZE_KBS;
    }

    private synchronized void updateOccupied(long diff) {
        occupied_space_bytes += diff;
    }
}
