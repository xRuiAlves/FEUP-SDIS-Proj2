package com.network.info;

import java.io.Serializable;
import java.net.InetAddress;

public class BasicInfo implements Serializable {

    private InetAddress ip;
    private Integer port;

    public BasicInfo(InetAddress ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public String toString() {
        return this.ip + " " + this.port;
    }
}
