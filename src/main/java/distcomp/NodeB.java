package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.IOException;

public class NodeB extends BaseNode implements ParentNode {

    public NodeB() throws JMSException, IOException {
        super();

        nodeID = "B";

        Queue queueBA = session.createQueue("B-A");
        this.producerBA = session.createProducer(queueBA);

        Queue queueBD = session.createQueue("B-D");
        this.producerBD = session.createProducer(queueBD);

        Queue queueBF = session.createQueue("B-F");
        this.producerBF = session.createProducer(queueBF);

        Queue queueAB = session.createQueue("A-B");
        consumerAB = session.createConsumer(queueAB);

        Queue queueDB = session.createQueue("D-B");
        consumerDB = session.createConsumer(queueDB);

        Queue queueFB = session.createQueue("F-B");
        consumerFB = session.createConsumer(queueFB);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);
    }

    @Override
    public void run() {

        Thread listenA = CustomerListener(consumerAB, nodeID);
        Thread listenD = CustomerListener(consumerDB, nodeID);
        Thread listenF = CustomerListener(consumerFB, nodeID);

        listenA.setDaemon(true);
        listenD.setDaemon(true);
        listenF.setDaemon(true);

        listenA.start();
        listenD.start();
        listenF.start();

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

        producerBA.send(en);
        producerBD.send(en);
        producerBF.send(en);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "A": {
                sendEN(producerBD);
                sendEN(producerBF);
                break;
            }
            case "F": {
                sendEN(producerBD);
                sendEN(producerBA);
                break;
            }
            case "D": {
                sendEN(producerBA);
                sendEN(producerBF);
                break;
            }
        }
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "A": {
                producerMaster = producerBA;
                break;
            }
            case "F": {
                producerMaster = producerBF;
                break;
            }
            case "D": {
                producerMaster = producerBD;
                break;
            }
        }
    }
}
