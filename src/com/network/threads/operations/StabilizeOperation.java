package com.network.threads.operations;


import com.network.ChordNode;
import com.network.connections.client.ConnectionInterface;
import com.network.info.BasicInfo;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.GetPredecessor;
import com.network.threads.ThreadPool;

import java.util.concurrent.ConcurrentLinkedQueue;
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
                return;
            }

            if (this.node.getId().equals(this.node.getSuccessor().getId())) {
                InfoInterface predecessor = this.node.getPredecessor();
                if (predecessor instanceof NullInfo) {
                    return;
                }

                this.node.setSuccessor((NodeInfo) predecessor, new ConcurrentLinkedQueue<BasicInfo>());

            } else {
                NodeInfo successor = node.getSuccessor();
                ConnectionInterface connection = successor.getListener().getInternal();
                if (connection.isClosed()) {
                    NetworkLogger.printLog(Level.SEVERE, "Successor disconnected");
                    this.node.advanceSuccessor();
                }
                ThreadPool.getInstance().submit(new SendMessage(new GetPredecessor(this.node), connection));
            }

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error asking its successor for its predecessor");
            e.printStackTrace();
        }

    }
}
