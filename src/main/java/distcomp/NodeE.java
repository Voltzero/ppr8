package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.io.IOException;

public class NodeE extends BaseNode implements ParentNode {

    public NodeE() throws JMSException, IOException {
        super();

        nodeID = "E";
        Queue c = session.createQueue("C");
        Queue cc = session.createQueue("C");

        producerC = session.createProducer(c);

        consumerC = session.createConsumer(cc);
/*
        Queue queueEC = session.createQueue("E-C");
        this.producerEC = session.createProducer(queueEC);

        Queue queueCE = session.createQueue("C-E");
        consumerCE = session.createConsumer(queueCE);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);*/
    }

    @Override
    public void run() {

        Thread listenC = CustomerListener(consumerC, nodeID);

        listenC.setDaemon(true);

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

        producerC.send(en);
    }

    @Override
    protected void sendEnWithout(String NodeID) throws JMSException {
        sendQU(M);
    }

    @Override
    protected void sendQU(String NodeID) throws JMSException {
        Message qu = session.createTextMessage();
        qu.setStringProperty("NodeID", nodeID);
        qu.setStringProperty("Command", QU);

        producerC.send(qu);
    }

    @Override
    protected void setProducerMaster(String NodeID) {
        switch (NodeID) {
            case "C": {
                producerMaster = producerC;
                break;
            }
        }
    }
}
