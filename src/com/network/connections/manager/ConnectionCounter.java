package com.network.connections.manager;

import com.network.connections.client.ConnectionInterface;

public class ConnectionCounter {

    private int count;
    private ConnectionInterface connection;

    public  ConnectionCounter(ConnectionInterface connection) {

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

    public ConnectionInterface getConnection() {
        return connection;
    }
}
