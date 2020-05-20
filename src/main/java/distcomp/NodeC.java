package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.IOException;

public class NodeC extends BaseNode implements ParentNode {

    public NodeC() throws JMSException, IOException {
        super();

        nodeID = "C";

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
        topicProducer = session.createProducer(topic);
    }

    @Override
    public void run() {

        Thread listenA = CustomerListener(consumerAC, nodeID);
        Thread listenE = CustomerListener(consumerEC, nodeID);
        Thread listenF = CustomerListener(consumerFC, nodeID);

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

        producerCA.send(en);
        producerCE.send(en);
        producerCF.send(en);

    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "A": {
                sendEN(producerCE);
                sendEN(producerCF);
                break;
            }
            case "F": {
                sendEN(producerCE);
                sendEN(producerCA);
                break;
            }
            case "E": {
                sendEN(producerCA);
                sendEN(producerCF);
                break;
            }
        }
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "A": {
                producerMaster = producerCA;
                break;
            }
            case "F": {
                producerMaster = producerCF;
                break;
            }
            case "E": {
                producerMaster = producerCE;
                break;
            }
        }
    }
}
