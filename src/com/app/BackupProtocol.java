package com.app;

import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.info.BasicInfo;
import com.network.messages.Message;
import com.network.messages.protocol.Backup;
import com.network.messages.protocol.No;
import com.network.messages.protocol.RequestBackup;
import com.network.messages.protocol.Yes;
import com.network.rmi.NodeRMIInterface;
import com.network.storage.io.AsyncFileHandler;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;

public class BackupProtocol {


    public static void start(NodeRMIInterface rmiInterface, String file_path) throws IOException {

        AsyncFileHandler.readFile(file_path, ((success, bytes_read, data) -> {
            if (!success) {
                System.out.printf("Could not read file with path %s!\n", file_path);
                return;
            }

            final String file_name = new File(file_path).getName();
            byte[] file_data = new byte[data.remaining()];
            data.get(file_data, 0, file_data.length);

            System.out.println(file_data.length);
            int n_successful = 0;
            HashSet<BasicInfo> hosts = new HashSet<>();
            for (int i = 0; i < ProtocolDefinitions.REPLICATION_DEGREE; ++i) {
                BigInteger id = ProtocolDefinitions.fileToIdWithReplication(file_name, i);

                try {
                    BasicInfo basicInfo = rmiInterface.lookup(id);
                    if(hosts.contains(basicInfo)) {
                        continue;
                    }
                    hosts.add(basicInfo);
                    System.out.println(basicInfo); // TODO Delete

                    if (attemptToSendFile(basicInfo, id, file_name, file_data)) {
                        n_successful++;
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("Success in backing up file %s with a replication degree of %d\n", file_name, n_successful);
            TestApp.latch.countDown();
        }));
    }

    private static boolean attemptToSendFile(BasicInfo basicInfo, BigInteger id, String file_name, byte[] file_data) throws IOException {
        ConnectionInterface connection = new JSSETCPConnection(basicInfo.getIp(), basicInfo.getPort());
        connection.sendMessage(new RequestBackup(file_data.length));

        boolean successful = false;

        try {
            Message msg = connection.getMessage();
            if (isResponseAffirmative(msg)) {
                connection.sendMessage(new Backup(file_data, id, file_name));

                Message backup_reply = connection.getMessage();
                if (isResponseAffirmative(backup_reply)) {
                    System.out.printf("Success in backing up file %s - id %s!\n", file_name, id);
                    successful = true;
                } else {
                    System.out.printf("Failure in backing up file %s - id %s!\n", file_name, id);
                }
            } else {
                System.out.printf("Failure in backing up file %s - id %s!\n", file_name, id);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        connection.close();
        return successful;
    }

    private static boolean isResponseAffirmative(Message msg) {
        if (msg instanceof Yes) {
            return true;
        } else if (msg instanceof No) {
            return false;
        } else {
            System.out.println("Unexpected message");
            return false;
        }
    }
}
