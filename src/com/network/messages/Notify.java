package com.network.messages;

import com.network.ChordNode;

import java.math.BigInteger;

public class Notify extends Message {
    private BigInteger id;

    public Notify(ChordNode node, BigInteger id){
        super(node);
        this.id = id;
    }

    protected Notify() {
    }

    public BigInteger getId() {
        return id;
    }
}
