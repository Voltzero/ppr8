package distcomp;

import javax.jms.*;
import java.util.Map;
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
    protected MessageProducer producerMaster;

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

    protected Random rand;
    protected String nodeID = "";
    protected final String EN = "EN";
    protected final String QU = "QN";
    protected volatile String M = "";
    protected boolean root = false;
    protected boolean wasRoot = false;

    protected Map<String, Boolean> neighboursMap;

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

        setNeighboursMap();
    }

    @Override
    public void onMessage(Message message) {
        try {
            String command = message.getStringProperty("Command");
            String id = message.getStringProperty("NodeID");
            if (!wasRoot && M.equals("")) {
                setMaster(id);
                setProducerMaster(id);
                System.out.println(nodeID + " Master is " + id);
                sendEnWithout(id);
                neighboursMap.put(id, true);
            } else if (!M.equals("")) {
                //System.out.println(nodeID + " received " + command + " from " + id);
                neighboursMap.put(id, true);

                if (command.equals(QU))
                    System.out.println(nodeID + " received " + command + " from " + id);

                if (checkNeighbours()) {
                    System.out.println(nodeID + " received answers from all neighbours | Sending QU to " + M);
                    sendQU(M);
                }
            } else if (wasRoot) {
                System.out.println("Root " + nodeID + " received " + command + " from " + id);
                neighboursMap.put(id, true);

                if (checkNeighbours())
                    System.out.println("Root " + nodeID + " received answers from all neighbours.");
            }
            sleepRandomTime();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private boolean checkNeighbours() {
        boolean check = true;
        for (Boolean b : neighboursMap.values()) {
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

    protected abstract void setNeighboursMap();

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

    @Override
    public void run() {
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
}
