package com.network.connections.client;

import com.network.ChordNode;
import com.network.connections.ConnectionHandler;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.*;
import com.network.threads.ThreadPool;
import com.network.threads.operations.SendMessage;

import java.io.EOFException;
import java.io.StreamCorruptedException;
import java.util.logging.Level;

public class Connection implements Runnable {
    private final ConnectionInterface ci;
    private ChordNode node;

    public Connection(ChordNode node, ConnectionInterface ci) {
        this.ci = ci;
        this.node = node;
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
                if (message instanceof LookUpAnsMessage) {
                    ConnectionHandler.getInstance().notify((LookUpAnsMessage) message);
                    this.ci.close();
                    return;
                } else if (message instanceof LookUpMessage) {
                    this.node.lookup((LookUpMessage) message);
                } else if (message instanceof GetPredecessor) {
                    // The same socket is live until the node that asks closes it
                    ThreadPool.getInstance().submit(new SendMessage(new Predecessor(this.node), this.ci));
                } else if (message instanceof Predecessor) {
                    this.node.setSuccessor(new NodeInfo(node, ((Predecessor) message).getId(), message.getHostname(), message.getPort()));
                } else if (message instanceof Notify) {
                    this.node.setPredecessor(new NodeInfo(node, ((Notify) message).getId(), message.getHostname(), message.getPort()));
                } else {
                    NetworkLogger.printLog(Level.SEVERE, "Message type not supported");
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
