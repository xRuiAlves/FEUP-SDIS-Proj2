package com.network.info;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NullInfo implements InfoInterface {
    @Override
    public BigInteger getId() {
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

    @Override
    public void startConnection() throws IOException {

    }
}
