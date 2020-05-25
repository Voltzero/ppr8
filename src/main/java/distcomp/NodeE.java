package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.util.HashMap;

public class NodeE extends BaseNode {

    public NodeE() throws JMSException, IOException {
        super();

        nodeID = "E";

        consumerE = session.createConsumer(e);
    }

    @Override
    public void run() {
        super.run();
        try {
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
        neighboursMap.put("C",false);
    }
}
