package com.app;

import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.info.BasicInfo;
import com.network.messages.protocol.Delete;
import com.network.rmi.NodeRMIInterface;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class DeleteProtocol {
    public static void start(NodeRMIInterface rmiInterface, String file_path) {
        final String file_name = new File(file_path).getName();

        // TODO Discuss: If several are replicated in the same node, should the requests be sent? It might be the result of redistribution
        for (int i = 0; i < ProtocolDefinitions.REPLICATION_DEGREE; ++i) {
            BigInteger id = ProtocolDefinitions.fileToIdWithReplication(file_name, i);

            try {
                BasicInfo basicInfo = rmiInterface.lookup(id);
                System.out.println(basicInfo); // TODO Delete

                ConnectionInterface connection = new JSSETCPConnection(basicInfo.getIp(), basicInfo.getPort());
                connection.sendMessage(new Delete(id));
                connection.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
