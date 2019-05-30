package com.network.messages.protocol;

import com.network.connections.listeners.MessageVisitor;
import com.network.messages.Message;

import java.net.InetAddress;

public class Redirect extends Message {

    private InetAddress hostname;
    private Integer port;

    public Redirect(InetAddress hostname, Integer port) {

        this.hostname = hostname;
        this.port = port;
    }

    public InetAddress getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }
}
