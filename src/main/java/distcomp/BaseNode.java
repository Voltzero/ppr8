package distcomp;

import javax.jms.*;
import java.util.Random;

public abstract class BaseNode extends Thread implements ParentNode, MessageListener {
    protected Session session;
    protected Connection con;

    protected MessageProducer producerA;
    protected MessageProducer producerB;
    protected MessageProducer producerC;
    protected MessageProducer producerD;
    protected MessageProducer producerE;
    protected MessageProducer producerF;

    protected MessageConsumer consumerA;
    protected MessageConsumer consumerB;
    protected MessageConsumer consumerC;
    protected MessageConsumer consumerD;
    protected MessageConsumer consumerE;
    protected MessageConsumer consumerF;

    protected Topic a;
    protected Topic b;
    protected Topic c;
    protected Topic d;
    protected Topic e;
    protected Topic f;

    protected MessageProducer topicProducer;
    protected MessageProducer producerMaster;
    protected Random rand;
    protected String nodeID = "";
    protected String EN = "EN";
    protected String QU = "QN";
    protected volatile String M = "";
    protected boolean root = false;
    protected boolean wasRoot = false;

    public BaseNode() throws JMSException {
        rand = new Random();

        ConnectionFactory factory = JmsProvider.getConnectionFactory();
        this.con = factory.createConnection();
        con.start();

        this.session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        a = session.createTopic("A");
        b = session.createTopic("B");
        c = session.createTopic("C");
        d = session.createTopic("D");
        e = session.createTopic("E");
        f = session.createTopic("F");

        producerA = session.createProducer(a);
        producerB = session.createProducer(b);
        producerC = session.createProducer(c);
        producerD = session.createProducer(d);
        producerE = session.createProducer(e);
        producerF = session.createProducer(f);

    }

    @Override
    public void onMessage(Message message) {
        try {
            String command = message.getStringProperty("Command");
            String id = message.getStringProperty("NodeID");
            System.out.println(nodeID + " recived " + command + " from " + id);
            if (!wasRoot && M.equals("") && command.equals(EN)) {
                setMaster(id);
                setProducerMaster(id);
                System.out.println(nodeID + " Master is " + id);
                sendEnWithout(id);
            }
            if (!M.equals("")) {
                if (command.equals(QU)) {
                    System.out.println(nodeID + " has Master " + M + " and recived " + command + " from " + id + " | Sending QU to " + M);
                    sendQU(M);
                }
            }
            if (wasRoot) {
                System.out.println("Root " + nodeID + " recived " + command + " from " + id);
            }
            sleepRandomTime();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    protected abstract void sendEnWithout(String NodeID) throws JMSException;

    protected void sendQU(String NodeID) throws JMSException {
        Message qu = session.createTextMessage();
        qu.setStringProperty("NodeID", nodeID);
        qu.setStringProperty("Command", QU);

        producerMaster.send(qu);
    }

    protected abstract void setProducerMaster(String NodeID);

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
