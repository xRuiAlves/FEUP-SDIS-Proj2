package com.network.threads.operations;

import com.network.ChordNode;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.connections.client.TCPConnection;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.LookUpMessage;

import javax.sound.sampled.Line;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

public class LookUpOperation implements Runnable {
    private final ChordNode node;
    private LookUpMessage message;

    public LookUpOperation(ChordNode node, LookUpMessage message) {
        this.node = node;
        this.message = message;
    }

    public void run() {
        try {
            BigInteger lookup_id = message.getId();
            if (lookup_id.equals(node.getId())
                    || (node.getPredecessor() != null
                    && (this.inNode(lookup_id, node.getPredecessor().getId(), node.getId())))
                    || (node.getSuccessor().getId().equals(node.getId()))
            ) {
                ConnectionInterface connection = new JSSETCPConnection(node, message.getHostname(), message.getPort());
                connection.sendMessage(new LookUpAnsMessage(this.node, message.getId()));
//                NetworkLogger.printLog(Level.INFO, "Lookup " + message.getId() + " sent to " + message.getHostname() + ":" + message.getPort());
                return;
            }

            NodeInfo closestPrecedent = this.findClosestPrecedent(lookup_id);
            closestPrecedent.getConnection().sendMessage(message);

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error in lookup operation - " + e.getMessage());
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