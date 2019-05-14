package com.network;

import com.network.connections.client.ConnectionInterface;
import com.network.utils.IdEncoder;

import java.math.BigInteger;

public class NodeInfo implements Comparable{
    private BigInteger target;
    private BigInteger access;
    private ConnectionInterface connection;

    public NodeInfo(BigInteger target, BigInteger access, ConnectionInterface connection) {
        this.target = target;
        this.access = access;
        this.connection = connection;
    }


    public BigInteger getTarget() {
        return target;
    }

    public BigInteger getAccess() {
        return access;
    }

    public ConnectionInterface getConnection() {
        return connection;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof NodeInfo)) {
            return 0;
        }
        return access.compareTo(((NodeInfo) o).access);
    }
}
