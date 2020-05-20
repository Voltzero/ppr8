package distcomp;

import javax.jms.JMSException;

public interface ParentNode {
    void sendEnAsRoot() throws JMSException;
}
