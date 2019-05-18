package com.network.connections.server;

import com.network.ChordNode;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.utils.IpFinder;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

public class JSSEServerConnection implements ServerConnectionInterface {
    private final SSLServerSocket serverSocket;
    private InetAddress ip;
    private ChordNode node;

    public JSSEServerConnection(ChordNode node) throws IOException {
        this.node = node;
        this.ip = IpFinder.findIp();
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        // TODO: CHANGE WAY OF GETTING PORT
        Random rand = new Random();
        int port = rand.nextInt(50000) + 10000;

        this.serverSocket = (SSLServerSocket) factory.createServerSocket(port);
    }

    @Override
    public InetAddress getIp() {
        return this.ip;
    }

    @Override
    public Integer getPort() {
        return this.serverSocket.getLocalPort();
    }

    @Override
    public ConnectionInterface accept() throws IOException {
        return new JSSETCPConnection(node, (SSLSocket) this.serverSocket.accept());
    }
}