package com.network.connections.server;

import com.network.connections.listeners.Listener;

import java.io.IOException;
import java.net.InetAddress;

public interface ServerConnectionInterface {
    InetAddress getIp();

    Integer getPort();

    Listener accept() throws IOException;
}
