package distcomp;

import javax.jms.*;
import java.util.Random;

public class ProcessC extends Thread {

    private final Session session;
    private final Connection con;
    private final MessageProducer producerCA;
    private final MessageConsumer consumerBC;
    private final MessageConsumer consumerDC;
    MessageProducer topicProducer;
    Random rand;
    private int tick = 0;

    public ProcessC() throws JMSException {
        rand = new Random();
        ConnectionFactory factory = JmsProvider.getConnectionFactory();
        this.con = factory.createConnection();
        con.start();

        this.session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue("C-A");
        producerCA = session.createProducer(queue);
        Queue queueBC = session.createQueue("B-C");
        consumerBC = session.createConsumer(queueBC);
        Queue queueDC = session.createQueue("D-C");
        consumerDC = session.createConsumer(queueDC);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);
    }

    private void sleepRandomTime() {
        try {
            Thread.sleep((rand.nextInt(4) + 1) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessage(String message) throws JMSException {
        TextMessage textMessage = session.createTextMessage(message);
        topicProducer.send(textMessage);
    }
    @Override
    public void run() {
        try {
            while (true) {
                ObjectMessage tm = (ObjectMessage) consumerBC.receive();

                if (tm == null) {
                    break;
                }
                double[] numbers = (double[]) tm.getObject();

                sendMessage("C Recived Data From B");
                sleepRandomTime();

                ObjectMessage tmD = (ObjectMessage) consumerDC.receive();
                if (tmD == null) {
                    break;
                }
                double[] numbersD = (double[]) tmD.getObject();

                sendMessage("C Recived Data From D");
                sleepRandomTime();

                for (int i = 0; i< numbers.length; i++){
                    numbersD[i] -= numbers[i];
                }

                sendMessage("C Calculated D-B");
                sleepRandomTime();

                ObjectMessage objectMessage = session.createObjectMessage(numbersD);
                producerCA.send(objectMessage);
                sendMessage("C send data to A");
                sleepRandomTime();


            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void destroy() throws JMSException {
        con.close();
    }
}
