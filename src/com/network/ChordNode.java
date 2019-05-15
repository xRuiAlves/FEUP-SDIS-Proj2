package com.network;

import com.network.connections.ConnectionHandler;
import com.network.connections.client.TCPConnection;
import com.network.connections.server.Server;
import com.network.info.InfoInterface;
import com.network.info.NodeInfo;
import com.network.info.NullInfo;
import com.network.log.NetworkLogger;
import com.network.messages.LookUpMessage;
import com.network.messages.Message;
import com.network.subscriptions.JoinHandler;
import com.network.threads.ThreadPool;
import com.network.threads.operations.LookUpOperation;
import com.network.threads.operations.SendMessage;
import com.network.threads.operations.StabilizeOperation;
import com.network.utils.IdEncoder;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ChordNode {

    public final static Integer m = 64;
    private final Server server;
    private final BigInteger id;
    private InfoInterface predecessor;
    private NodeInfo successor;

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
        this.server = new Server(this);
        ThreadPool.getInstance().submit(this.server);
        this.id = IdEncoder.encode(server.getServerConnection().getIp(), server.getServerConnection().getPort());
        ThreadPool.getInstance().scheduleAtFixedRate(new StabilizeOperation(this), 0, 200, TimeUnit.MILLISECONDS);
        this.predecessor = new NullInfo();
        NetworkLogger.printLog(Level.WARNING, "Initiate successor of the first node as itself");
        NetworkLogger.setNodeId(this.id.toString());
        NetworkLogger.printLog(Level.INFO, "Server connection open: " + server.getServerConnection().getIp().toString() + ":" + server.getServerConnection().getPort());
        NetworkLogger.printLog(Level.INFO, "New chord network started");
    }

    private ChordNode(InetAddress host, Integer port) throws IOException {
        this.server = new Server(this);
        ThreadPool.getInstance().submit(this.server);
        this.id = IdEncoder.encode(server.getServerConnection().getIp(), server.getServerConnection().getPort());
        ThreadPool.getInstance().scheduleAtFixedRate(new StabilizeOperation(this), 0, 200, TimeUnit.MILLISECONDS);
        this.predecessor = new NullInfo();

        NetworkLogger.setNodeId(this.id.toString());
        NetworkLogger.printLog(Level.INFO, "Server connection open: " + server.getServerConnection().getIp().toString() + ":" + server.getServerConnection().getPort());
        this.join(host, port);
    }

    private void join(InetAddress host, Integer port) throws IOException {
        TCPConnection connection  = new TCPConnection(this, host, port);
        NetworkLogger.printLog(Level.INFO, "Connection established");
        Message lookUpMessage = new LookUpMessage(this, this.id);
        ConnectionHandler.getInstance().subscribeLookUp(this.id, new JoinHandler(this));
        ThreadPool.getInstance().submit(new SendMessage(lookUpMessage, connection));
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
        this.successor = node;
        NetworkLogger.printLog(Level.INFO, "Successor updated - " + node);
    }

    public NodeInfo getSuccessor() {
        return successor;
    }

    public InfoInterface getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(InfoInterface predecessor) {
        this.predecessor = predecessor;
        NetworkLogger.printLog(Level.INFO, "Predecessor updated - " + predecessor);

    }
}
