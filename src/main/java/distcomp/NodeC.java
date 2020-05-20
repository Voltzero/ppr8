package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;

public class NodeC extends BaseNode implements ParentNode {

    public NodeC() throws JMSException, IOException {
        super();

        nodeID = "C";

        consumerC = session.createConsumer(c);
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
    }

    @Override
    public void sendEnAsRoot() throws JMSException {
        Message en = session.createTextMessage();
        en.setStringProperty("NodeID", nodeID);
        en.setStringProperty("Command", EN);

        producerA.send(en);
        producerE.send(en);
        producerF.send(en);

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
}
