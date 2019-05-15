package com.network.subscriptions;

import com.network.messages.Message;

public interface SubscriptionHandlerInterface {
    void notify(Message msg);

    boolean isPermanent();
}
