package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.util.HashMap;

public class NodeD extends BaseNode {

    public NodeD() throws JMSException, IOException {
        super();

        nodeID = "D";

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
        neighboursMap.put("A",false);
        neighboursMap.put("B",false);
    }
}
