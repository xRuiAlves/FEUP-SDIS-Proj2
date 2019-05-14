package com.network.connections.server;

import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.TCPConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface ServerConnectionInterface {
    InetAddress getIp();

    Integer getPort();

    ConnectionInterface accept() throws IOException;
}
