package com.app;

import com.network.connections.client.JSSETCPConnection;
import com.network.info.BasicInfo;
import com.network.rmi.NodeRMIInterface;
import com.network.utils.IdEncoder;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashSet;

public class BackupProtocol {
    public static void start(NodeRMIInterface rmiInterface, String file_path) throws RemoteException, InterruptedException {
        HashSet<BigInteger> replication_degrees = new HashSet<>();

        for (int i = 0; i < ProtocolDefinitions.REPLICATION_DEGREE; ++i) {
            replication_degrees.add(IdEncoder.encode(file_path + "_chk" + i));
        }

        for (BigInteger id : replication_degrees) {
            BasicInfo basicInfo = rmiInterface.lookup(id);

            System.out.println(basicInfo); // TODO Delete
            sendFile(basicInfo);
        }
    }

    private static void sendFile(BasicInfo basicInfo) {
        JSSETCPConnection connection =
    }
}
