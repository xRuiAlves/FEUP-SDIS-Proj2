package com.network.subscriptions;

import com.network.info.BasicInfo;
import com.network.messages.chord.ChordMessage;

import java.util.concurrent.CountDownLatch;

public class LookUpHandler implements SubscriptionHandlerInterface {

    private final CountDownLatch latch;
    private BasicInfo info;

    public LookUpHandler() {
        this.latch = new CountDownLatch(1);
    }

    public void await() throws InterruptedException {
        this.latch.await();
    }

    public BasicInfo getInfo() {
        return info;
    }

    @Override
    public void notify(ChordMessage msg) {
        this.info = new BasicInfo(msg.getHostname(), msg.getPort());
        this.latch.countDown();
    }

    @Override
    public boolean isPermanent() {
        return false;
    }
}
