package distcomp;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;

public class NodeA extends BaseNode {

    public NodeA() throws JMSException, IOException {
        super();

        nodeID = "A";

        consumerA = session.createConsumer(a);
    }

    public NodeA(String COORD, String CRITICAL) throws JMSException {
        super();

        nodeID = "A";

        this.COORDINATOR = COORD;
        this.CRITICAL = CRITICAL;

        if (nodeID.equals(COORDINATOR))
            isCoord = true;
        if (nodeID.equals(CRITICAL))
            isCritical = true;

    }

    @Override
    public void run() {
        super.run();
        try {
            //getSendingThread().start();
            consumerA.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        super.onMessage(message);
    }

}
