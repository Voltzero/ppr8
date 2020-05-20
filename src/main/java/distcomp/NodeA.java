package distcomp;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.io.IOException;

public class NodeA extends BaseNode {

    public NodeA() throws JMSException, IOException {
        super();

        nodeID = "A";

        consumerA = session.createConsumer(a);

/*
        Queue queueAB = session.createQueue("A-B");
        this.producerAB = session.createProducer(queueAB);

        Queue queueAC = session.createQueue("A-C");
        this.producerAC = session.createProducer(queueAC);

        Queue queueAD = session.createQueue("A-D");
        this.producerAD = session.createProducer(queueAD);

        Queue queueBA = session.createQueue("B-A");
        consumerBA = session.createConsumer(queueBA);

        Queue queueCA = session.createQueue("C-A");
        consumerCA = session.createConsumer(queueCA);

        Queue queueDA = session.createQueue("D-A");
        consumerDA = session.createConsumer(queueDA);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);*/

    }

    @Override
    public void run() {

        Thread listenB = CustomerListener(consumerB, nodeID);
        Thread listenC = CustomerListener(consumerC, nodeID);
        Thread listenD = CustomerListener(consumerD, nodeID);

        listenB.setDaemon(true);
        listenC.setDaemon(true);
        listenD.setDaemon(true);

        listenB.start();
        listenC.start();
        listenD.start();

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
}
