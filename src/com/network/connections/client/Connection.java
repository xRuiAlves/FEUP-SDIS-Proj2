package com.network.connections.client;

import com.network.ChordNode;
import com.network.connections.ConnectionHandler;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.*;

import java.util.logging.Level;

public abstract class Connection implements ConnectionInterface{

    private ChordNode node;

    protected Connection(ChordNode node) {

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
                } else if(message instanceof GetPredecessor) {
                    // The same socket is live until the node that asks closes it
                    this.sendMessage(new Predecessor(this.node));
                    NetworkLogger.printLog(Level.INFO, "Predecessor sent");
                } else if (message instanceof Predecessor) {
                    NetworkLogger.printLog(Level.WARNING, "Change predecessor - " + ((Predecessor) message).getId());
                    this.node.setPredecessor(new NodeInfo(((Predecessor) message).getId(), message.getHostname(), message.getPort()));
                }
                else {
                    NetworkLogger.printLog(Level.SEVERE,"Implement receive precedent request and precedent response");
                }

            } catch (Exception e) {
                if (this.isClosed()) {
                    NetworkLogger.printLog(Level.INFO, "Connection closed");
                    return;
                }
                NetworkLogger.printLog(Level.WARNING, "Error receiving message - " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
    }
}
