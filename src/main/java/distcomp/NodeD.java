package distcomp;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodeD extends BaseNode {

    public NodeD() throws JMSException, IOException {
        super();

        nodeID = "D";

        consumerD = session.createConsumer(d);
    }

    public NodeD(Map<String, Map<String, Integer>> topologyMap) throws JMSException, IOException {
        super();

        nodeID = "D";

        this.topologyMap = topologyMap;

        consumerD = session.createConsumer(d);
    }

    @Override
    public void run() {
        super.run();
        try {
            consumerD.setMessageListener(this);
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
        sendEN(producerB);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "A": {
                sendEN(producerB);
                break;
            }
            case "B": {
                sendEN(producerA);
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
            case "B": {
                producerMaster = producerB;
                break;
            }
        }
    }

    @Override
    protected void setNeighboursMap() {
        neighboursMap = new HashMap<>();
        neighboursMap.put("A", false);
        neighboursMap.put("B", false);
    }
}
