package distcomp;

import javax.jms.JMSException;
import java.io.IOException;

public class NodeB extends BaseNode {

    public NodeB() throws JMSException, IOException {
        super();

        nodeID = "B";

        consumerB = session.createConsumer(b);
    }

    public NodeB(String COORD, String CRITICAL) throws JMSException {
        super();

        nodeID = "B";

        consumerB = session.createConsumer(b);
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
            consumerB.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
