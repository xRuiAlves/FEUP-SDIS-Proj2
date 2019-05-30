package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;

public class SaveReclaimed extends ChordMessage {
    private BigInteger id;
    private String name;

    public SaveReclaimed(ChordNode node, String name, BigInteger id){
        super(node);
        this.name = name;
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return true;
    }
}
