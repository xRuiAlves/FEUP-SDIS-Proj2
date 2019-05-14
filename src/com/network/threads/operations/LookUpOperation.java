package com.network.threads.operations;

import com.network.Node;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpMessage;

import java.math.BigInteger;
import java.util.logging.Level;

public class LookUpOperation implements Runnable {
    private final Node node;
    private LookUpMessage message;

    public LookUpOperation(Node node, LookUpMessage message) {
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
                NetworkLogger.printLog(Level.SEVERE, "Implement look up reply");
                return;
            }

            NetworkLogger.printLog(Level.WARNING, "Check if it is in the finger table");
            node.getSuccessor().getConnection().sendMessage(message);

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error in lookup operation - " + e.getMessage());
        }

    }
}