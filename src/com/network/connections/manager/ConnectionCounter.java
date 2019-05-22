package com.network.connections.manager;

import com.network.connections.listeners.Listener;

public class ConnectionCounter {

    private int count;
    private Listener listener;

    public ConnectionCounter(Listener listener) {
        this.listener = listener;
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

    public Listener getListener() {
        return listener;
    }
}
