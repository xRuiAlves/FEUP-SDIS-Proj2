package com.network.rmi;

import com.network.info.NodeInfo;

import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeRMIInterface extends Remote {
    NodeInfo lookup(BigInteger lookup_id) throws RemoteException;
}
