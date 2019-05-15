package com.network.threads.operations;

import com.network.ChordNode;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpMessage;
import com.network.threads.ThreadPool;

import java.math.BigInteger;
import java.util.logging.Level;

import static com.network.ChordNode.m;

public class UpdateFingers implements Runnable {

    private final ChordNode node;
    private final BigInteger two;
    private int next;

    public UpdateFingers(ChordNode node) {
        this.node = node;
        this.next = 1;
        this.two = new BigInteger("2");

    }

    @Override
    public void run() {
        NetworkLogger.printLog(Level.WARNING, "Try to update fingers");
        if (node.getSuccessor() != null && !node.getSuccessor().getId().equals(node.getId())) {
            BigInteger fingerId = (this.node.getId().add(two.pow(next))).mod(two.pow(m));

            ThreadPool.getInstance().submit(new LookUpOperation(node, new LookUpMessage(node, fingerId)));
            this.next += 1;
            if (this.next > m) {
                this.next = 1;
            }
        }

    }
}
