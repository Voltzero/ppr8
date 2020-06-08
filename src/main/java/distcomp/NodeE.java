package distcomp;

import javax.jms.JMSException;
import java.io.IOException;

public class NodeE extends BaseNode {

    public NodeE() throws JMSException, IOException {
        super();

        nodeID = "E";

        consumerE = session.createConsumer(e);
    }

    public NodeE(String COORD, String CRITICAL) throws JMSException {
        super();

        nodeID = "E";

        consumerE = session.createConsumer(e);
        this.COORDINATOR = COORD;
        this.CRITICAL = CRITICAL;

        if (nodeID.equals(COORDINATOR))
            isCoord = true;
        if (nodeID.equals(CRITICAL))
            isCritical = true;

    }

    public NodeE(String COORD) throws JMSException {
        super();

        nodeID = "E";

        consumerE = session.createConsumer(e);
        this.COORDINATOR = COORD;

        if (nodeID.equals(COORDINATOR)) {
            isCoord = true;
            this.weight = 1d;
        } else
            this.weight = 0d;

    }

    @Override
    public void run() {
        super.run();
        try {
            //getSendingThread().start();
            consumerE.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
