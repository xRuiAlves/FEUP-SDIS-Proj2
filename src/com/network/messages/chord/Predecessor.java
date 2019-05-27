package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;
import com.network.info.BasicInfo;
import com.network.info.NodeInfo;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Predecessor extends ChordMessage {
    private BigInteger id;
    private ConcurrentLinkedQueue<BasicInfo> successors;

    public Predecessor(ChordNode node, ConcurrentLinkedQueue<BasicInfo> successors) throws UnknownHostException {
        super(node.getId(), node.getPredecessor().getIp(), node.getPredecessor().getPort());
        this.id = node.getPredecessor().getId();
        this.successors = successors;
    }

    public BigInteger getId() {
        return id;
    }

    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }

    public ConcurrentLinkedQueue<BasicInfo> getSuccessors() {
        return successors;
    }
}
