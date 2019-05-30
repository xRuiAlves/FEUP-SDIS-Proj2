package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;

public class Reclaimed extends ChordMessage{
    private byte[] file_data;
    private BigInteger id;
    private String name;

    public Reclaimed(ChordNode node, byte[] file_data, BigInteger id, String name) {
        super(node);
        this.file_data = file_data;
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean accept(MessageVisitor mv) throws Exception {
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
