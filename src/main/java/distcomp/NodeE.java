package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodeE extends BaseNode {

    public NodeE() throws JMSException, IOException {
        super();

        nodeID = "E";

        consumerE = session.createConsumer(e);
    }

    public NodeE(Map<String, Map<String, Integer>> topologyMap) throws JMSException, IOException {
        super();

        nodeID = "E";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);

        consumerE = session.createConsumer(e);
    }

    public NodeE(Map<String, Map<String, Integer>> topologyMap, boolean floodMax) throws JMSException, IOException {
        super();

        nodeID = "E";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);
        setFloodNeighboursMap();
        diameter = dijkstra.getDiam();
        this.floodMax = floodMax;

        consumerE = session.createConsumer(e);
        if (floodMax)
            generateMaxID(randLVLBound);
    }

    @Override
    public void run() {
        super.run();
        try {
            //getSendingThread().start();
            consumerE.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEnAsRoot() throws JMSException {
        sendEN(producerC);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        sendQU(M);
    }

    @Override
    protected void sendQU(String NodeID) throws JMSException {
        Message qu = session.createTextMessage();
        qu.setStringProperty("NodeID", nodeID);
        qu.setStringProperty("Command", QU);

        producerC.send(qu);
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "C": {
                producerMaster = producerC;
                break;
            }
        }
    }

    @Override
    protected void setNeighboursMap() {
        neighboursMap = new HashMap<>();
        neighboursMap.put("C", false);
    }
}
