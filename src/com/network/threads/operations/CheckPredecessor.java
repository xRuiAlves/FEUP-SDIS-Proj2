package com.network.threads.operations;

import com.network.ChordNode;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;


public class CheckPredecessor implements Runnable {
    private ChordNode node;

    public CheckPredecessor(ChordNode node) {
        this.node = node;
    }

    @Override
    public void run() {
        final InfoInterface predecessor = this.node.getPredecessor();
        if(predecessor instanceof NodeInfo) {
            if (((NodeInfo) predecessor).getListener().getInternal().isClosed()) {
                node.resetPredecessor();
            }
        }
    }
}
