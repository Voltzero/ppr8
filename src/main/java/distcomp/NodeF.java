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
            consumerF.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
