package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;

public class LookUp extends ChordMessage {
    private BigInteger id;

    public LookUp(ChordNode node, BigInteger id){
        super(node);
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }

    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }
}
