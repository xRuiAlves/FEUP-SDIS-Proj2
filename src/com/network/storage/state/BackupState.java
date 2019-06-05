package com.network.storage.state;

import com.network.log.NetworkLogger;

import java.math.BigInteger;
import java.util.Enumeration;
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

    private long maxDiskSizeKbs = 1000000;

    private volatile long occupied_space_bytes = 0;

    public boolean registerBackup(FileBackupInfo info) {
        if (!this.canStore(info.getSize())) {
            NetworkLogger.printLog(Level.SEVERE, "Space Limit Reached! Unable to store file!");
            return false;
        }

        this.updateOccupied(info.getSize());
        this.backups.put(info.getId(), info);
        if (info instanceof RemoteBackupInfo) {
            NetworkLogger.printLog(Level.INFO, String.format("Remotly backed up file with name %s and id %s", info.getName(), info.getId()));

        } else {
            NetworkLogger.printLog(Level.INFO, String.format("Backed up file with name %s and id %s", info.getName(), info.getId()));
        }
        return true;
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
        return occupied_space_bytes + file_size <= maxDiskSizeKbs * 1000;
    }

    private synchronized void updateOccupied(long diff) {
        occupied_space_bytes += diff;
    }

    public ConcurrentHashMap<BigInteger, FileBackupInfo> getBackups() {
        return backups;
    }

    public Enumeration<BigInteger> getIds() {
        return this.backups.keys();
    }

    public FileBackupInfo get(BigInteger id) {
        return this.backups.getOrDefault(id, null);
    }

    public void setMaxDiskSizeKbs(long maxDiskSizeKbs) {
        this.maxDiskSizeKbs = maxDiskSizeKbs;
    }

    public boolean isOversized() {
        return occupied_space_bytes > maxDiskSizeKbs * 1000;
    }
}
