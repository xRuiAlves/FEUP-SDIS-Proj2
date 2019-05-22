package com.network.messages.protocol;

import com.network.messages.Message;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;

public class RequestBackup extends Message {
    private final float size;
    private final BigInteger id;
    private final String name;

    public RequestBackup(float size, BigInteger id, String name) {
        this.size = size;
        this.id = id;
        this.name = name;
    }

    public float getSize() {
        return size;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }
}
