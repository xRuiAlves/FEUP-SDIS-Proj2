package com.network.storage.state;

import java.math.BigInteger;
import java.util.Objects;

public class FileBackupInfo {
    private final String name;
    private final long size;

    private final BigInteger id;

    public FileBackupInfo(String name, long size, BigInteger id) {
        this.name = name;
        this.size = size;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (getClass() != o.getClass() && o.getClass() != BigInteger.class)) return false;

        if (o instanceof FileBackupInfo) {
            FileBackupInfo that = (FileBackupInfo) o;
            return Objects.equals(id, that.id);
        } else {
            BigInteger that_id = (BigInteger) o;
            return Objects.equals(id, that_id);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getSize() {
        return size;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
