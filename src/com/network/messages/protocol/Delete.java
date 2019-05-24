package com.network.messages.protocol;

import com.network.connections.listeners.MessageVisitor;
import com.network.messages.Message;

import java.math.BigInteger;

public class Delete extends Message {
    private final BigInteger id;

    public Delete(BigInteger id) {
        this.id = id;
    }

    @Override
    public boolean accept(MessageVisitor mv) {
        mv.visit(this);
        return false;
    }

    public BigInteger getId() {
        return id;
    }
}
