package com.anastasia.project.client;

import lombok.Getter;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;

/**
 * Wraps an RPC connection to a Corda node.
 * <p>
 * The RPC connection is configured using command line arguments.
 */
@Getter
public class NodeRPCConnection implements AutoCloseable {
    // The host of the node we are connecting to.

    public NodeRPCConnection(String host, String username, String password, int rpcPort) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.rpcPort = rpcPort;
        NetworkHostAndPort rpcAddress = new NetworkHostAndPort(host, rpcPort);
        CordaRPCClient rpcClient = new CordaRPCClient(rpcAddress);
        rpcConnection = rpcClient.start(username, password);
        proxy = rpcConnection.getProxy();
    }

    private String host;
    // The RPC port of the node we are connecting to.

    private String username;
    // The username for logging into the RPC client.

    private String password;
    // The password for logging into the RPC client.

    private int rpcPort;

    private CordaRPCConnection rpcConnection;

    private CordaRPCOps proxy;

    public void close() {
        rpcConnection.notifyServerAndClose();
    }
}