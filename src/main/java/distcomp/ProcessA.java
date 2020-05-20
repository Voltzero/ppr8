package distcomp;

import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ProcessA extends Thread {

    private final Session session;
    private final Connection con;
    private final MessageProducer producerAB;
    private final MessageProducer producerAD;
    private final MessageConsumer consumerCA;
    private final MessageConsumer consumerBA;
    private FileWriter fileWriter;
    MessageProducer topicProducer;
    Random rand;
    private int tick = 0;

    public ProcessA() throws JMSException, IOException {
        rand = new Random();

        ConnectionFactory factory = JmsProvider.getConnectionFactory();
        this.con = factory.createConnection();
        con.start();

        this.session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue("A-B");
        this.producerAB = session.createProducer(queue);

        Queue queueAD = session.createQueue("A-D");
        this.producerAD = session.createProducer(queueAD);

        Queue queueCA = session.createQueue("C-A");
        consumerCA = session.createConsumer(queueCA);

        Queue queueBA = session.createQueue("B-A");
        consumerBA = session.createConsumer(queueBA);

        Topic topic = session.createTopic("ReportTopic");
        topicProducer = session.createProducer(topic);

        File file = new File("out.txt");
        fileWriter = new FileWriter(file);
    }

    private double[] generateNumbers() {
        double[] result = new double[100];
        for (int i = 0; i < 100; i++) {
            result[i] = rand.nextInt(900000) + 100000;
        }
        return result;
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
                double[] generate = generateNumbers();
                sendMessage("A generated numbers");
                sleepRandomTime();

                ObjectMessage ready = (ObjectMessage) consumerBA.receive();
                if (ready == null) {
                    break;
                }
                String mess = (String) ready.getObject();

                if(!mess.equalsIgnoreCase("ready")){
                    break;
                }

                ObjectMessage textMessage = session.createObjectMessage(generate);
                producerAB.send(textMessage);
                sendMessage("A send data to B");
                sleepRandomTime();

                ObjectMessage textMessageD = session.createObjectMessage(generate);
                producerAD.send(textMessageD);
                sendMessage("A send data to D");
                sleepRandomTime();

                ObjectMessage om = (ObjectMessage) consumerCA.receive();
                if (om == null) {
                    break;
                }
                double[] numbers = (double[]) om.getObject();
                sendMessage("A Recived Data From C");
                fileWriter.write(Arrays.toString(numbers));
                fileWriter.flush();
                sleepRandomTime();

            }
        } catch (JMSException | IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() throws JMSException {
        con.close();
    }
}
