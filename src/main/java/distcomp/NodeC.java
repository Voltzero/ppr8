package distcomp;

import javax.jms.JMSException;
import java.io.IOException;

public class NodeC extends BaseNode {

    public NodeC() throws JMSException, IOException {
        super();

        nodeID = "C";

        consumerC = session.createConsumer(c);
    }

    public NodeC(String COORD, String CRITICAL) throws JMSException {
        super();

        nodeID = "C";

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
            consumerC.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
