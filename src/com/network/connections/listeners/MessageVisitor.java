package com.network.connections.listeners;

import com.network.messages.chord.*;
import com.network.messages.protocol.*;

import java.net.UnknownHostException;

public interface MessageVisitor {
    void visit(LookUpAnswer msg);

    void visit(GetPredecessor getPredecessor) throws UnknownHostException;

    void visit(LookUp lookUp);

    void visit(Notify notify);

    void visit(Predecessor predecessor);

    void visit(Backup backup);

    void visit(No no);

    void visit(RequestBackup requestBackup);

    void visit(RetrieveIfExists retrieveIfExists);

    void visit(Yes yes);

}
