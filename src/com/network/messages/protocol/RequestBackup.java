package com.network.messages.protocol;

import com.network.connections.listeners.MessageVisitor;
import com.network.messages.Message;

public class RequestBackup extends Message {
    private final long size;

    public RequestBackup(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }
}
