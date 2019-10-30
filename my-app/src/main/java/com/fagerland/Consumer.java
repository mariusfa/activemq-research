package com.fagerland;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Consumer implements Runnable, ExceptionListener {

    private String queueTopicName;
    private boolean isQueue; // false means topic
    private String name;

    public Consumer(String queueTopicName, boolean isQueue, String name) {
        this.queueTopicName = queueTopicName;
        this.isQueue = isQueue;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination;
            if (isQueue) {
                destination = session.createQueue(queueTopicName);
            } else {
                destination = session.createTopic(queueTopicName);
            }

            MessageConsumer consumer = session.createConsumer(destination);

            while (true) {
//                System.out.println("Waiting for message.");
                Message message = consumer.receive();
                if (message == null) {
                    break;
                }
                String textMessage = ((TextMessage)message).getText();
                if (textMessage.contains("9999:messageCount")) {
                    System.out.println(System.currentTimeMillis());
                }
                //System.out.println("Consumer got message: " + ((TextMessage)message).getText() + " : " + name);
            }
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
