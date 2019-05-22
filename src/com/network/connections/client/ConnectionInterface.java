package com.network.connections.client;

import com.network.messages.Message;

import java.io.IOException;
import java.net.InetAddress;

public interface ConnectionInterface {

    void sendMessage(Message message) throws IOException;

    Message getMessage() throws IOException, ClassNotFoundException;

    void close() throws IOException;

    InetAddress getIp();

    Integer getPort();

    boolean isClosed();

    void setClosed(boolean b);
}
