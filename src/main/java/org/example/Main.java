package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        RingBuffer<Integer> buffer = new RingBuffer<>(5);
        int max = 20;

        CountDownLatch producerDone = new CountDownLatch(1);
        CountDownLatch consumerDone = new CountDownLatch(1);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < max; i++) {
                    buffer.put(i);
                    log.info("Produced: {}", i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                producerDone.countDown();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < max; i++) {
                    int value = buffer.take();
                    log.info("Consumed: {}", value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                consumerDone.countDown();
            }
        });

        producer.start();
        consumer.start();

        producerDone.await();
        consumerDone.await();
    }
}