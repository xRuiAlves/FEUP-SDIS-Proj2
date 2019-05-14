package com.network.messages;

import com.network.Node;

import java.math.BigInteger;

public class LookUpAnsMessage extends Message {

    private BigInteger id;

    public LookUpAnsMessage(Node node, BigInteger id){
        super(node);
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }
}
