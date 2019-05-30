package com.network.storage.state;

import java.math.BigInteger;
import java.net.InetAddress;

public class RemoteBackupInfo extends FileBackupInfo {
    private InetAddress hostname;
    private Integer port;

    public RemoteBackupInfo(String name, BigInteger id, InetAddress hostname, Integer port) {
        super(name, 0, id);
        this.hostname = hostname;
        this.port = port;
    }

    public InetAddress getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }
}
