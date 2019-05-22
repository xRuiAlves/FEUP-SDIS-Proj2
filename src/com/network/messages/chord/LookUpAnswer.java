package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;

public class LookUpAnswer extends ChordMessage {

    private BigInteger id;

    public LookUpAnswer(ChordNode node, BigInteger id){
        super(node);
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }

    @Override
    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return true;
    }
}
