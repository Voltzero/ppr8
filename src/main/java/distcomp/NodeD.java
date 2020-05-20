package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.io.IOException;

public class NodeD extends BaseNode implements ParentNode {

    public NodeD() throws JMSException, IOException {
        super();

        nodeID = "D";
        Queue a = session.createQueue("A");
        Queue b = session.createQueue("B");
        Queue ac = session.createQueue("A");
        Queue bc = session.createQueue("B");

        producerA = session.createProducer(a);
        producerB = session.createProducer(b);

        consumerA = session.createConsumer(ac);
        consumerB = session.createConsumer(bc);
/*
        Queue queueDA = session.createQueue("D-A");
        this.producerDA = session.createProducer(queueDA);

        Queue queueDB = session.createQueue("D-B");
        this.producerDB = session.createProducer(queueDB);

        Queue queueAD = session.createQueue("A-D");
        consumerAD = session.createConsumer(queueAD);

        Queue queueBD = session.createQueue("B-D");
        consumerBD = session.createConsumer(queueBD);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);*/
    }

    @Override
    public void run() {

        Thread listenA = CustomerListener(consumerA, nodeID);
        Thread listenB = CustomerListener(consumerB, nodeID);

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

        producerA.send(en);
        producerB.send(en);

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
}
