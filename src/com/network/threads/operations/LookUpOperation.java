package com.network.threads.operations;

import com.network.ChordNode;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.TCPConnection;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.LookUpMessage;

import java.math.BigInteger;
import java.util.logging.Level;

public class LookUpOperation implements Runnable {
    private final ChordNode node;
    private LookUpMessage message;

    public LookUpOperation(ChordNode node, LookUpMessage message) {
        this.node = node;
        this.message = message;
    }

    public void run() {
        NetworkLogger.printLog(Level.SEVERE, "Implement finger table");
        try {
            BigInteger lookup_id = message.getId();
            if (lookup_id.equals(node.getId())
                    || (node.getPredecessor() != null
                        && (node.getPredecessor().getAccess().compareTo(lookup_id) < 0 && node.getId().compareTo(lookup_id) >= 0))
                    || (node.getSuccessor() == null)
            ) {
                ConnectionInterface connection = new TCPConnection(node, message.getHostname(), message.getPort());
                connection.sendMessage(new LookUpAnsMessage(this.node, message.getId()));
                NetworkLogger.printLog(Level.INFO, "Lookup " + message.getId() + " sent to " + message.getHostname() + ":" + message.getPort());
                return;
            }

            NetworkLogger.printLog(Level.WARNING, "Check if it is in the finger table");
            node.getSuccessor().getConnection().sendMessage(message);

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error in lookup operation - " + e.getMessage());
        }

    }
}