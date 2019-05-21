package com.network.messages;

import com.network.ChordNode;

import java.math.BigInteger;

public class GetPredecessor extends Message{

    public GetPredecessor(ChordNode node){
        super(node);
    }

    protected GetPredecessor() {
    }
}
