package com.network.threads.operations;

import com.network.ChordNode;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.log.NetworkLogger;

import java.util.logging.Level;

public class CheckPredecessor implements Runnable {
    private ChordNode node;

    public CheckPredecessor(ChordNode node) {
        this.node = node;
    }

    @Override
    public void run() {
        NetworkLogger.printLog(Level.INFO, "" + node.getSuccessor());
        NetworkLogger.printLog(Level.INFO, "" + node.getPredecessor());
        final InfoInterface predecessor = this.node.getPredecessor();
        if(predecessor instanceof NodeInfo) {
            if (((NodeInfo) predecessor).getListener().getInternal().isClosed()) {
                node.resetPredecessor();
            }
        }
    }
}
