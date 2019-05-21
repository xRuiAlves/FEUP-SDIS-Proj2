package com.network.storage.io;

@FunctionalInterface
public interface FileWrittenHandler {

    void done(boolean success, int bytes_written);

}
