package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.IOException;
import java.util.HashMap;

public class NodeF extends BaseNode implements ParentNode {

    public NodeF() throws JMSException, IOException {
        super();

        nodeID = "F";

        Queue queueFB = session.createQueue("F-B");
        this.producerFB = session.createProducer(queueFB);

        Queue queueFC = session.createQueue("F-C");
        this.producerFC = session.createProducer(queueFC);

        Queue queueBF = session.createQueue("B-F");
        consumerBF = session.createConsumer(queueBF);

        Queue queueCF = session.createQueue("C-F");
        consumerCF = session.createConsumer(queueCF);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);

        setMapNeighbours();
    }

    @Override
    public void run() {

        Thread listenB = CustomerListener(consumerBF, nodeID);
        Thread listenC = CustomerListener(consumerCF, nodeID);

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

        producerFB.send(en);
        producerFC.send(en);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        switch (NodeID) {
            case "C": {
                sendEN(producerFB);
                break;
            }
            case "B": {
                sendEN(producerFC);
                break;
            }
        }
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "B": {
                producerMaster = producerFB;
                break;
            }
            case "C": {
                producerMaster = producerFC;
                break;
            }
        }
    }
    @Override
    protected void setMapNeighbours() {
        mapNeighbours = new HashMap<>();
        mapNeighbours.put(consumerBF, false);
        mapNeighbours.put(consumerCF, false);
    }
}
