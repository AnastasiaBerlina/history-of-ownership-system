package com.anastasia.project.configs;

import com.anastasia.project.client.NodeRPCConnection;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Getter
public class CordaNodeConfig {

    private static final String LOCALHOST = "localhost";

    private String password = "test";

    private String user = "user1";

    @Value("${hosts.glass.address}")
    private int glassAddress;

    @Value("${hosts.gyspsum.address}")
    private int gypsymAddress;

    @Value("${hosts.iceCream.address}")
    private int iceCreamAddress;

    @Value("${hosts.root.address}")
    private int root;

    private List<NodeRPCConnection> connections = new ArrayList<>();

    @PostConstruct
    private void init() {
        NodeRPCConnection nodeRPCConnection = new NodeRPCConnection(LOCALHOST, user, password, glassAddress);
        connections.add(nodeRPCConnection);

        nodeRPCConnection = new NodeRPCConnection(LOCALHOST, user, password, gypsymAddress);
        connections.add(nodeRPCConnection);

        nodeRPCConnection = new NodeRPCConnection(LOCALHOST, user, password, iceCreamAddress);
        connections.add(nodeRPCConnection);

        nodeRPCConnection = new NodeRPCConnection(LOCALHOST, user, password, root);
        connections.add(nodeRPCConnection);
    }

    @PreDestroy
    private void close() {
        connections.forEach(NodeRPCConnection::close);
    }
}
