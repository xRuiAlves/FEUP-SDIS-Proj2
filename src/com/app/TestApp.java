package com.app;

import com.network.info.BasicInfo;
import com.network.rmi.NodeRMIInterface;

import java.math.BigInteger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CountDownLatch;

public class TestApp {
    public static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: <peer-id> <operation> <filename>");
            System.err.println("Supported operations: BACKUP, RESTORE, DELETE");
            System.exit(-1);
        }

        String peer_p = args[0];
        String operation = args[1].toUpperCase();
        String filename = args[2];

        try {
            Registry reg = LocateRegistry.getRegistry();
            NodeRMIInterface rmiInterface = (NodeRMIInterface) reg.lookup(peer_p);
            switch (operation) {
                case "LOOKUP":
                    BasicInfo id = rmiInterface.lookup(BigInteger.ONE);
                    System.out.println(id);
                    break;

                case "BACKUP":
                    BackupProtocol.start(rmiInterface, filename);
                    latch.await();
                    break;

                case "RESTORE":
                    RestoreProtocol.start(rmiInterface, filename);
                    break;

                case "DELETE":
                    DeleteProtocol.start(rmiInterface, filename);
                    break;

                default:
                    System.err.println("Unsupported operation: " + operation);
                    System.err.println("Supported operations: BACKUP, RESTORE, DELETE");
                    System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
