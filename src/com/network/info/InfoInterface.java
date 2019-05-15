package com.network.info;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface InfoInterface {
    BigInteger getAccess();

    InetAddress getIp() throws UnknownHostException;

    Integer getPort();
}
