package com.network.subscriptions;

import com.network.NodeInfo;
import com.network.connections.client.TCPConnection;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.Message;
import com.network.Node;

import java.io.IOException;
import java.util.logging.Level;

public class JoinHandler implements SubscriptionHandlerInterface {

    private Node node;

    public JoinHandler(Node node) {
        this.node = node;
    }
    @Override
    public void notify(Message msg) {
        if (msg instanceof LookUpAnsMessage) {
            TCPConnection connection = null;
            try {
                connection = new TCPConnection(msg.getHostname(), msg.getPort());
            } catch (IOException e) {
                NetworkLogger.printLog(Level.SEVERE, "Failed connection to successor");
                System.exit(-2);
            }

            node.setSuccessor(new NodeInfo(((LookUpAnsMessage) msg).getId(), msg.getSenderId(), connection));
            NetworkLogger.printLog(Level.WARNING, "Implement - send update predecessor");
        }
    }
}
