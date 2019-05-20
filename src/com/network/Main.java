package com.network;

import com.network.log.NetworkLogger;
import com.network.messages.Message;
import com.network.rmi.NodeRMI;
import com.network.rmi.NodeRMIInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

public class Main {

    public static final String rmiPrefix = "node-";

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java Main <start_op> [ <ip> <port> ]");
            System.exit(-1);
        }

        ChordNode node = null;
        switch (args[0]) {
            case "create":
                node = new ChordNode();
                break;
            case "join":
                if (args.length < 3) {
                    System.err.println("Usage: java join <start_op> <ip> <port>");
                    System.exit(-1);
                }
                InetAddress host = InetAddress.getByName(args[1]);
                Integer port = Integer.valueOf(args[2]);
                node = new ChordNode(host, port);
                break;
            default:
                System.err.println("Operation not supported");
                System.exit(-1);
        }

        Main.establishRMI(node);

    }

    private static void establishRMI(ChordNode node) {
        NodeRMI reBackup = new NodeRMI();
        NodeRMIInterface stub;
        try {
            stub = (NodeRMIInterface) UnicastRemoteObject.exportObject(reBackup, 0);
            Registry reg;
            try {
                reg = LocateRegistry.getRegistry();
                reg.rebind(rmiPrefix + node.getId(), stub);
            } catch (RemoteException e) {
                NetworkLogger.printLog(Level.INFO, "Tries to create Registry");
                reg = LocateRegistry.createRegistry(1099);
                reg.rebind(rmiPrefix + node.getId(), stub);
            }

            NetworkLogger.printLog(Level.INFO, "RMI established " + rmiPrefix + node.getId());
        } catch (RemoteException e) {
            NetworkLogger.printLog(Level.SEVERE, "Failed establishing RMI - " + e.getMessage());
        }
    }
}