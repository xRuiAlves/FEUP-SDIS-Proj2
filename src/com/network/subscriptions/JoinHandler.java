package com.network.subscriptions;

import com.network.info.NodeInfo;
import com.network.connections.client.TCPConnection;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.Message;
import com.network.ChordNode;

import java.io.IOException;
import java.util.logging.Level;

public class JoinHandler implements SubscriptionHandlerInterface {

    private ChordNode node;

    public JoinHandler(ChordNode node) {
        this.node = node;
    }
    @Override
    public void notify(Message msg) {
        if (msg instanceof LookUpAnsMessage) {
            TCPConnection connection = null;
            try {
                connection = new TCPConnection(node, msg.getHostname(), msg.getPort());
                connection.start();
            } catch (IOException e) {
                NetworkLogger.printLog(Level.SEVERE, "Failed connection to successor");
                System.exit(-2);
            }

            node.setSuccessor(new NodeInfo(msg.getSenderId(), connection));
        }
    }

    @Override
    public boolean isPermanent() {
        return false;
    }
}
