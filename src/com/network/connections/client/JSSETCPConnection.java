package com.network.connections.client;

import com.network.messages.Message;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class JSSETCPConnection implements ConnectionInterface {
    private final InetAddress ip;
    private final SSLSocket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Integer port;
    private boolean closed = false;

    public JSSETCPConnection(InetAddress ip, Integer port) throws IOException {
        this.ip = ip;
        this.port = port;

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(ip, port);
        this.socket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_256_GCM_SHA384", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA"});

        this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    public JSSETCPConnection(SSLSocket socket) throws IOException {
        this.ip = socket.getInetAddress();
        this.port = socket.getPort();
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    @Override
    public synchronized void sendMessage(Message message) throws IOException {
        this.outputStream.writeObject(message);
    }

    @Override
    public Message getMessage() throws IOException, ClassNotFoundException {
        return (Message) this.inputStream.readObject();
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }

    @Override
    public InetAddress getIp() {
        return ip;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public boolean isClosed() {
        return this.closed || this.socket.isClosed() || !this.socket.isConnected();
    }

    @Override
    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
