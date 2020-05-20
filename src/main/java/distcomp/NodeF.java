package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.io.IOException;

public class NodeF extends BaseNode implements ParentNode {

    public NodeF() throws JMSException, IOException {
        super();

        nodeID = "F";
        Queue b = session.createQueue("B");
        Queue c = session.createQueue("C");
        Queue bc = session.createQueue("B");
        Queue cc = session.createQueue("C");

        producerB = session.createProducer(b);
        producerC = session.createProducer(c);
        consumerB = session.createConsumer(bc);
        consumerC = session.createConsumer(cc);
/*
        Queue queueFB = session.createQueue("F-B");
        this.producerFB = session.createProducer(queueFB);

        Queue queueFC = session.createQueue("F-C");
        this.producerFC = session.createProducer(queueFC);

        Queue queueBF = session.createQueue("B-F");
        consumerBF = session.createConsumer(queueBF);

        Queue queueCF = session.createQueue("C-F");
        consumerCF = session.createConsumer(queueCF);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);*/
    }

    @Override
    public void run() {

        Thread listenB = CustomerListener(consumerB, nodeID);
        Thread listenC = CustomerListener(consumerC, nodeID);

        listenB.setDaemon(true);
        listenC.setDaemon(true);

        listenB.start();
        listenC.start();

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

        producerB.send(en);
        producerC.send(en);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "C": {
                sendEN(producerB);
                break;
            }
            case "B": {
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
        }
    }
}
