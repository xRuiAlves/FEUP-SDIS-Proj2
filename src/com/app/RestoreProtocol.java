package com.app;

import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.info.BasicInfo;
import com.network.messages.Message;
import com.network.messages.protocol.Backup;
import com.network.messages.protocol.No;
import com.network.messages.protocol.RetrieveIfExists;
import com.network.messages.protocol.Retrieved;
import com.network.rmi.NodeRMIInterface;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;

public class RestoreProtocol {

    public static void start(NodeRMIInterface rmiInterface, String file_name) throws IOException {
        for (int i = 0; i < ProtocolDefinitions.REPLICATION_DEGREE; ++i) {
            BigInteger id = ProtocolDefinitions.fileToIdWithReplication(file_name, i);

            try {
                BasicInfo basicInfo = rmiInterface.lookup(id);
                System.out.println(basicInfo); // TODO Delete

                if (attemptRestore(basicInfo, id, file_name)) {
                   break;
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean attemptRestore(BasicInfo basicInfo, BigInteger id, String file_name) throws IOException {
        ConnectionInterface connection = new JSSETCPConnection(basicInfo.getIp(), basicInfo.getPort());
        connection.sendMessage(new RetrieveIfExists(id));

        boolean successful = false;
        try {
            Message msg = connection.getMessage();
            if (isResponseAffirmative(msg)) {
                Retrieved retrieved = (Retrieved) msg;
                successful = true;
                RandomAccessFile file = new RandomAccessFile(file_name, "rw");
                file.write(retrieved.getFileData());
            } else {
                System.out.printf("Failure in restoring up file %s - id %s!\n", file_name, id);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection.close();
        return successful;
    }


    private static boolean isResponseAffirmative(Message msg) {
        if (msg instanceof Retrieved) {
            return true;
        } else if (msg instanceof No) {
            return false;
        } else {
            System.out.println("Unexpected message");
            return false;
        }
    }
}
