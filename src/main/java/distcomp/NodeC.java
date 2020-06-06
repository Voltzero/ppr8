package distcomp;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodeC extends BaseNode {

    public NodeC() throws JMSException, IOException {
        super();

        nodeID = "C";

        consumerC = session.createConsumer(c);
    }

    public NodeC(Map<String, Map<String, Integer>> topologyMap) throws JMSException, IOException {
        super();

        nodeID = "C";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);

        consumerC = session.createConsumer(c);
    }

    public NodeC(Map<String, Map<String, Integer>> topologyMap, boolean floodMax) throws JMSException, IOException {
        super();

        nodeID = "C";

        this.topologyMap = topologyMap;
        dijkstra = new Dijkstra(topologyMap, nodeID);
        previousNode = dijkstra.calculateShortestPaths(nodeID);
        setFloodNeighboursMap();
        diameter = dijkstra.getDiam();
        this.floodMax = floodMax;

        consumerC = session.createConsumer(c);
        if (floodMax)
            generateMaxID(randLVLBound);
    }

    public NodeC(String COORD, String CRITICAL) throws JMSException {
        super();

        nodeID = "C";

        this.COORDINATOR = COORD;
        this.CRITICAL = CRITICAL;

        if (nodeID.equals(COORDINATOR))
            isCoord = true;
        if (nodeID.equals(CRITICAL))
            isCritical = true;

    }

    @Override
    public void run() {
        super.run();
        try {
            //getSendingThread().start();
            consumerC.setMessageListener(this);
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
        sendEN(producerE);
        sendEN(producerF);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "A": {
                sendEN(producerE);
                sendEN(producerF);
                break;
            }
            case "F": {
                sendEN(producerE);
                sendEN(producerA);
                break;
            }
            case "E": {
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
            case "E": {
                producerMaster = producerE;
                break;
            }
        }
    }

    @Override
    protected void setNeighboursMap() {
        neighboursMap = new HashMap<>();
        neighboursMap.put("A", false);
        neighboursMap.put("F", false);
        neighboursMap.put("E", false);
    }
}
