package com.network.connections.server;

import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.TCPConnection;
import com.network.utils.IpFinder;
import com.network.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerConnection implements ServerConnectionInterface {

    private final ServerSocket serverSocket;
    private InetAddress ip;
    private Node node;

    public ServerConnection(Node node) throws IOException {
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
    public ConnectionInterface accept() throws IOException {
        return new TCPConnection(node, this.serverSocket.accept());
    }
}
