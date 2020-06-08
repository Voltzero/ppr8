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

        consumerD = session.createConsumer(d);
        this.COORDINATOR = COORD;
        this.CRITICAL = CRITICAL;

        if (nodeID.equals(COORDINATOR))
            isCoord = true;
        if (nodeID.equals(CRITICAL))
            isCritical = true;

    }

    public NodeD(String COORD) throws JMSException {
        super();

        nodeID = "D";

        consumerD = session.createConsumer(d);
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
            consumerD.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
