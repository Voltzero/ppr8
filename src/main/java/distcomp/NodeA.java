package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.util.HashMap;

public class NodeA extends BaseNode {

    public NodeA() throws JMSException, IOException {
        super();

        nodeID = "A";

        consumerA = session.createConsumer(a);

    }

    @Override
    public void run() {
        if (root) {
            try {
                sendEnAsRoot();
                root = false;
                sleepRandomTime();
            } catch (JMSException e) {
                e.getMessage();
            }
        }
        try {
            consumerA.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        super.onMessage(message);
    }

    @Override
    public void sendEnAsRoot() throws JMSException {
        sendEN(producerB);
        sendEN(producerC);
        sendEN(producerD);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "B": {
                sendEN(producerC);
                sendEN(producerD);
                break;
            }
            case "C": {
                sendEN(producerB);
                sendEN(producerD);
                break;
            }
            case "D": {
                sendEN(producerB);
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
            case "D": {
                producerMaster = producerD;
                break;
            }
        }
    }

    @Override
    protected void setNeighboursMap() {
        neighboursMap = new HashMap<>();
        neighboursMap.put("B",false);
        neighboursMap.put("C",false);
        neighboursMap.put("D",false);
    }

}
