package com.network.info;

import com.network.connections.client.ConnectionInterface;

import java.math.BigInteger;
import java.net.InetAddress;

public class NodeInfo implements Comparable, InfoInterface {
    private BigInteger access;
    private ConnectionInterface connection;
    private InetAddress ip;
    private Integer port;

    public NodeInfo(BigInteger access, ConnectionInterface connection) {
        this.access = access;
        this.connection = connection;
        ip = this.connection.getIp();
        port = this.connection.getPort();

    }

    public NodeInfo(BigInteger access, InetAddress ip, Integer port) {
        this.access = access;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public BigInteger getAccess() {
        return access;
    }

    public ConnectionInterface getConnection() {
        return connection;
    }

    @Override
    public InetAddress getIp() {
        return ip;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof NodeInfo)) {
            return 0;
        }
        return access.compareTo(((NodeInfo) o).access);
    }

    @Override
    public String toString() {
        return this.access.toString();
    }
}
