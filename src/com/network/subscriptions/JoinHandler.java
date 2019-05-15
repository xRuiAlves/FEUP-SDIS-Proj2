package com.network.subscriptions;

import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.Message;
import com.network.ChordNode;

import java.util.logging.Level;

public class JoinHandler implements SubscriptionHandlerInterface {

    private ChordNode node;

    public JoinHandler(ChordNode node) {
        this.node = node;
    }
    @Override
    public void notify(Message msg) {
        if (node.getId().equals(msg.getSenderId())) {
            NetworkLogger.printLog(Level.SEVERE, "Node id already taken");
            System.exit(-5);
        }
        node.setSuccessor(new NodeInfo(node, msg.getSenderId(), msg.getHostname(), msg.getPort()));
    }

    @Override
    public boolean isPermanent() {
        return false;
    }
}
