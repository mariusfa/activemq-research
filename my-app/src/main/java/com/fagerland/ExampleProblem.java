package com.fagerland;

public class ExampleProblem {

    public static final String TOPIC_NAME = "topic1";
    public static final String QUEUE_NAME = "queue1";

    public static void run() throws InterruptedException {
        thread(new Consumer(QUEUE_NAME, true, "worker-1"), false);
        thread(new Consumer(QUEUE_NAME, true, "worker-2"), false);
        thread(new Consumer(QUEUE_NAME, true, "worker-3"), false);
        thread(new Consumer(TOPIC_NAME, false, "arkiv"), false);
        thread(new ForwardTopicToQueue(TOPIC_NAME, QUEUE_NAME), false);
        Thread.sleep(100);
        thread(new Producer(TOPIC_NAME, false), false);
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

}
