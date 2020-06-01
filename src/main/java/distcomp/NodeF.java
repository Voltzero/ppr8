package distcomp;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodeF extends BaseNode {

    public NodeF() throws JMSException, IOException {
        super();

        nodeID = "F";

        consumerF = session.createConsumer(f);
    }

    public NodeF(Map<String, Map<String, Integer>> topologyMap) throws JMSException, IOException {
        super();

        nodeID = "F";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);

        consumerF = session.createConsumer(f);
    }

    public NodeF(Map<String, Map<String, Integer>> topologyMap, boolean floodMax) throws JMSException, IOException {
        super();

        nodeID = "F";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);
        setFloodNeighboursMap();
        diameter = dijkstra.getDiam();
        this.floodMax = floodMax;

        consumerF = session.createConsumer(f);
        if (floodMax)
            generateMaxID(randLVLBound);
    }

    @Override
    public void run() {
        super.run();
        try {
            //getSendingThread().start();
            consumerF.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEnAsRoot() throws JMSException {
        sendEN(producerB);
        sendEN(producerC);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "C": {
                sendEN(producerB);
                break;
            }
            case "B": {
                sendEN(producerC);
                break;
            }
        }
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "B": {
                producerMaster = producerB;
                break;
            }
            case "C": {
                producerMaster = producerC;
                break;
            }
        }
    }

    @Override
    protected void setNeighboursMap() {
        neighboursMap = new HashMap<>();
        neighboursMap.put("B", false);
        neighboursMap.put("C", false);
    }
}
