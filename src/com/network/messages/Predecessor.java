package com.network.messages;

import com.network.ChordNode;

import java.math.BigInteger;
import java.net.UnknownHostException;

public class Predecessor extends Message{
    private BigInteger id;

    public Predecessor(ChordNode node) throws UnknownHostException {
        super(node.getId(), node.getPredecessor().getIp(), node.getPredecessor().getPort());
        this.id = node.getPredecessor().getId();
    }

    protected Predecessor() {
    }

    public BigInteger getId() {
        return id;
    }
}
