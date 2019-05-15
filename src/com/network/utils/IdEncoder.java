package com.network.utils;

import com.network.ChordNode;
import com.network.log.NetworkLogger;

import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public class IdEncoder {

    private static IdEncoder ourInstance = new IdEncoder();
    private final BigInteger m;

    private IdEncoder() {
        this.m = new BigInteger("2").pow(ChordNode.m);
    }

    public static BigInteger encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, hash).mod(ourInstance.m);
        } catch (NoSuchAlgorithmException e) {
            NetworkLogger.printLog(Level.SEVERE, "Encoding algorithm not found, using unencrypted data");
            return BigInteger.ZERO;
        }
    }

    public static BigInteger encode(InetAddress hostname, Integer port) {
        return encode(hostname + ":" + port);
    }

}
