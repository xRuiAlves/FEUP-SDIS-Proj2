package com.network.connections.server;

import com.network.connections.client.Connection;

import java.io.IOException;
import java.net.InetAddress;

public interface ServerConnectionInterface {
    InetAddress getIp();

    Integer getPort();

    Connection accept() throws IOException;
}
