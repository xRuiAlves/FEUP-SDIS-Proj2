package com.network;

import com.network.connections.ConnectionHandler;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.TCPConnection;
import com.network.connections.manager.ConnectionManager;
import com.network.connections.server.Server;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpMessage;
import com.network.messages.Message;
import com.network.messages.Notify;
import com.network.subscriptions.FingerTableUpdate;
import com.network.subscriptions.JoinHandler;
import com.network.threads.ThreadPool;
import com.network.threads.operations.LookUpOperation;
import com.network.threads.operations.SendMessage;
import com.network.threads.operations.StabilizeOperation;
import com.network.threads.operations.UpdateFingers;
import com.network.utils.IdEncoder;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ChordNode {

    public final static Integer m = 64;
    private final Server server;
    private final BigInteger id;

    private InfoInterface predecessor;
    private NodeInfo successor;
    private ConcurrentHashMap<BigInteger, InfoInterface> fingerTable;
    private ConnectionManager manager;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java ChordNode <start_op> [ <ip> <port> ]");
            System.exit(-1);
        }

        switch (args[0]) {
            case "create":
                new ChordNode();
                break;
            case "join":
                if (args.length < 3) {
                    System.err.println("Usage: java join <start_op> <ip> <port>");
                    System.exit(-1);
                }
                InetAddress host = InetAddress.getByName(args[1]);
                Integer port = Integer.valueOf(args[2]);
                new ChordNode(host, port);
                break;
            default:
                System.err.println("Operation not supported");
                break;
        }
    }

    private ChordNode() throws IOException {
        this.manager = new ConnectionManager();
        this.fingerTable = new ConcurrentHashMap<>();
        this.server = new Server(this);
        ThreadPool.getInstance().submit(this.server);
        this.id = IdEncoder.encode(server.getServerConnection().getIp(), server.getServerConnection().getPort());
        this.predecessor = new NullInfo();
        this.successor = new NodeInfo(this, this.id, this.server.getServerConnection().getIp(), this.server.getServerConnection().getPort());
        ThreadPool.getInstance().scheduleAtFixedRate(new StabilizeOperation(this), 0, 1000, TimeUnit.MILLISECONDS);

        NetworkLogger.setNodeId(this.id.toString());
        NetworkLogger.printLog(Level.INFO, "Server connection open: " + server.getServerConnection().getIp().toString() + ":" + server.getServerConnection().getPort());
        NetworkLogger.printLog(Level.INFO, "New chord network started");

        this.startFingers();
    }

    private ChordNode(InetAddress host, Integer port) throws IOException {
        this.manager = new ConnectionManager();
        this.fingerTable = new ConcurrentHashMap<>();
        this.server = new Server(this);
        ThreadPool.getInstance().submit(this.server);
        this.id = IdEncoder.encode(server.getServerConnection().getIp(), server.getServerConnection().getPort());
        this.predecessor = new NullInfo();
        ThreadPool.getInstance().scheduleAtFixedRate(new StabilizeOperation(this), 0, 1000, TimeUnit.MILLISECONDS);

        NetworkLogger.setNodeId(this.id.toString());
        NetworkLogger.printLog(Level.INFO, "Server connection open: " + server.getServerConnection().getIp().toString() + ":" + server.getServerConnection().getPort());

        this.join(host, port);
        this.startFingers();
    }

    private void join(InetAddress host, Integer port) throws IOException {
        TCPConnection connection = null;
        try {
            connection = new TCPConnection(this, host, port);
        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Cannot find network");
            System.exit(-4);
        }
        NetworkLogger.printLog(Level.INFO, "Connection established");
        Message lookUpMessage = new LookUpMessage(this, this.id);
        ConnectionHandler.getInstance().subscribeLookUp(this.id, new JoinHandler(this));
        ThreadPool.getInstance().submit(new SendMessage(lookUpMessage, connection));
    }

    private void startFingers() {
        FingerTableUpdate fingerTableUpdate = new FingerTableUpdate(this);
        BigInteger two = new BigInteger("2");
        for (int i = 1; i < m; i++) {
            BigInteger fingerId = (this.id.add(two.pow(i))).mod(two.pow(m));
            this.fingerTable.put(fingerId, new NullInfo());
            ConnectionHandler.getInstance().subscribeLookUp(fingerId, fingerTableUpdate);
        }
        ThreadPool.getInstance().scheduleAtFixedRate(new UpdateFingers(this), 0, 500, TimeUnit.MILLISECONDS);

    }

    public void lookup(LookUpMessage message) {
        ThreadPool.getInstance().submit(new LookUpOperation(this, message));
    }

    public Server getServer() {
        return server;
    }

    public BigInteger getId() {
        return id;
    }

    public void setSuccessor(NodeInfo node) {
        if (this.successor == null || this.id.equals(this.successor.getId()) || node.inInterval(this.id, this.successor.getId())) {
            try {
                node.startConnection();
                this.successor = node;
                NetworkLogger.printLog(Level.INFO, "Changed successor - " + this.successor);
            } catch (IOException e) {
                NetworkLogger.printLog(Level.WARNING, "Failed connection to successor");
            }
        }
        if (this.successor != null) {
            ConnectionInterface connection = this.successor.getConnection();
            if (connection != null)
                ThreadPool.getInstance().submit(new SendMessage(new Notify(this, this.id), connection));
        }
    }

    public NodeInfo getSuccessor() {
        return successor;
    }

    public InfoInterface getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(NodeInfo predecessor) {
        if (this.predecessor instanceof NullInfo  || (predecessor.inInterval(this.predecessor.getId(), this.id))) {
            this.predecessor = predecessor;
            NetworkLogger.printLog(Level.INFO, "Predecessor updated - " + predecessor);
        }
    }

    public ConcurrentHashMap<BigInteger, InfoInterface> getFingerTable() {
        return fingerTable;
    }

    public ConnectionManager getManager() {
        return manager;
    }
}
