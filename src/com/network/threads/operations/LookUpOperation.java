package com.network.threads.operations;

import com.network.ChordNode;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.TCPConnection;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.LookUpMessage;

import java.math.BigInteger;
import java.util.Map;
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
                ConnectionInterface connection = new TCPConnection(node, message.getHostname(), message.getPort());
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
        NodeInfo next = this.node.getSuccessor();

        if (n.equals(next.getId()) || this.inNode(lookup_id, n, next.getId())) {
            return next;
        }
        for (Map.Entry<BigInteger, InfoInterface> entry : this.node.getFingerTable().entrySet()) {
            if (entry.getValue() instanceof NullInfo) {
                NetworkLogger.printLog(Level.WARNING, "Finger table incomplete");
                return next;
            }

            next = (NodeInfo) entry.getValue();
            if (this.inNode(entry.getKey(), n, lookup_id)) {
                return next;
            }
        }
        return next;
    }


    private boolean inNode(BigInteger id, BigInteger lower, BigInteger upper) {
        if (lower.compareTo(upper) > 0) {
            return (id.compareTo(lower) > 0 || id.compareTo(upper) < 0);
        } else {
            return id.compareTo(lower) > 0 && id.compareTo(upper) < 0;
        }
    }
}