package com.network.threads.operations;


import com.network.ChordNode;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;
import com.network.messages.GetPredecessor;

import java.util.logging.Level;

public class StabilizeOperation implements Runnable{
    private ChordNode node;

    public StabilizeOperation(ChordNode node) {
        this.node = node;
    }


    @Override
    public void run() {
        try {
            NodeInfo successor = node.getSuccessor();
            if (successor != null) {
                successor.getConnection().sendMessage(new GetPredecessor(this.node));
                NetworkLogger.printLog(Level.INFO, "Starting stabilization algorithm");
            }
        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error asking its successor for its predecessor");
        }

    }
}
