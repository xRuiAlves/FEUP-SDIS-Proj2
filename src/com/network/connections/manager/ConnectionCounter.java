package com.network.connections.manager;

import com.network.connections.client.Connection;

public class ConnectionCounter {

    private int count;
    private Connection connection;

    public ConnectionCounter(Connection connection) {
        this.connection = connection;
        this.count = 0;
    }


    public void inc() {
        this.count++;
    }

    public boolean dec() {
        this.count--;
        return count == 0;
    }

    public int getCount() {
        return count;
    }

    public Connection getConnection() {
        return connection;
    }
}
