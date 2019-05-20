package com.network.rmi;

import com.network.info.BasicInfo;

import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeRMIInterface extends Remote {
    BasicInfo lookup(BigInteger lookup_id) throws RemoteException, InterruptedException;
}
