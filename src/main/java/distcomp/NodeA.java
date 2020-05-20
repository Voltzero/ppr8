package distcomp;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.IOException;
import java.util.ArrayList;

public class NodeA extends BaseNode {

    public NodeA() throws JMSException, IOException {
        super();

        nodeID = "A";

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
        topicProducer = session.createProducer(topic);
    }

    @Override
    public void run() {

        Thread listenB = CustomerListener(consumerBA, nodeID);
        Thread listenC = CustomerListener(consumerCA, nodeID);
        Thread listenD = CustomerListener(consumerDA, nodeID);

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
        sendEN(producerAB);
        sendEN(producerAC);
        sendEN(producerAD);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "B": {
                sendEN(producerAC);
                sendEN(producerAD);
                break;
            }
            case "C": {
                sendEN(producerAB);
                sendEN(producerAD);
                break;
            }
            case "D": {
                sendEN(producerAB);
                sendEN(producerAC);
                break;
            }
        }
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "B": {
                producerMaster = producerAB;
                break;
            }
            case "C": {
                producerMaster = producerAC;
                break;
            }
            case "D": {
                producerMaster = producerAD;
                break;
            }
        }
    }
}
