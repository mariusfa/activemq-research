package com.fagerland;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ForwardTopicToQueue implements Runnable, ExceptionListener {

    private String topicName;
    private String queueName;

    public ForwardTopicToQueue(String topicName, String queueName) {
        this.topicName = topicName;
        this.queueName = queueName;
    }

    @Override
    public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination topicDestination = session.createTopic(topicName);
            Destination queueDestinaation = session.createQueue(queueName);

            MessageConsumer consumer = session.createConsumer(topicDestination);
            MessageProducer producer = session.createProducer(queueDestinaation);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            while (true) {
//                System.out.println("Waiting for message.");
                Message message = consumer.receive();
                if (message == null) {
                    break;
                }
                producer.send(message);
                //System.out.println("Forwarder got/sent message: " + ((TextMessage)message).getText() + " : " + Thread.currentThread().getName());
            }
            producer.close();
            consumer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }

    }

    @Override
    public void onException(JMSException e) {
        System.out.println("JMS Exception occured. Shutting down client.");
    }
}
