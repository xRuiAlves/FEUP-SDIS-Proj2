package com.network.messages.chord;


import com.network.ChordNode;
import com.network.messages.Message;

import java.math.BigInteger;
import java.net.InetAddress;

public abstract class ChordMessage extends Message {
    protected BigInteger senderId;
    protected InetAddress hostname;
    protected Integer port;

    ChordMessage(BigInteger senderId, InetAddress hostname, Integer port) {
        this.senderId = senderId;
        this.hostname = hostname;
        this.port = port;
    }

    ChordMessage(ChordNode node) {
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
