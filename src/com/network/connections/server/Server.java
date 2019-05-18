package com.network.connections.server;

import com.network.connections.client.ConnectionInterface;
import com.network.log.NetworkLogger;
import com.network.threads.ThreadPool;
import com.network.ChordNode;

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
                ConnectionInterface  connection = this.serverConnection.accept();
                ThreadPool.getInstance().submit(connection);
            } catch (IOException e) {
                NetworkLogger.printLog(Level.WARNING, "Error accepting connection - " + e.getMessage());
            }
        }
    }

    public ServerConnectionInterface getServerConnection() {
        return serverConnection;
    }
}
