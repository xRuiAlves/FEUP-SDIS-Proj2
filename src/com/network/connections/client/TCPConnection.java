package com.network.connections.client;

import com.network.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection implements ConnectionInterface {

    private final InetAddress ip;
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final Integer port;

    public TCPConnection(InetAddress ip, Integer port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socket = new Socket(ip, port);
        this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    public TCPConnection(Socket socket) throws IOException {
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
    public boolean isClosed() {
        return this.socket.isClosed() || !this.socket.isConnected();
    }
}
