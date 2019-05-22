package com.network.messages.protocol;

import com.network.messages.Message;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;

public class RetrieveIfExists extends Message {
    private BigInteger id;

    public RetrieveIfExists(BigInteger id) {
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
