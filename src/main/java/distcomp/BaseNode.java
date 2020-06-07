package distcomp;

import javax.jms.*;
import java.util.Queue;
import java.util.*;

public abstract class BaseNode extends Thread implements MessageListener {
    protected Session session;
    protected Connection con;

    protected MessageProducer producerA;
    protected MessageProducer producerB;
    protected MessageProducer producerC;
    protected MessageProducer producerD;
    protected MessageProducer producerE;
    protected MessageProducer producerF;
    protected MessageProducer producerReport;

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
    protected Topic report;

    protected Random rand;
    protected String nodeID = "";

    protected String COORDINATOR;
    protected String CRITICAL;
    protected boolean isCoord = false;
    protected boolean isCritical = false;
    protected Queue<String> queueToCritical;
    private volatile boolean waitingForResponse = false;

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
        report = session.createTopic("Report");

        producerA = session.createProducer(a);
        producerB = session.createProducer(b);
        producerC = session.createProducer(c);
        producerD = session.createProducer(d);
        producerE = session.createProducer(e);
        producerF = session.createProducer(f);
        producerReport = session.createProducer(report);
    }

    @Override
    public void onMessage(Message message) {

        if (isCoord) {

        } else if (isCritical) {

        } else {

        }
    }

    private void askForPermission() throws JMSException {
        Message ms = session.createTextMessage();
        ms.setStringProperty("NodeID", nodeID);

        messenger(ms, COORDINATOR);
    }

    private void sendReport(String s) throws JMSException {
        Message ms = session.createTextMessage(s);
        producerReport.send(ms);
    }

    private void messenger(Message message, String node) throws JMSException {
        switch (node) {
            case "A": {
                producerA.send(message);
                break;
            }
            case "B": {
                producerB.send(message);
                break;
            }
            case "C": {
                producerC.send(message);
                break;
            }
            case "D": {
                producerD.send(message);
                break;
            }
            case "E": {
                producerE.send(message);
                break;
            }
            case "F": {
                producerF.send(message);
                break;
            }
            default: {
                System.out.println(nodeID + " is sending something into space...");
                break;
            }
        }
    }

    protected void sleepRandomTime() {
        try {
            Thread.sleep((rand.nextInt(5) + 1) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    protected Thread getAskingThread() {
        return new Thread(() -> {
            while (true) {
                sleepRandomTime();
                try {
                    if (!waitingForResponse) {
                        askForPermission();
                        waitingForResponse = true;
                    }
                } catch (JMSException jmsException) {
                    jmsException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run() {
        if (isCoord) {
            queueToCritical = new PriorityQueue<>();
        } else if (isCritical) {

        } else {
            getAskingThread().start();
        }
    }
}
