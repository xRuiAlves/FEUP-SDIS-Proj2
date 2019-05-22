package com.network.subscriptions;

import com.network.messages.chord.ChordMessage;

public interface SubscriptionHandlerInterface {
    void notify(ChordMessage msg);

    boolean isPermanent();
}
