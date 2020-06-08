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

        consumerC = session.createConsumer(c);
        this.COORDINATOR = COORD;
        this.CRITICAL = CRITICAL;

        if (nodeID.equals(COORDINATOR))
            isCoord = true;
        if (nodeID.equals(CRITICAL))
            isCritical = true;

    }

    public NodeC(String COORD) throws JMSException {
        super();

        nodeID = "C";

        consumerC = session.createConsumer(c);
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
            consumerC.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
