package com.app;

import com.network.info.NodeInfo;
import com.network.rmi.NodeRMIInterface;

import java.math.BigInteger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: <peer-id> <operation>");
            System.exit(-1);
        }

        String peer_p = args[0];
        String operation = args[1].toUpperCase();

        try {
            Registry reg = LocateRegistry.getRegistry("localhost");
            NodeRMIInterface rmiInterface = (NodeRMIInterface) reg.lookup(peer_p);
            switch (operation) {
                case "LOOKUP":
                    NodeInfo id = rmiInterface.lookup(BigInteger.ONE);
                    System.out.println(id);
                    break;

                default:
                    System.err.println("Unsupported operation: " + operation);
                    System.exit(-1);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
