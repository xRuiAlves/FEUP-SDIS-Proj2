package com.network.info;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface InfoInterface {
    BigInteger getId();

    InetAddress getIp() throws UnknownHostException;

    Integer getPort();

    void startConnection() throws IOException;
}
