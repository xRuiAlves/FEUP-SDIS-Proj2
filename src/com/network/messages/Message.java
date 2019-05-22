package com.network.messages;

import com.network.connections.listeners.MessageVisitor;

import java.io.Serializable;

public abstract class Message implements Serializable {
    /**
     * Accepts a visitor that will visit the message
     * @param mv The visitor that will visit this Message
     * @return Should terminate the connection
     */
    public abstract boolean accept(MessageVisitor mv) throws Exception;
}
