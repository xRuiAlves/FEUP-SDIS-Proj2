package com.network.storage.io;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface FileReadHandler {

    void done(boolean success, int bytes_read, ByteBuffer data);

}
