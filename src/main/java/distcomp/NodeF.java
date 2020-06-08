package distcomp;

import javax.jms.JMSException;
import java.io.IOException;

public class NodeF extends BaseNode {

    public NodeF() throws JMSException, IOException {
        super();

        nodeID = "F";

        consumerF = session.createConsumer(f);
    }

    public NodeF(String COORD, String CRITICAL) throws JMSException {
        super();

        nodeID = "F";

        consumerF = session.createConsumer(f);
        this.COORDINATOR = COORD;
        this.CRITICAL = CRITICAL;

        if (nodeID.equals(COORDINATOR))
            isCoord = true;
        if (nodeID.equals(CRITICAL))
            isCritical = true;

    }

    public NodeF(String COORD) throws JMSException {
        super();

        nodeID = "F";

        consumerF = session.createConsumer(f);
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
            consumerF.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
