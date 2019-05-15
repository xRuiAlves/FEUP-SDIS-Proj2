package com.network.threads.operations;

import com.network.connections.client.ConnectionInterface;
import com.network.log.NetworkLogger;
import com.network.messages.Message;

import java.io.IOException;
import java.util.logging.Level;

public class SendMessage implements Runnable{

    private Message message;
    private ConnectionInterface connection;

    public SendMessage(Message message, ConnectionInterface connection) {
        this.message = message;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            this.connection.sendMessage(message);
            //NetworkLogger.printLog(Level.INFO, "Message sent " + message.getClass().getName());
        } catch (IOException e) {
            NetworkLogger.printLog(Level.WARNING, "Failure in sending " + message.getClass() + " - " + e.getMessage());
        }
    }

}
