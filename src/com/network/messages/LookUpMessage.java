package com.network.messages;

import com.network.ChordNode;

import java.math.BigInteger;

public class LookUpMessage extends Message {
    private BigInteger id;

    public LookUpMessage(ChordNode node, BigInteger id){
        super(node);
        this.id = id;
    }

    protected LookUpMessage() {
    }

    public BigInteger getId() {
        return id;
    }
}
