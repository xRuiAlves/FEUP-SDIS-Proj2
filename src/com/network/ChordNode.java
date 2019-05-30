package com.network;

import com.network.connections.ConnectionHandler;
import com.network.connections.client.ConnectionInterface;
import com.network.connections.client.JSSETCPConnection;
import com.network.connections.listeners.Listener;
import com.network.connections.manager.ConnectionManager;
import com.network.connections.server.Server;
import com.network.info.BasicInfo;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.Message;
import com.network.messages.chord.LookUp;
import com.network.messages.chord.Notify;
import com.network.storage.state.BackupState;
import com.network.subscriptions.FingerTableUpdate;
import com.network.subscriptions.JoinHandler;
import com.network.threads.ThreadPool;
import com.network.threads.operations.*;
import com.network.utils.IdEncoder;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ChordNode {

    public final static Integer m = 64;
    public final static Integer fault = 3;

    private static final String FOLDER_PREFIX = "node_";
    private final Server server;
    private final BigInteger id;

    private InfoInterface predecessor;
    private NodeInfo successor;
    private ConcurrentHashMap<BigInteger, InfoInterface> fingerTable = new ConcurrentHashMap<>();
    private ConcurrentLinkedDeque<BigInteger> fingerTableOrder = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedQueue<BasicInfo> successors = new ConcurrentLinkedQueue<>();
    private ConnectionManager manager = new ConnectionManager();
    private FileRedistribution fileRedistribution;

    public ChordNode() throws IOException {
        this.server = new Server(this);
        ThreadPool.getInstance().submit(this.server);
        this.id = IdEncoder.encode(server.getServerConnection().getIp(), server.getServerConnection().getPort());
        this.predecessor = new NullInfo();
        this.successor = new NodeInfo(this, this.id, this.server.getServerConnection().getIp(), this.server.getServerConnection().getPort());
        ThreadPool.getInstance().scheduleAtFixedRate(new StabilizeOperation(this), 0, 500, TimeUnit.MILLISECONDS);
        ThreadPool.getInstance().scheduleAtFixedRate(new CheckPredecessor(this), 0, 500, TimeUnit.MILLISECONDS);
        fileRedistribution = new FileRedistribution(this);
        ThreadPool.getInstance().scheduleAtFixedRate(fileRedistribution, 0, 1000, TimeUnit.MILLISECONDS);

        NetworkLogger.setNodeId(this.id.toString());
        NetworkLogger.printLog(Level.INFO, "Server connection open: " + server.getServerConnection().getIp().toString() + ":" + server.getServerConnection().getPort());
        NetworkLogger.printLog(Level.INFO, "New chord network started");

        this.startFingers();
    }

    public ChordNode(InetAddress host, Integer port) throws IOException {
        this.server = new Server(this);
        ThreadPool.getInstance().submit(this.server);
        this.id = IdEncoder.encode(server.getServerConnection().getIp(), server.getServerConnection().getPort());
        this.predecessor = new NullInfo();
        ThreadPool.getInstance().scheduleAtFixedRate(new StabilizeOperation(this), 0, 500, TimeUnit.MILLISECONDS);
        ThreadPool.getInstance().scheduleAtFixedRate(new CheckPredecessor(this), 0, 500, TimeUnit.MILLISECONDS);
        fileRedistribution = new FileRedistribution(this);
        ThreadPool.getInstance().scheduleAtFixedRate(fileRedistribution, 0, 1000, TimeUnit.MILLISECONDS);

        NetworkLogger.setNodeId(this.id.toString());
        NetworkLogger.printLog(Level.INFO, "Server connection open: " + server.getServerConnection().getIp().toString() + ":" + server.getServerConnection().getPort());

        this.join(host, port);
        this.startFingers();
    }

    private void join(InetAddress host, Integer port) throws IOException {
        Listener listener = null;
        try {
            listener = new Listener(this, new JSSETCPConnection(host, port));
        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Cannot find network");
            System.exit(-4);
        }
        NetworkLogger.printLog(Level.INFO, "Connection established");
        Message lookUpMessage = new LookUp(this, this.id);
        ConnectionHandler.getInstance().subscribeLookUp(this.id, new JoinHandler(this));
        ThreadPool.getInstance().submit(new SendMessage(lookUpMessage, listener.getInternal()));
    }

    private void startFingers() {
        FingerTableUpdate fingerTableUpdate = new FingerTableUpdate(this);
        BigInteger two = new BigInteger("2");
        for (int i = 1; i < m; i++) {
            BigInteger fingerId = (this.id.add(two.pow(i))).mod(two.pow(m));
            this.fingerTableOrder.add(fingerId);
            this.fingerTable.put(fingerId, new NullInfo());
            ConnectionHandler.getInstance().subscribeLookUp(fingerId, fingerTableUpdate);
        }
        ThreadPool.getInstance().scheduleAtFixedRate(new UpdateFingers(this), 0, 1000, TimeUnit.MILLISECONDS);

    }

    public void lookup(LookUp message) {
        ThreadPool.getInstance().submit(new LookUpOperation(this, message));
    }

    public Server getServer() {
        return server;
    }

    public BigInteger getId() {
        return id;
    }

    public String getBaseFolderName() {
        return FOLDER_PREFIX + id;
    }

    public void setSuccessor(NodeInfo node, ConcurrentLinkedQueue<BasicInfo> sucessors) {
        if (this.successor == null || ((this.id.equals(this.successor.getId()) || node.inInterval(this.id, this.successor.getId())) && !this.successor.getId().equals(node.getId()))) {
            try {
                NetworkLogger.printLog(Level.INFO, "Predecessor has id " + node.getId());
                node.startConnection();
                this.successor = node;
                this.successors = sucessors;
                NetworkLogger.printLog(Level.INFO, "Changed successor - " + this.successor);
            } catch (IOException e) {
                NetworkLogger.printLog(Level.WARNING, "Failed connection to successor - " + e.getMessage());
            }
        }

        if (this.successor != null) {
            if (this.id.equals(node.getId())) {
                this.successors = sucessors;
            }

            ConnectionInterface connection = this.successor.getListener().getInternal();
            if (connection != null)
                ThreadPool.getInstance().submit(new SendMessage(new Notify(this, this.id), connection));
        }
    }

    public void advanceSuccessor() {

        try {
            BasicInfo info = this.successors.poll();
            if (info == null) {
                NetworkLogger.printLog(Level.SEVERE, "Couldn't determine successor - exiting");
                System.exit(-2);
            }

            NetworkLogger.printLog(Level.INFO, "Moving to next peer with " + info.getIp() + ":" + info.getPort());
            BigInteger succId = IdEncoder.encode(info.getIp(), info.getPort());
            final NodeInfo newSucc = new NodeInfo(this, succId, info.getIp(), info.getPort());
            if (succId.equals(id)) {
                this.predecessor = new NullInfo();
                this.successors = new ConcurrentLinkedQueue<>();
            } else {
                newSucc.startConnection();
            }
            this.successor = newSucc;
            NetworkLogger.printLog(Level.WARNING, "Changed to the next successor - " + this.successor);
        } catch (IOException e) {
            NetworkLogger.printLog(Level.WARNING, "Failed connection to next successor - " + e.getMessage());
            this.advanceSuccessor();
        }
    }

    public NodeInfo getSuccessor() {
        return successor;
    }

    public InfoInterface getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(NodeInfo predecessor) {
        if (this.predecessor instanceof NullInfo || (predecessor.inInterval(this.predecessor.getId(), this.id))) {

            try {
                predecessor.startConnection();
                this.predecessor = predecessor;
                ThreadPool.getInstance().submit(() -> {
                    Enumeration<BigInteger> ids = BackupState.getInstance().getIds();
                    while (ids.hasMoreElements()) {
                        BigInteger id = ids.nextElement();
                        this.getFileRedistribution().addIdToCheck(id);

                    }
                });
                NetworkLogger.printLog(Level.INFO, "Changed predecessor - " + this.predecessor);
            } catch (IOException e) {
                NetworkLogger.printLog(Level.WARNING, "Failed connection to predecessor - " + e.getMessage());
            }
        }
    }

    public ConcurrentHashMap<BigInteger, InfoInterface> getFingerTable() {
        return fingerTable;
    }

    public ConcurrentLinkedDeque<BigInteger> getFingerTableOrder() {
        return fingerTableOrder;
    }

    public ConnectionManager getManager() {
        return manager;
    }

    public FileRedistribution getFileRedistribution() {
        return fileRedistribution;
    }

    public ConcurrentLinkedQueue<BasicInfo> getSuccessors() {
        return successors;
    }


    public void resetPredecessor() {
        this.predecessor = new NullInfo();
    }
}
