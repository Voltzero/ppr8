package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.IOException;

public class NodeD extends BaseNode implements ParentNode {

    public NodeD() throws JMSException, IOException {
        super();

        nodeID = "D";

        Queue queueDA = session.createQueue("D-A");
        this.producerDA = session.createProducer(queueDA);

        Queue queueDB = session.createQueue("D-B");
        this.producerDB = session.createProducer(queueDB);

        Queue queueAD = session.createQueue("A-D");
        consumerAD = session.createConsumer(queueAD);

        Queue queueBD = session.createQueue("B-D");
        consumerBD = session.createConsumer(queueBD);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);
    }

    @Override
    public void run() {

        Thread listenA = CustomerListener(consumerAD, nodeID);
        Thread listenB = CustomerListener(consumerBD, nodeID);

        listenA.setDaemon(true);
        listenB.setDaemon(true);

        listenA.start();
        listenB.start();

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

        producerDA.send(en);
        producerDB.send(en);

    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "A": {
                sendEN(producerDB);
                break;
            }
            case "B": {
                sendEN(producerDA);
                break;
            }
        }
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "A": {
                producerMaster = producerDA;
                break;
            }
            case "B": {
                producerMaster = producerDB;
                break;
            }
        }
    }
}
