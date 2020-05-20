/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distcomp;

import java.util.Arrays;
import java.util.Random;
import javax.jms.*;

public class ProcessB extends Thread {

    private final Connection con;
    Session session;
    MessageConsumer consumerAB;
    MessageProducer producerBC;
    MessageProducer producerBA;
    MessageProducer topicProducer;
    Random rand;
    private int tick = 0;

    public ProcessB() throws JMSException {
        rand = new Random();
        ConnectionFactory factory = JmsProvider.getConnectionFactory();
        this.con = factory.createConnection();
        con.start();

        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queueAB = session.createQueue("A-B");
        consumerAB = session.createConsumer(queueAB);

        Queue queueBC = session.createQueue("B-C");
        producerBC = session.createProducer(queueBC);

        Queue queueBA = session.createQueue("B-A");
        producerBA = session.createProducer(queueBA);

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
                ObjectMessage objectMessageA = session.createObjectMessage("ready");
                producerBA.send(objectMessageA);
                sendMessage("B is ready");
                sleepRandomTime();

                ObjectMessage tm = (ObjectMessage) consumerAB.receive();
                if (tm == null) {
                    break;
                }
                double[] numbers = (double[]) tm.getObject();
                sendMessage("B Recived Data From A");
                sleepRandomTime();

                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = Math.log(numbers[i]);
                }
                sendMessage("B Calculated Log(n)");
                sleepRandomTime();

                ObjectMessage objectMessage = session.createObjectMessage(numbers);
                producerBC.send(objectMessage);
                sendMessage("B send data to C");
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
