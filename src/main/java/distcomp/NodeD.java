package distcomp;

import javax.jms.JMSException;
import java.io.IOException;

public class NodeD extends BaseNode {

    public NodeD() throws JMSException, IOException {
        super();

        nodeID = "D";

        consumerD = session.createConsumer(d);
    }

    public NodeD(String COORD, String CRITICAL) throws JMSException {
        super();

        nodeID = "D";

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
            consumerD.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
