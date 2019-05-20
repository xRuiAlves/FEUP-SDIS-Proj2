package com.network.rmi;

import com.network.ChordNode;
import com.network.connections.ConnectionHandler;
import com.network.info.BasicInfo;
import com.network.messages.LookUpMessage;
import com.network.subscriptions.LookUpHandler;
import com.network.threads.ThreadPool;
import com.network.threads.operations.LookUpOperation;

import java.math.BigInteger;

public class NodeRMI implements NodeRMIInterface {
    private ChordNode node;

    public NodeRMI(ChordNode node) {
        this.node = node;
    }

    @Override
    public BasicInfo lookup(BigInteger lookup_id) throws InterruptedException {
        LookUpHandler handler = new LookUpHandler();
        ConnectionHandler.getInstance().subscribeLookUp(lookup_id, handler);
        ThreadPool.getInstance().submit(new LookUpOperation(this.node, new LookUpMessage(this.node, lookup_id)));
        handler.await();
        return handler.getInfo();
    }
}
