package com.network.connections;

import com.network.messages.LookUpAnsMessage;
import com.network.subscriptions.SubscriptionHandlerInterface;

import java.math.BigInteger;

public interface ConnectionHandlerInterface {

    void subscribeLookUp(BigInteger id, SubscriptionHandlerInterface handler);

    void notify(LookUpAnsMessage message);

}
