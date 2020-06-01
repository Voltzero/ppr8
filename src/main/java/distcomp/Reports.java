package distcomp;

import javax.jms.*;

public class Reports extends Thread implements MessageListener {

    protected Session session;
    protected Connection con;
    private MessageConsumer consumer;
    protected Topic report;

    public Reports() throws JMSException {
        ConnectionFactory factory = JmsProvider.getConnectionFactory();
        this.con = factory.createConnection();
        con.start();

        this.session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        report = session.createTopic("Report");

        consumer = session.createConsumer(report);
    }

    @Override
    public void onMessage(Message message) {

    }

    @Override
    public void run() {
        try {
            consumer.setMessageListener(this);
            while (true) {
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
