package com.network.info;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NullInfo implements InfoInterface {
    @Override
    public BigInteger getAccess() {
        return BigInteger.ONE.negate();
    }

    @Override
    public InetAddress getIp() throws UnknownHostException {
        return InetAddress.getByName("0.0.0.0");
    }

    @Override
    public Integer getPort() {
        return 0;
    }
}
