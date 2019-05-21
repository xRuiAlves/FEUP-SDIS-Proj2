package com.network.messages;

import com.network.ChordNode;

import java.math.BigInteger;

public class LookUpAnsMessage extends Message {

    private BigInteger id;

    public LookUpAnsMessage(ChordNode node, BigInteger id){
        super(node);
        this.id = id;
    }

    protected LookUpAnsMessage() {
    }

    public BigInteger getId() {
        return id;
    }
}
