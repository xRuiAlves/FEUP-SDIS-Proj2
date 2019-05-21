package com.network.info;

import com.network.ChordNode;
import com.network.connections.client.Connection;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.connections.client.TCPConnection;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;

public class NodeInfo implements Comparable, InfoInterface {
    private ChordNode node;
    private BigInteger id;
    private Connection connection;
    private InetAddress ip;
    private Integer port;

    public NodeInfo(ChordNode node, BigInteger id, Connection connection) {
        this.node = node;
        this.id = id;
        this.connection = connection;
        ip = this.connection.getInternal().getIp();
        port = this.connection.getInternal().getPort();

    }

    public NodeInfo(ChordNode node, BigInteger id, InetAddress ip, Integer port) {
        this.node = node;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public BigInteger getId() {
        return id;
    }

    public Connection getConnection() {
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
    public void startConnection() throws IOException {
        if (this.connection == null) {
            this.connection = new Connection(node, new JSSETCPConnection(this.ip, this.port));
            this.connection.start();
        }
    }

    public boolean inInterval(BigInteger lower, BigInteger upper) {
        if (lower.compareTo(upper) > 0) {
            return (this.id.compareTo(lower) > 0 || this.id.compareTo(upper) < 0);
        } else {
            return this.id.compareTo(lower) > 0 && this.id.compareTo(upper) < 0;
        }
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof NodeInfo)) {
            return 0;
        }
        return id.compareTo(((NodeInfo) o).id);
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
