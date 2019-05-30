package com.network.messages.chord;

import com.network.ChordNode;
import com.network.connections.listeners.MessageVisitor;

import java.math.BigInteger;
import java.util.ArrayList;

public class Reclaimed extends ChordMessage{
    private byte[] file_data;
    private BigInteger id;
    private String name;
    private ArrayList<BigInteger> visited = new ArrayList<>();

    public Reclaimed(ChordNode node, byte[] file_data, BigInteger id, String name) {
        super(node);
        this.file_data = file_data;
        this.id = id;
        this.name = name;
    }

    public void visited(BigInteger v) {
        visited.add(v);
    }

    public boolean alreadyVisited(BigInteger id) {
        return this.visited.contains(id);
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
