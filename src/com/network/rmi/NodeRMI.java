package com.network.rmi;

import com.network.info.NodeInfo;

import java.math.BigInteger;
import java.rmi.RemoteException;

public class NodeRMI implements NodeRMIInterface {
    @Override
    public NodeInfo lookup(BigInteger lookup_id) throws RemoteException {
        return null;
    }
}
