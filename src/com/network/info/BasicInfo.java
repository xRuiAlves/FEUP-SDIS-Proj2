package com.network.info;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicInfo basicInfo = (BasicInfo) o;
        return Objects.equals(ip, basicInfo.ip) &&
                Objects.equals(port, basicInfo.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
