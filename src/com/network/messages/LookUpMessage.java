package com.network.messages;

import com.network.Node;

import java.math.BigInteger;

public class LookUpMessage extends Message {
    private BigInteger id;

    public LookUpMessage(Node node, BigInteger id){
        super(node);
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }
}
