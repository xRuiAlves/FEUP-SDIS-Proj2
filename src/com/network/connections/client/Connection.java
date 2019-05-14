package com.network.connections.client;

import com.network.connections.ConnectionHandler;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.LookUpMessage;
import com.network.messages.Message;

import java.math.BigInteger;
import java.util.logging.Level;

public abstract class Connection implements ConnectionInterface{
    private BigInteger id;

    @Override
    public void run() {
        while(true) {
            try {
                Message message = this.getMessage();
                this.id = message.getSenderId();
                if (message instanceof LookUpAnsMessage) {
                    ConnectionHandler.getInstance().notify((LookUpAnsMessage) message);
                    this.close();
                    return;
                } else if (message instanceof LookUpMessage) {
                    NetworkLogger.printLog(Level.SEVERE,"Implement table look up redirect");
                } else {
                    NetworkLogger.printLog(Level.SEVERE,"Implement receive precedent request and precedent response");
                }
            } catch (Exception e) {
                NetworkLogger.printLog(Level.WARNING, "Error receiving message - " + e.getMessage());
            }
        }
    }
}
