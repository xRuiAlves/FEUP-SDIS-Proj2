package com.network.threads.operations;

import com.network.ChordNode;
import com.network.connections.listeners.Listener;
import com.network.connections.client.JSSETCPConnection;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.LookUpAnswer;
import com.network.messages.chord.LookUp;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

public class LookUpOperation implements Runnable {
    private final ChordNode node;
    private LookUp message;

    public LookUpOperation(ChordNode node, LookUp message) {
        this.node = node;
        this.message = message;
    }

    @Override
    public void run() {
        BigInteger lookup_id = message.getId();
        try {
            if (lookup_id.equals(node.getId())
                    || (node.getPredecessor() != null
                    && (this.inNode(lookup_id, node.getPredecessor().getId(), node.getId())))
                    || (node.getSuccessor().getId().equals(node.getId()))
            ) {
                Listener listener = new Listener(node, new JSSETCPConnection(message.getHostname(), message.getPort()));
                listener.getInternal().sendMessage(new LookUpAnswer(this.node, message.getId()));
                // NetworkLogger.printLog(Level.INFO, "Lookup " + message.getId() + " sent to " + message.getHostname() + ":" + message.getPort());
                return;
            }

            NodeInfo closestPrecedent = this.findClosestPrecedent(lookup_id);
            closestPrecedent.getListener().getInternal().sendMessage(message);

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error in lookup " + lookup_id + " operation - " + e.getMessage());
            if (this.node.getSuccessor() == null) {
                System.exit(-3);
            }
        }
    }

    private NodeInfo findClosestPrecedent(BigInteger lookup_id) {
        BigInteger n = this.node.getId();
        NodeInfo targetNode = this.node.getSuccessor();

        if (n.equals(targetNode.getId()) || this.inNode(lookup_id, n, targetNode.getId())) {
            return targetNode;
        }

        ConcurrentLinkedDeque<BigInteger> order = this.node.getFingerTableOrder();
        ConcurrentHashMap<BigInteger, InfoInterface> fingers = this.node.getFingerTable();

        for (BigInteger i : order) {
            InfoInterface nodeInfo = fingers.get(i);
            if (nodeInfo instanceof NullInfo) {
                continue;
            }

            if (nodeInfo.getId().equals(lookup_id)) {
                return (NodeInfo) nodeInfo;
            }

            if (!this.inNode(nodeInfo.getId(), n, lookup_id)) {
                return targetNode;
            }
            targetNode = (NodeInfo) nodeInfo;
        }
        return targetNode;
    }


    private boolean inNode(BigInteger id, BigInteger lower, BigInteger upper) {
        if (lower.compareTo(upper) > 0) {
            return (id.compareTo(lower) > 0 || id.compareTo(upper) < 0);
        } else {
            return id.compareTo(lower) > 0 && id.compareTo(upper) < 0;
        }
    }
}