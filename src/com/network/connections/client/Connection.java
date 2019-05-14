package com.network.connections.client;

import com.network.connections.ConnectionHandler;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.LookUpMessage;
import com.network.messages.Message;
import com.network.Node;

import java.util.logging.Level;

public abstract class Connection implements ConnectionInterface{

    private Node node;

    public Connection(Node node) {

        this.node = node;
    }
    @Override
    public void run() {
        while(true) {
            try {
                Message message = this.getMessage();

                if (message instanceof LookUpAnsMessage) {
                    ConnectionHandler.getInstance().notify((LookUpAnsMessage) message);
                    this.close();
                    return;
                } else if (message instanceof LookUpMessage) {
                    this.node.lookup((LookUpMessage) message);
                } else {
                    NetworkLogger.printLog(Level.SEVERE,"Implement receive precedent request and precedent response");
                }

            } catch (Exception e) {
                NetworkLogger.printLog(Level.WARNING, "Error receiving message - " + e.getMessage());
                if (this.isClosed()) {
                    return;
                }
            }
        }
    }
}
