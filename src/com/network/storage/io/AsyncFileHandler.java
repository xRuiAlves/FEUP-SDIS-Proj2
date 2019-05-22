package com.network.storage.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.StandardOpenOption;

public class AsyncFileHandler {

    private static class IOReadHandlerWrapper implements CompletionHandler<Integer, Object> {
        private final FileReadHandler handler;
        private final ByteBuffer file_data;
        private final AsynchronousFileChannel file_channel;

        public IOReadHandlerWrapper(FileReadHandler handler, ByteBuffer file_data, AsynchronousFileChannel file_channel) {
            this.handler = handler;
            this.file_data = file_data;
            this.file_channel = file_channel;
        }

        @Override
        public void completed(Integer bytes_read, Object ignored) {
            if (this.file_data.position() > 0) {
                this.file_data.rewind();
            }
            this.handler.done(true, bytes_read, this.file_data);
            try {
                this.file_channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable throwable, Object ignored) {
            throwable.printStackTrace();

            this.handler.done(false, 0, this.file_data);

            try {
                this.file_channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class IOWriteHandlerWrapper implements CompletionHandler<Integer, Object> {
        private final FileWrittenHandler handler;
        private final AsynchronousFileChannel file_channel;

        public IOWriteHandlerWrapper(FileWrittenHandler handler, AsynchronousFileChannel file_channel) {
            this.handler = handler;
            this.file_channel = file_channel;
        }

        @Override
        public void completed(Integer bytes_written, Object ignored) {
            this.handler.done(true, bytes_written);
            try {
                this.file_channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable throwable, Object ignored) {
            throwable.printStackTrace();

            this.handler.done(false, 0);

            try {
                this.file_channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readFile(String path, FileReadHandler completion_handler) throws IOException {
        // Cannot use try-with-resources since the channel would be closed before the async read operation was done.
        // The file channel must be passed to the completion handler, where it is closed.
        AsynchronousFileChannel file_channel = AsynchronousFileChannel.open(new File(path).toPath(), StandardOpenOption.READ);
        long file_size = file_channel.size();

        final ByteBuffer file_buffer = ByteBuffer.allocate((int) file_size);
        file_channel.read(file_buffer, 0, null, new IOReadHandlerWrapper(completion_handler, file_buffer, file_channel));
    }

    public static void writeToFile(String path, ByteBuffer data_to_write, FileWrittenHandler completion_handler) throws IOException {
        writeToFile(path, 0, data_to_write, completion_handler);
    }

    public static void writeToFile(String path, long start_position, ByteBuffer data_to_write, FileWrittenHandler completion_handler) throws IOException {
        // Cannot use try-with-resources since the channel would be closed before the async read operation was done.
        // The file channel must be passed to the completion handler, where it is closed.
        AsynchronousFileChannel file_channel = AsynchronousFileChannel.open(new File(path).toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);


        file_channel.write(data_to_write, start_position, null, new IOWriteHandlerWrapper(completion_handler, file_channel));
    }
}
