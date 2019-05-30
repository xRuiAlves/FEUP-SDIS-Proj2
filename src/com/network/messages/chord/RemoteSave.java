package com.network.messages.chord;

import com.network.connections.listeners.MessageVisitor;
import com.network.messages.Message;

import java.math.BigInteger;
import java.net.InetAddress;

public class RemoteSave extends Message {
    private BigInteger id;
    private String name;
    private InetAddress hostname;
    private Integer port;

    public RemoteSave(String name, BigInteger id, InetAddress hostname, Integer port){
        this.name = name;
        this.id = id;
        this.hostname = hostname;
        this.port = port;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
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
