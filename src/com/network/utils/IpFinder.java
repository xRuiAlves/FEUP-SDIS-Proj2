package com.network.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IpFinder {

    public static InetAddress findIp() throws UnknownHostException {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return InetAddress.getByName(socket.getLocalAddress().getHostAddress());
        } catch (Exception e) {
            return InetAddress.getByName("0.0.0.0");
        }
    }
}
