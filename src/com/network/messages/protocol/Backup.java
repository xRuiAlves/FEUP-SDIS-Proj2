package com.network.messages.protocol;

import com.network.messages.Message;
import com.network.connections.listeners.MessageVisitor;

import java.io.IOException;
import java.math.BigInteger;

public class Backup extends Message {
    private final byte[] file_data;
    private final BigInteger id;
    private final String name;

    public Backup(byte[] file_data, BigInteger id, String name) {
        this.file_data = file_data;
        this.id = id;
        this.name = name;
    }

    public boolean accept(MessageVisitor mv) throws IOException {
        mv.visit(this);
        return false;
    }

    public byte[] getFileData() {
        return file_data;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
