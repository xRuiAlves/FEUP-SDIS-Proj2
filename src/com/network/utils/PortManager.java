package com.network.utils;

import java.io.IOException;
import java.net.ServerSocket;

public final class PortManager {
    public static int getAvailablePort() throws IOException {
        ServerSocket s = new ServerSocket(0);
        final int port_number = s.getLocalPort();
        s.close();

        return port_number;
    }
}
