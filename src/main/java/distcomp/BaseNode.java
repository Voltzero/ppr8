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
    protected String[] previousNode = null;
    protected Dijkstra dijkstra;
    protected MaxID maxID = null;
    protected int diameter;
    protected boolean floodMax = false;

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
        } else if (maxID == null) {
            try {
                routeDijkstra(message);
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        } else {
            try {
                floodMax(message);
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        }
    }

    private void floodMax(Message message) throws JMSException {
        String msRoot = message.getStringProperty("RootID");
        String node = message.getStringProperty("NodeID");
        int lvl = message.getIntProperty("NodeLVL");
        int count = message.getIntProperty("Countdown");

        if (count > 1)
            floodNodes(nodeID, node, lvl, count - 1, msRoot);

        if (!node.equals(this.nodeID)) {
            if (maxID.getNodeLvl() < lvl) {
                maxID.setNodeID(node);
                maxID.setNodeLvl(lvl);
                System.out.println("Node " + nodeID + " received flood from " + msRoot + " and now has new MAXID: [ " + node + ", " + lvl + " ]");
                floodNodes(nodeID, node, lvl, diameter, msRoot);
            }
        }

    }

    public void floodNodes(String rootID, String nodeID, int nodeLvl, int countdown) throws JMSException {
        Message ms = session.createTextMessage();
        ms.setStringProperty("RootID", rootID);
        ms.setStringProperty("NodeID", nodeID);
        ms.setIntProperty("NodeLVL", nodeLvl);
        ms.setIntProperty("Countdown", countdown);

        for (String s : previousNode)
            if (isNeighbour(s))
                messenger(ms, s);
    }

    public void floodNodes(String rootID, String nodeID, int nodeLvl, int countdown, String except) throws JMSException {
        Message ms = session.createTextMessage();
        ms.setStringProperty("RootID", rootID);
        ms.setStringProperty("NodeID", nodeID);
        ms.setIntProperty("NodeLVL", nodeLvl);
        ms.setIntProperty("Countdown", countdown);

        for (String s : previousNode)
            if (isNeighbour(s) && !s.equals(except))
                messenger(ms, s);
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
        if (previousNode == null)
            previousNode = dijkstra.calculateShortestPaths(nodeID);

        Message ms = session.createTextMessage();
        ms.setStringProperty("RootID", rootID);
        ms.setStringProperty("SenderID", nodeID);
        ms.setStringProperty("ReceiverID", receiverID);

        if (previousNode[Dijkstra.getNodeIndex(receiverID)] != null) {
            String node = previousNode[Dijkstra.getNodeIndex(receiverID)];

            if (node.equals(nodeID)) {
                messenger(ms, receiverID);  // send directly to "receiver"
            } else if (isNeighbour(node)) {
                messenger(ms, node);        // send to neighbour node, it should belong to the best possible route to "receiver"
            } else {
                String previous = "";       // seek for neighbour node that leads to the "receiver"
                do {
                    previous = node;
                    node = previousNode[Dijkstra.getNodeIndex(node)];
                } while (!node.equals(nodeID));

                messenger(ms, previous);
            }
        } else {
            System.out.println(receiverID + " is unreachable from " + nodeID);
        }
    }

    private boolean isNeighbour(String node) {
        return topologyMap.get(nodeID).entrySet().stream().anyMatch(entry -> (entry).getKey().equals(node));
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
            Thread.sleep((rand.nextInt(5) + 1) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    protected void generateMaxID() {
        maxID = new MaxID(nodeID, rand.nextInt(19) + 1);
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

    protected Thread getSendingThread() {
        return new Thread(() -> {
            while (true) {
                Random r = new Random();
                int los = r.nextInt(100);
                if (los > 70) {
                    r = new Random();
                    String rNode = Dijkstra.getNodeIDfromIndex(r.nextInt(5));
                    if (!rNode.equals(nodeID)) {
                        try {
                            sendToNode(nodeID, rNode);
                        } catch (JMSException jmsException) {
                            jmsException.printStackTrace();
                        }
                    }
                }
                sleepRandomTime();
            }
        });
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
        if (floodMax) {
            try {
                floodNodes(nodeID, maxID.getNodeID(), maxID.getNodeLvl(), diameter);
                floodMax = false;
                sleepRandomTime();
            } catch (JMSException jmsException) {
                jmsException.printStackTrace();
            }
        }
    }
}
