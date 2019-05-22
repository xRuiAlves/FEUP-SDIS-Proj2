package com.network.connections;

import com.network.log.NetworkLogger;
import com.network.messages.chord.LookUpAnswer;
import com.network.subscriptions.SubscriptionHandlerInterface;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

public class ConnectionHandler implements ConnectionHandlerInterface {

    private ConcurrentHashMap<BigInteger, ConcurrentLinkedDeque<SubscriptionHandlerInterface> > lookUps;
    private static ConnectionHandler instance = new ConnectionHandler();

    public static ConnectionHandler getInstance() {
        return instance;
    }

    private ConnectionHandler() {
        this.lookUps = new ConcurrentHashMap<>();
    }

    @Override
    public void subscribeLookUp(BigInteger id, SubscriptionHandlerInterface handler) {
        if (this.lookUps.containsKey(id)) {
            this.lookUps.get(id).add(handler);
            return;
        }

        ConcurrentLinkedDeque<SubscriptionHandlerInterface> deque = new ConcurrentLinkedDeque<>();
        deque.add(handler);
        this.lookUps.put(id,deque);
    }

    @Override
    public void notify(LookUpAnswer message) {
        if (this.lookUps.containsKey(message.getId())) {
            ConcurrentLinkedDeque<SubscriptionHandlerInterface> deque = this.lookUps.get(message.getId());
            if (deque.size() == 0) {
                NetworkLogger.printLog(Level.SEVERE, "No handlers for lookup " + message.getId());
            }
            Iterator iter = deque.iterator();
            while (iter.hasNext()) {
                SubscriptionHandlerInterface next = (SubscriptionHandlerInterface) iter.next();
                next.notify(message);
                if (!next.isPermanent()) {
                    iter.remove();
                }
            }
        }
    }


}
