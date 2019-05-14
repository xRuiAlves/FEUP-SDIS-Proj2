package com.network.messages;


import com.network.Node;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;

public abstract class Message implements Serializable {
    protected BigInteger senderId;
    protected InetAddress hostname;
    protected Integer port;


    Message(BigInteger senderId, InetAddress hostname, Integer port) {
        this.senderId = senderId;
        this.hostname = hostname;
        this.port = port;
    }

    Message(Node node) {
        this.senderId = node.getId();
        this.hostname = node.getServer().getServerConnection().getIp();
        this.port = node.getServer().getServerConnection().getPort();
    }

    public BigInteger getSenderId() {
        return senderId;
    }

    public Integer getPort() {
        return port;
    }

    public InetAddress getHostname() {
        return hostname;
    }
}
