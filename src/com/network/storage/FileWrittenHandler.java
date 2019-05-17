package com.network.storage;

@FunctionalInterface
public interface FileWrittenHandler {

    void done(boolean success, int bytes_written);

}
