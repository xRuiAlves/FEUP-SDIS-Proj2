package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;
import java.net.UnknownHostException;

public class Predecessor extends ChordMessage {
    private BigInteger id;

    public Predecessor(ChordNode node) throws UnknownHostException {
        super(node.getId(), node.getPredecessor().getIp(), node.getPredecessor().getPort());
        this.id = node.getPredecessor().getId();
    }

    public BigInteger getId() {
        return id;
    }

    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }
}
