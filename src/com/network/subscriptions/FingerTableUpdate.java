package com.network.subscriptions;

import com.network.ChordNode;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpAnsMessage;
import com.network.messages.Message;

import java.math.BigInteger;
import java.util.logging.Level;

public class FingerTableUpdate implements SubscriptionHandlerInterface {

    private final ChordNode node;

    public FingerTableUpdate(ChordNode node) {
        this.node = node;
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof LookUpAnsMessage) {
            try {
                BigInteger id = ((LookUpAnsMessage) msg).getId();
                InfoInterface finger = node.getFingerTable().get(id);

                if(finger instanceof NullInfo || !finger.getId().equals(msg.getSenderId())) {
                    NetworkLogger.printLog(Level.INFO, "Updated finger table - " + id + " " + msg.getSenderId());
                    NodeInfo newConnection = new NodeInfo(this.node, msg.getSenderId(), msg.getHostname(), msg.getPort());
                    newConnection.startConnection();
                    node.getFingerTable().put(id, newConnection);

                    if (finger instanceof NodeInfo) {
                        ((NodeInfo) finger).getConnection().close();
                    }
                }
            } catch (Exception e) {
                NetworkLogger.printLog(Level.WARNING, "Failure connecting to finger - " + e.getMessage());
            }

        }
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}
