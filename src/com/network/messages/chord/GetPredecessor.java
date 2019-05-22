package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;

public class GetPredecessor extends ChordMessage {

    public GetPredecessor(ChordNode node){
        super(node);
    }

    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }
}
