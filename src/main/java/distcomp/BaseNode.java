package distcomp;

import javax.jms.*;
import java.util.Random;

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
    private volatile boolean waitingForResponse = false;
    private volatile boolean criticalAvailable = true;
    protected volatile double weight;

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
            try {
                if (message.propertyExists("Done")) {
                    this.weight += message.getDoubleProperty("Done");
                    sendReport("Coordinator " + nodeID + " has received DONE from " + message.getStringProperty("NodeID") + " with weight: " + message.getDoubleProperty("Done"));
                    if (weight == 1d) {
                        sendReport("\n\t\t\t\t\tCoordinator " + nodeID + " has finished distributed calculating... Let's do another round\n");
                        getJobThread().start();
                    }
                }
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        } else {
            try {
                if (message.propertyExists("Job")) {
                    this.weight = message.getDoubleProperty("Job");
                    sendReport(nodeID + " has received JOB with weight " + weight);
                    sleepRandomTime();
                    notifyCoord();
                }
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        }
    }

    private void sendReport(String s) throws JMSException {
        Message ms = session.createTextMessage(s);
        producerReport.send(ms);
    }

    private void notifyCoord() throws JMSException {
        Message ms = session.createTextMessage();
        ms.setDoubleProperty("Done", weight);
        ms.setStringProperty("NodeID", nodeID);
        messenger(ms, COORDINATOR);
        this.weight = 0;
    }

    private void giveJob(String node) throws JMSException {
        double w;
        do {
            w = rand.nextDouble();
        } while (w >= weight);
        this.weight -= w;
        Message ms = session.createTextMessage();
        ms.setDoubleProperty("Job", w);
        messenger(ms, node);
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
            Thread.sleep((rand.nextInt(6) + 2) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    protected Thread getJobThread() {
        return new Thread(() -> {
            String[] nodes = {"A", "B", "C", "D", "E", "F"};
            sleepRandomTime();
            for (String n : nodes) {
                try {
                    if (!n.equals(COORDINATOR))
                        giveJob(n);
                    Thread.sleep(1800);
                } catch (JMSException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run() {
        if (isCoord) {
            getJobThread().start();
        } else {
            ;
        }
    }
}
