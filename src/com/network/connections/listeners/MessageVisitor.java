package com.network.connections.listeners;

import com.network.messages.chord.*;
import com.network.messages.protocol.*;

import java.io.IOException;
import java.net.UnknownHostException;

public interface MessageVisitor {
    void visit(LookUpAnswer msg);

    void visit(GetPredecessor getPredecessor) throws UnknownHostException;

    void visit(LookUp lookUp);

    void visit(Notify notify);

    void visit(Predecessor predecessor);

    void visit(Backup backup) throws IOException;

    void visit(No no);

    void visit(RequestBackup requestBackup) throws IOException;

    void visit(RetrieveIfExists retrieveIfExists);

    void visit(Yes yes);

}