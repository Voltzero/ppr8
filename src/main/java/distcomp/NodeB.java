package distcomp;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodeB extends BaseNode {

    public NodeB() throws JMSException, IOException {
        super();

        nodeID = "B";

        consumerB = session.createConsumer(b);
    }

    public NodeB(Map<String, Map<String, Integer>> topologyMap) throws JMSException, IOException {
        super();

        nodeID = "B";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);

        consumerB = session.createConsumer(b);
    }

    public NodeB(Map<String, Map<String, Integer>> topologyMap, boolean floodMax) throws JMSException, IOException {
        super();

        nodeID = "B";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);
        diameter = dijkstra.getDiam();

        consumerA = session.createConsumer(b);
        if (floodMax)
            generateMaxID();
    }

    @Override
    public void run() {
        super.run();
        try {
            getSendingThread().start();
            consumerB.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEnAsRoot() throws JMSException {
        sendEN(producerA);
        sendEN(producerD);
        sendEN(producerF);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "A": {
                sendEN(producerD);
                sendEN(producerF);
                break;
            }
            case "F": {
                sendEN(producerD);
                sendEN(producerA);
                break;
            }
            case "D": {
                sendEN(producerA);
                sendEN(producerF);
                break;
            }
        }
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "A": {
                producerMaster = producerA;
                break;
            }
            case "F": {
                producerMaster = producerF;
                break;
            }
            case "D": {
                producerMaster = producerD;
                break;
            }
        }
    }

    @Override
    protected void setNeighboursMap() {
        neighboursMap = new HashMap<>();
        neighboursMap.put("A", false);
        neighboursMap.put("F", false);
        neighboursMap.put("D", false);
    }
}
