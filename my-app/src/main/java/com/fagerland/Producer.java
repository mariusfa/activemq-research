package com.fagerland;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.UUID;


public class Producer implements Runnable {

    private String queueTopicName;
    private boolean isQueue; // false means topic

    public Producer(String queueTopicName, boolean isQueue) {
        this.queueTopicName = queueTopicName;
        this.isQueue = isQueue;
    }

    @Override
    public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination;
            if (isQueue) {
                destination = session.createQueue(queueTopicName);
            } else {
                destination = session.createTopic(queueTopicName);
            }

            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            System.out.println(System.currentTimeMillis());
            for (int i = 0; i < 10000; i++) {
                TextMessage textMessage = session.createTextMessage(String.valueOf(i) + ":messageCount " + ": " + UUID.randomUUID());
                producer.send(textMessage);
            }

            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }

    }
}
