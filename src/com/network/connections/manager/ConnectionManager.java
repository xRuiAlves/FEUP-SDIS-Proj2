package com.network.connections.manager;

import com.network.ChordNode;
import com.network.connections.client.Connection;
import com.network.connections.client.ConnectionInterface;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<BigInteger, ConnectionCounter> connections;

    public ConnectionManager() {
        this.connections = new ConcurrentHashMap<>();
    }

    public boolean contains(BigInteger id) {
        return this.connections.containsKey(id);
    }

    public Connection get(BigInteger id) {
        ConnectionCounter connectionCounter = this.connections.get(id);
        connectionCounter.inc();
        return connectionCounter.getConnection();
    }

    public void decreaseInterface(BigInteger id) {
        if (this.connections.get(id).dec()) {
            this.connections.remove(id);
        }
    }

    public void put(BigInteger senderId, Connection connection) {
        this.connections.put(senderId, new ConnectionCounter(connection));
    }

    public ConcurrentHashMap<BigInteger, ConnectionCounter> getConnections() {
        return connections;
    }
}
