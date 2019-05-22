package com.network.connections.server;

import com.network.ChordNode;
import com.network.connections.listeners.Listener;
import com.network.connections.client.TCPConnection;
import com.network.utils.IpFinder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class TCPServerConnection implements ServerConnectionInterface {

    private final ServerSocket serverSocket;
    private InetAddress ip;
    private ChordNode node;

    public TCPServerConnection(ChordNode node) throws IOException {
        this.node = node;
        this.serverSocket = new ServerSocket(0);
        this.ip = IpFinder.findIp();
    }

    @Override
    public InetAddress getIp() {
        return ip;
    }

    @Override
    public Integer getPort() {
        return this.serverSocket.getLocalPort();
    }

    @Override
    public Listener accept() throws IOException {
        return new Listener(node, new TCPConnection(this.serverSocket.accept()));
    }
}
