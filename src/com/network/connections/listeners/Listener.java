package com.network.connections.listeners;

import com.network.ChordNode;
import com.network.connections.client.ConnectionInterface;
import com.network.log.NetworkLogger;
import com.network.messages.Message;
import com.network.threads.ThreadPool;

import java.io.EOFException;
import java.util.logging.Level;

public class Listener implements Runnable {
    final ConnectionInterface ci;
    ChordNode node;
    private final MessageVisitor visitor;

    public Listener(ChordNode node, ConnectionInterface ci) {
        this.ci = ci;
        this.node = node;
        visitor = new ListenerVisitor(this);
    }

    public void start() {
        ThreadPool.getInstance().submit(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = this.ci.getMessage();
                //NetworkLogger.printLog(Level.INFO, "Message Received - " + message.getClass().getSimpleName());

                if (message.accept(this.visitor)) {
                    this.ci.close();
                    return;
                }


            } catch (EOFException ignored) {
                NetworkLogger.printLog(Level.INFO, "Connection closed by peer");
                return;
            } catch (Exception e) {
                NetworkLogger.printLog(Level.WARNING, "Error receiving message - " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
    }

    public ConnectionInterface getInternal() {
        return ci;
    }
}
