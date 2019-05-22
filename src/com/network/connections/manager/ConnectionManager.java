package com.network.connections.manager;

import com.network.connections.listeners.Listener;

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

    public Listener get(BigInteger id) {
        ConnectionCounter connectionCounter = this.connections.get(id);
        connectionCounter.inc();
        return connectionCounter.getListener();
    }

    public void decreaseInterface(BigInteger id) {
        if (this.connections.get(id).dec()) {
            this.connections.remove(id);
        }
    }

    public void put(BigInteger senderId, Listener listener) {
        this.connections.put(senderId, new ConnectionCounter(listener));
    }

    public ConcurrentHashMap<BigInteger, ConnectionCounter> getConnections() {
        return connections;
    }
}
