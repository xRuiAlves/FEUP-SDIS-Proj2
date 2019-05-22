package com.network.connections;

import com.network.messages.chord.LookUpAnswer;
import com.network.subscriptions.SubscriptionHandlerInterface;

import java.math.BigInteger;

public interface ConnectionHandlerInterface {

    void subscribeLookUp(BigInteger id, SubscriptionHandlerInterface handler);

    void notify(LookUpAnswer message);

}
