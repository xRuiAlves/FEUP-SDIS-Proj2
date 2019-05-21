package com.network.storage.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FileIOExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        readTest();
        writeTest();
    }

    // Note: The lambdas used in the tests below are just an example - ideally they would be shorter and the code refactored to a separate method.
    // Furthermore, the types are optional, but were added for reference

    private static void writeTest() throws IOException, InterruptedException {
        System.out.println("Writing to a file - test");

        ByteBuffer data = ByteBuffer.wrap("This file is even smaller than tiny, but it is a simple test only to file outputting!!!\n\n\tTesting newlines and tabs here too.".getBytes());

        // Reading a test file
        AsyncFileHandler.writeToFile("test_files/test_output.txt", data, (boolean success, int bytes_written) -> {
            if (success) {
                System.out.printf("%d bytes were written successfully!\n", bytes_written);
            } else {
                System.out.println("File writing was not successful.");
            }
        });

        // To ensure the write completes before the program terminates. Will not be an issue later on as the program will be running indefinitely
        Thread.sleep(1000);
    }

    private static void readTest() throws IOException, InterruptedException {
        System.out.println("Reading a file - test");

        // Reading a test file
        AsyncFileHandler.readFile("test_files/tiny.txt", (boolean success, int bytes_read, ByteBuffer file_data) -> {
            if (success) {
                System.out.printf("%d bytes were read successfully!\n", bytes_read);

                if (file_data.position() > 0) {
                    // Buffer is not at the start, needs rewinding (reset position)
                    file_data.rewind();
                }

                // Getting file contents:
                byte[] raw_file_data = new byte[file_data.remaining()];

                file_data.get(raw_file_data, 0, raw_file_data.length);

                // Only for text files!! Just for testing purposes!
                System.out.printf("File contents: %s\n", new String(raw_file_data, 0, raw_file_data.length));
            } else {
                System.out.println("File reading was not successful.");
            }
        });

        // To ensure the read completes before the program terminates. Will not be an issue later on as the program will be running indefinitely
        Thread.sleep(1000);
    }
}
