package distcomp;

import javax.jms.*;
import java.util.Random;

public class ProcessD extends Thread {

    private final Session session;
    private final Connection con;
    private final MessageProducer producerDC;
    private final MessageConsumer consumerAD;
    MessageProducer topicProducer;
    Random rand;
    private int tick = 0;

    public ProcessD() throws JMSException {
        rand = new Random();
        ConnectionFactory factory = JmsProvider.getConnectionFactory();
        this.con = factory.createConnection();
        con.start();

        this.session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue("D-C");
        producerDC = session.createProducer(queue);
        Queue queueBC = session.createQueue("A-D");
        consumerAD = session.createConsumer(queueBC);

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
                ObjectMessage tm = (ObjectMessage) consumerAD.receive();
                if (tm == null) {
                    break;
                }
                double[] numbers = (double[]) tm.getObject();
                sendMessage("D Recived Data From A");
                sleepRandomTime();

                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] *= numbers[i];
                }
                sendMessage("D Squared Numbers");
                sleepRandomTime();

                ObjectMessage objectMessage = session.createObjectMessage(numbers);
                producerDC.send(objectMessage);
                sendMessage("D send data to C");
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
