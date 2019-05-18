package com.network.connections.client;

import com.network.ChordNode;
import com.network.messages.Message;
import com.network.threads.ThreadPool;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class JSSETCPConnection extends Connection {

    private final InetAddress ip;
    private final SSLSocket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Integer port;

    public JSSETCPConnection(ChordNode node, InetAddress ip, Integer port) throws IOException {
        super(node);
        this.ip = ip;
        this.port = port;

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(ip, port);

        this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    public JSSETCPConnection(ChordNode node, SSLSocket socket) throws IOException {
        super(node);
        this.ip = socket.getInetAddress();
        this.port = socket.getPort();
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    @Override
    public void sendMessage(Message message) throws IOException {
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
    public void start() {
        ThreadPool.getInstance().submit(this);
    }

    @Override
    public boolean isClosed() {
        return this.socket.isClosed() || !this.socket.isConnected();
    }
}
