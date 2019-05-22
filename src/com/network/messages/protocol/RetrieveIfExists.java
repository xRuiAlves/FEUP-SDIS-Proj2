package com.network.messages.protocol;

import com.network.messages.Message;
import com.network.connections.listeners.MessageVisitor;

public class RetrieveIfExists extends Message {
    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }
}
