package distcomp;

import javax.jms.*;
import java.util.*;

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
    protected Map<String, Map<String, Integer>> topologyMap = null;
    protected Map<String, Integer> distances;
    protected List<ArrayList<String>> routes = null;
    protected Dijkstra dijkstra;

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
        if (topologyMap == null) {
            handleECHO(message);
        } else {
            try {
                routeDijkstra(message);
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        }
    }

    private void routeDijkstra(Message message) throws JMSException {
        if (message.getStringProperty("ReceiverID").equals(nodeID)) {
            System.out.println(nodeID + " successfully received message from: " + message.getStringProperty("SenderID") + " message root was: " + message.getStringProperty("RootID"));
        } else {
            System.out.println(nodeID + " received from: " + message.getStringProperty("SenderID") + ", sending forward to: " + message.getStringProperty("ReceiverID"));
            sendToNode(message.getStringProperty("RootID"), message.getStringProperty("ReceiverID"));
        }
    }

    protected void setDistances() {
        distances.put(nodeID, 0);
        Map<String, Integer> dist = topologyMap.get(nodeID);
        Iterator it = dist.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            distances.put(((String) pair.getKey()), ((Integer) pair.getValue()));
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

    public void sendToNode(String rootID, String receiverID) throws JMSException {
        if (routes == null)
            routes = dijkstra.calculateShortestPaths(nodeID);

        Message ms = session.createTextMessage();
        ms.setStringProperty("RootID", rootID);
        ms.setStringProperty("SenderID", nodeID);
        ms.setStringProperty("ReceiverID", receiverID);

        String node = "";
        String previousNode = "";
        do {
            node = routes.get(Dijkstra.getNodeIndex(receiverID)).get(routes.get(Dijkstra.getNodeIndex(receiverID)).size() - 1);
        } while (!node.equals(nodeID));

        producerMaster.send(ms); // DO ZMIANY !!!!!!!!!!
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

    private void handleECHO(Message message) {
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
