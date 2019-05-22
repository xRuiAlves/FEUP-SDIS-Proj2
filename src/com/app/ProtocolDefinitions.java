package com.app;

import com.network.utils.IdEncoder;

import java.math.BigInteger;

public class ProtocolDefinitions {
    public static final int REPLICATION_DEGREE = 3;
    private static final String CHK_PREFIX = "_chk";
    public static final int TIMEOUT = 2500;


    public static BigInteger fileToIdWithReplication(String file_path, int replication_index) {
        return IdEncoder.encode(file_path + ProtocolDefinitions.CHK_PREFIX + replication_index);
    }
}
