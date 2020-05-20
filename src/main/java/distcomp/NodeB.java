package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.io.IOException;

public class NodeB extends BaseNode implements ParentNode {

    public NodeB() throws JMSException, IOException {
        super();

        nodeID = "B";

        Queue a = session.createQueue("A");
        Queue d = session.createQueue("D");
        Queue f = session.createQueue("F");
        Queue ac = session.createQueue("A");
        Queue dc = session.createQueue("D");
        Queue fc = session.createQueue("F");

        producerA = session.createProducer(a);
        producerD = session.createProducer(d);
        producerF = session.createProducer(f);

        consumerA = session.createConsumer(ac);
        consumerD = session.createConsumer(dc);
        consumerF = session.createConsumer(fc);
/*
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
        topicProducer = session.createProducer(topic);*/
    }

    @Override
    public void run() {

        Thread listenA = CustomerListener(consumerA, nodeID);
        Thread listenD = CustomerListener(consumerD, nodeID);
        Thread listenF = CustomerListener(consumerF, nodeID);

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

        producerA.send(en);
        producerD.send(en);
        producerF.send(en);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "A": {
                sendEN(producerD);
                sendEN(producerF);
                break;
            }
            case "F": {
                sendEN(producerD);
                sendEN(producerA);
                break;
            }
            case "D": {
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
            case "D": {
                producerMaster = producerD;
                break;
            }
        }
    }
}
