package distcomp;

import javax.jms.*;
import java.util.HashMap;
import java.util.Random;

public abstract class BaseNode extends Thread implements ParentNode {
    protected Session session;
    protected Connection con;
    protected MessageProducer producerAB;
    protected MessageProducer producerAC;
    protected MessageProducer producerAD;
    protected MessageConsumer consumerBA;
    protected MessageConsumer consumerCA;
    protected MessageConsumer consumerDA;

    protected MessageProducer producerBA;
    protected MessageProducer producerBD;
    protected MessageProducer producerBF;
    protected MessageConsumer consumerAB;
    protected MessageConsumer consumerDB;
    protected MessageConsumer consumerFB;

    protected MessageProducer producerCA;
    protected MessageProducer producerCE;
    protected MessageProducer producerCF;
    protected MessageConsumer consumerAC;
    protected MessageConsumer consumerEC;
    protected MessageConsumer consumerFC;

    protected MessageProducer producerDA;
    protected MessageProducer producerDB;
    protected MessageConsumer consumerAD;
    protected MessageConsumer consumerBD;

    protected MessageProducer producerEC;
    protected MessageConsumer consumerCE;

    protected MessageProducer producerFB;
    protected MessageProducer producerFC;
    protected MessageConsumer consumerBF;
    protected MessageConsumer consumerCF;

    protected MessageProducer topicProducer;
    protected MessageProducer producerMaster;
    protected Random rand;
    protected String nodeID = "";
    protected String EN = "EN";
    protected String QU = "QN";
    protected volatile String M = "";
    protected boolean root = false;
    protected boolean wasRoot = false;
    protected volatile HashMap<MessageConsumer, Boolean> mapNeighbours;

    private boolean quSent = false;

    public BaseNode() throws JMSException {
        rand = new Random();

        ConnectionFactory factory = JmsProvider.getConnectionFactory();
        this.con = factory.createConnection();
        con.start();

        this.session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    protected Thread CustomerListener(MessageConsumer consumer, String NodeID) {
        return new Thread(() -> {
            while (true) {
                try {
                    Message recived = (Message) consumer.receive();
                    String command = recived.getStringProperty("Command");
                    String id = recived.getStringProperty("NodeID");
                    System.out.println(nodeID + " recived " + command + " from " + id);
                    if (!wasRoot && M.equals("")) {
                        setMaster(id);
                        setProducerMaster(id);
                        System.out.println(nodeID + " Master is " + id);
                        sendEnWithout(id);
                        mapNeighbours.put(consumer, true);
                    }else if (!M.equals("")) {
                        if (!quSent) {
                            mapNeighbours.put(consumer, true);
                            System.out.println(nodeID + " has Master " + M + " and recived " + command + " from " + id);
                        }
                    }
                    if (wasRoot) {
                        if (!mapNeighbours.get(consumer)) {
                            System.out.println("Root " + nodeID + " recived " + command + " from " + id);
                            mapNeighbours.put(consumer, true);
                        }
                    }
                    sleepRandomTime();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected boolean checkNeighbours() {
        boolean check = true;
        for (Boolean b : mapNeighbours.values()) {
            if (!b) {
                check = false;
                break;
            }
        }
        return check;
    }

    protected abstract void sendEnWithout(String NodeID) throws JMSException;

    protected void sendQU(String NodeID) throws JMSException {
        Message qu = session.createTextMessage();
        qu.setStringProperty("NodeID", nodeID);
        qu.setStringProperty("Command", QU);

        producerMaster.send(qu);
    }

    protected abstract void setProducerMaster(String NodeID);
    protected abstract void setMapNeighbours();

    protected void sendEN(MessageProducer producer) throws JMSException {
        Message en = session.createTextMessage();
        en.setStringProperty("NodeID", nodeID);
        en.setStringProperty("Command", EN);

        producer.send(en);
    }

    protected void sleepRandomTime() {
        try {
            Thread.sleep((rand.nextInt(4) + 1) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void setMaster(String ID) {
        M = ID;
    }

    public synchronized void setAsRoot() {
        root = true;
        wasRoot = true;
    }
}
