package com.network.connections.listeners;

import com.network.connections.ConnectionHandler;
import com.network.info.NodeInfo;
import com.network.messages.chord.*;
import com.network.threads.ThreadPool;
import com.network.threads.operations.SendMessage;

import java.net.UnknownHostException;

public class ListenerVisitor extends DefaultListener {
    private final Listener l;

    ListenerVisitor(Listener l) {
        this.l = l;
    }

    @Override
    public void visit(LookUpAnswer msg) {
        ConnectionHandler.getInstance().notify(msg);
    }

    @Override
    public void visit(GetPredecessor getPredecessor) throws UnknownHostException {
        // The same socket is live until the node that asks closes it
        ThreadPool.getInstance().submit(new SendMessage(new Predecessor(l.node), l.ci));
    }

    @Override
    public void visit(LookUp lookUp) {
        l.node.lookup(lookUp);
    }

    @Override
    public void visit(Notify notify) {
        l.node.setPredecessor(new NodeInfo(l.node, notify.getId(), notify.getHostname(), notify.getPort()));
    }

    @Override
    public void visit(Predecessor predecessor) {
        l.node.setSuccessor(new NodeInfo(l.node, predecessor.getId(), predecessor.getHostname(), predecessor.getPort()));
    }
}
