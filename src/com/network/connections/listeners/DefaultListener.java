package com.network.connections.listeners;

import com.network.log.NetworkLogger;
import com.network.messages.Message;
import com.network.messages.chord.*;
import com.network.messages.protocol.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;

public abstract class DefaultListener implements MessageVisitor {
    @Override
    public void visit(LookUpAnswer msg) {
        this.defaultVisit(msg);
    }

    @Override
    public void visit(GetPredecessor getPredecessor) throws UnknownHostException {
        this.defaultVisit(getPredecessor);
    }

    @Override
    public void visit(LookUp lookUp) {
        this.defaultVisit(lookUp);
    }

    @Override
    public void visit(Notify notify) {
        this.defaultVisit(notify);
    }

    @Override
    public void visit(Predecessor predecessor) {
        this.defaultVisit(predecessor);
    }

    @Override
    public void visit(Backup backup) throws IOException {
        this.defaultVisit(backup);
    }

    @Override
    public void visit(No no) {
        this.defaultVisit(no);
    }

    @Override
    public void visit(RequestBackup requestBackup) throws IOException {
        this.defaultVisit(requestBackup);
    }

    @Override
    public void visit(RetrieveIfExists retrieveIfExists) throws IOException {
        this.defaultVisit(retrieveIfExists);
    }

    @Override
    public void visit(Yes yes) {
        this.defaultVisit(yes);
    }

    @Override
    public void visit(Retrieved retrieved) {
        this.defaultVisit(retrieved);
    }

    private void defaultVisit(Message msg) {
        NetworkLogger.printLog(Level.SEVERE, "Message type not supported");
    }
}
