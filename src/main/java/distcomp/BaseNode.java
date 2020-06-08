package distcomp;

import javax.jms.*;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private volatile boolean criticalAvailable = true;

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
                if (message.propertyExists("GetAccess")) {
                    synchronized (queueToCritical) {
                        queueToCritical.offer(message.getStringProperty("GetAccess"));
                        //sendReport("Queue: " + Arrays.toString(queueToCritical.toArray()));
                    }
                }
                if (message.propertyExists("Available")) {
                    criticalAvailable = true;
                }
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        } else if (isCritical) {
            try {
                if (message.propertyExists("Critical")) {
                    String node = message.getStringProperty("NodeID");
                    sendReport(message.getStringProperty("Critical"));
                    Thread.sleep(1500);
                    communicateCritical(node, nodeID + " says Hello to node " + node);
                    Thread.sleep(1500);
                    communicateCritical(node, nodeID + " says that's it for now to " + node + "\n");
                    Thread.sleep(500);
                    rejectAccess(node);
                    notifyCoord();
                }
            } catch (JMSException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (message.propertyExists("Response")) {
                    if (message.getBooleanProperty("Response")) {
                        communicateCritical(CRITICAL, nodeID + " says Hello to critical " + CRITICAL);
                    }
                }
                if (message.propertyExists("Critical")) {
                    sendReport(message.getStringProperty("Critical"));
                }
                if (message.propertyExists("Reject")) {
                    if (message.getBooleanProperty("Reject")) {
                        waitingForResponse = false;
                        sendReport(nodeID + " was rejected\n");
                    }
                }
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        }
    }

    private void askForPermission() throws JMSException {
        Message ms = session.createTextMessage();
        ms.setStringProperty("GetAccess", nodeID);
        messenger(ms, COORDINATOR);
    }

    private void grantAccess(String node) throws JMSException {
        Message ms = session.createTextMessage();
        ms.setBooleanProperty("Response", true);
        messenger(ms, node);
    }

    private void rejectAccess(String node) throws JMSException {
        Message ms = session.createTextMessage();
        ms.setBooleanProperty("Reject", true);
        messenger(ms, node);
    }

    private void sendReport(String s) throws JMSException {
        Message ms = session.createTextMessage(s);
        producerReport.send(ms);
    }

    private void notifyCoord() throws JMSException {
        Message ms = session.createTextMessage();
        ms.setBooleanProperty("Available", true);
        messenger(ms, COORDINATOR);
    }

    private void communicateCritical(String node, String message) throws JMSException {
        Message ms = session.createTextMessage();
        ms.setStringProperty("Critical", message);
        ms.setStringProperty("NodeID", nodeID);
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
            Thread.sleep((rand.nextInt(5) + 1) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    protected Thread getAskingThread() {
        return new Thread(() -> {
            while (true) {
                sleepRandomTime();
                if (!waitingForResponse) {
                    try {
                        askForPermission();
                        waitingForResponse = true;
                    } catch (JMSException jmsException) {
                        jmsException.printStackTrace();
                    }
                }
            }
        });
    }

    protected Thread getAvailabilityThread() {
        return new Thread(() -> {
            while (true) {
                if (criticalAvailable) {
                    synchronized (queueToCritical) {
                        if (!queueToCritical.isEmpty()) {
                            try {
                                grantAccess(queueToCritical.poll());
                                criticalAvailable = false;
                            } catch (JMSException jmsException) {
                                System.out.println("Problem z przydzielaniem dostępu do sekcji krytycznej.");
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run() {
        if (isCoord) {
            queueToCritical = new ConcurrentLinkedQueue<>();
            getAvailabilityThread().start();
        } else if (isCritical) {

        } else {
            getAskingThread().start();
        }
    }
}
