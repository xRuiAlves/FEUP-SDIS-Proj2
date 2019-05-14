package com.network.connections;

import com.network.messages.LookUpAnsMessage;
import com.network.subscriptions.SubscriptionHandlerInterface;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

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
    public void notify(LookUpAnsMessage message) {
        ConcurrentLinkedDeque<SubscriptionHandlerInterface> deque = this.lookUps.remove(message.getId());
        for (SubscriptionHandlerInterface handler : deque) {
            handler.notify(message);
        }
    }


}
