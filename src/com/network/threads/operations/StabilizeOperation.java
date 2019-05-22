package com.network.threads.operations;


import com.network.ChordNode;
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

        // TODO: Check if successor failed
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
                ThreadPool.getInstance().submit(new SendMessage(new GetPredecessor(this.node), successor.getListener().getInternal()));
            }

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error asking its successor for its predecessor");
        }

    }
}
