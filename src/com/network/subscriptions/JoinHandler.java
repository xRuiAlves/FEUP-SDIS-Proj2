package com.network.subscriptions;

import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.ChordMessage;
import com.network.ChordNode;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class JoinHandler implements SubscriptionHandlerInterface {

    private ChordNode node;

    public JoinHandler(ChordNode node) {
        this.node = node;
    }
    @Override
    public void notify(ChordMessage msg) {
        if (node.getId().equals(msg.getSenderId())) {
            NetworkLogger.printLog(Level.SEVERE, "Node id already taken");
            System.exit(-5);
        }
        node.setSuccessor(new NodeInfo(node, msg.getSenderId(), msg.getHostname(), msg.getPort()), new ConcurrentLinkedQueue<>());
    }

    @Override
    public boolean isPermanent() {
        return false;
    }
}
