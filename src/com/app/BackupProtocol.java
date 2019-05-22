package com.app;

import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.info.BasicInfo;
import com.network.messages.protocol.RequestBackup;
import com.network.rmi.NodeRMIInterface;
import com.network.storage.io.AsyncFileHandler;
import com.network.utils.IdEncoder;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;

public class BackupProtocol {

    public static void start(NodeRMIInterface rmiInterface, String file_path) throws IOException {
        HashSet<BigInteger> replication_degrees = new HashSet<>();

        for (int i = 0; i < ProtocolDefinitions.REPLICATION_DEGREE; ++i) {
            replication_degrees.add(ProtocolDefinitions.fileToIdWithReplication(file_path, i));
        }

        AsyncFileHandler.readFile(file_path, ((success, bytes_read, data) -> {
            if (!success) {
                System.out.printf("Could not read file with path %s!\n", file_path);
                return;
            }

            final String file_name = new File(file_path).getName();
            byte[] file_data = new byte[data.remaining()];
            data.get(file_data, 0, file_data.length);

            for (BigInteger id : replication_degrees) {
                try {
                    BasicInfo basicInfo = rmiInterface.lookup(id);
                    System.out.println(basicInfo); // TODO Delete

                    sendFile(basicInfo, id, file_name, file_data);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private static void sendFile(BasicInfo basicInfo, BigInteger id, String file_name, byte[] file_data) throws IOException {
        ConnectionInterface connection = new JSSETCPConnection(basicInfo.getIp(), basicInfo.getPort());
        connection.sendMessage(new RequestBackup(file_data.length, id, file_name));


    }
}
