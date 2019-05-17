package com.network.storage;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface FileReadHandler {

    void done(boolean success, int bytes_read, ByteBuffer data);

}
