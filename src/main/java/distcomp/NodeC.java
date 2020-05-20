package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.io.IOException;

public class NodeC extends BaseNode implements ParentNode {

    public NodeC() throws JMSException, IOException {
        super();

        nodeID = "C";
        Queue a = session.createQueue("A");
        Queue e = session.createQueue("E");
        Queue f = session.createQueue("F");
        Queue ac = session.createQueue("A");
        Queue ec = session.createQueue("E");
        Queue fc = session.createQueue("F");

        producerA = session.createProducer(a);
        producerE = session.createProducer(e);
        producerF = session.createProducer(f);

        consumerA = session.createConsumer(ac);
        consumerE = session.createConsumer(ec);
        consumerF = session.createConsumer(fc);
/*
        Queue queueCA = session.createQueue("C-A");
        this.producerCA = session.createProducer(queueCA);

        Queue queueCE = session.createQueue("C-E");
        this.producerCE = session.createProducer(queueCE);

        Queue queueCF = session.createQueue("C-F");
        this.producerCF = session.createProducer(queueCF);

        Queue queueAC = session.createQueue("A-C");
        consumerAC = session.createConsumer(queueAC);

        Queue queueEC = session.createQueue("E-C");
        consumerEC = session.createConsumer(queueEC);

        Queue queueFC = session.createQueue("F-C");
        consumerFC = session.createConsumer(queueFC);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);*/
    }

    @Override
    public void run() {

        Thread listenA = CustomerListener(consumerA, nodeID);
        Thread listenE = CustomerListener(consumerE, nodeID);
        Thread listenF = CustomerListener(consumerF, nodeID);

        listenA.setDaemon(true);
        listenE.setDaemon(true);
        listenF.setDaemon(true);

        listenA.start();
        listenE.start();
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
