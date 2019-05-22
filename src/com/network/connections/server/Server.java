package com.network.connections.server;

import com.network.ChordNode;
import com.network.connections.listeners.Listener;
import com.network.log.NetworkLogger;
import com.network.threads.ThreadPool;

import java.io.IOException;
import java.util.logging.Level;

public class Server implements Runnable{

    private final ServerConnectionInterface serverConnection;

    public Server(ChordNode node) throws IOException {
        this.serverConnection = new JSSEServerConnection(node);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Listener listener = this.serverConnection.accept();
                ThreadPool.getInstance().submit(listener);
            } catch (IOException e) {
                NetworkLogger.printLog(Level.WARNING, "Error accepting connection - " + e.getMessage());
            }
        }
    }

    public ServerConnectionInterface getServerConnection() {
        return serverConnection;
    }
}
