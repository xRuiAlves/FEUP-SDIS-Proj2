package com.app;

import com.network.utils.IdEncoder;

import java.io.File;
import java.math.BigInteger;

public class ProtocolDefinitions {
    public static final int REPLICATION_DEGREE = 3;
    private static final String CHK_PREFIX = "_chk";
    public static final String ROOT_FOLDER = "restored";


    public static BigInteger fileToIdWithReplication(String file_path, int replication_index) {
        return IdEncoder.encode(file_path + ProtocolDefinitions.CHK_PREFIX + replication_index);
    }

    public static void buildParentFolder() {
        File folder = new File(ROOT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
