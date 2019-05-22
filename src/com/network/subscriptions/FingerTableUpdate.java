package com.network.subscriptions;

import com.network.ChordNode;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.chord.ChordMessage;
import com.network.messages.chord.LookUpAnswer;

import java.math.BigInteger;
import java.util.logging.Level;

public class FingerTableUpdate implements SubscriptionHandlerInterface {

    private final ChordNode node;

    public FingerTableUpdate(ChordNode node) {
        this.node = node;
    }

    @Override
    public void notify(ChordMessage msg) {
        if (msg instanceof LookUpAnswer) {
            try {
                BigInteger id = ((LookUpAnswer) msg).getId();
                InfoInterface finger = node.getFingerTable().get(id);

                if(finger instanceof NullInfo || !finger.getId().equals(msg.getSenderId())) {
//                    NetworkLogger.printLog(Level.INFO, "Updated finger table - " + id + " " + msg.getSenderId());

                    if (finger instanceof NodeInfo) {
                        BigInteger fingerId = finger.getId();
                        if (this.node.getManager().contains(fingerId)) {
                            this.node.getManager().decreaseInterface(fingerId);
                        }
                    }

                    if (this.node.getManager().contains(msg.getSenderId())) {
                        NodeInfo newConnection = new NodeInfo(this.node, msg.getSenderId(), this.node.getManager().get(msg.getSenderId()));
                        node.getFingerTable().put(id, newConnection);

                    } else {
                        NodeInfo newConnection = new NodeInfo(this.node, msg.getSenderId(), msg.getHostname(), msg.getPort());
                        newConnection.startConnection();
                        node.getFingerTable().put(id, newConnection);
                        node.getManager().put(msg.getSenderId(), newConnection.getListener());
                    }

                } else {
//                    NetworkLogger.printLog(Level.INFO, "No change to finger table - " + id + " " + msg.getSenderId());
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
