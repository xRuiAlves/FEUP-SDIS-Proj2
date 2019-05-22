package com.network.messages.protocol;

import com.network.connections.listeners.MessageVisitor;
import com.network.messages.Message;

public class Retrieved  extends Message {

    private byte[] file_data;

    public Retrieved(byte[] file_data) {

        this.file_data = file_data;
    }

    @Override
    public boolean accept(MessageVisitor mv) throws Exception {
        mv.visit(this);
        return false;
    }

    public byte[] getFileData() {
        return file_data;
    }
}
