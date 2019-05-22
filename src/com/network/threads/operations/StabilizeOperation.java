package com.network.threads.operations;


import com.network.ChordNode;
import com.network.connections.client.ConnectionInterface;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.GetPredecessor;
import com.network.threads.ThreadPool;

import java.util.logging.Level;

public class StabilizeOperation implements Runnable{
    private ChordNode node;

    public StabilizeOperation(ChordNode node) {
        this.node = node;
    }


    @Override
    public void run() {

        try {
            if (this.node.getSuccessor() == null) {
//                NetworkLogger.printLog(Level.INFO, "Stabilize not possible");
                return;
            }

            if (this.node.getId().equals(this.node.getSuccessor().getId())) {
                InfoInterface predecessor = this.node.getPredecessor();
                if (predecessor instanceof NullInfo) {
//                    NetworkLogger.printLog(Level.INFO, "Stabilize not possible");
                    return;
                }

                this.node.setSuccessor((NodeInfo) predecessor);

            } else {
                NodeInfo successor = node.getSuccessor();
                ConnectionInterface connection = successor.getListener().getInternal();
                if (connection.isClosed()) {
                    NetworkLogger.printLog(Level.SEVERE, "Successor disconnected");
                    NetworkLogger.printLog(Level.SEVERE, "No fault tolerance implemented - exiting");
                    System.exit(-1);
                }
                ThreadPool.getInstance().submit(new SendMessage(new GetPredecessor(this.node), connection));
            }

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error asking its successor for its predecessor");
        }

    }
}
