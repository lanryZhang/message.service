/*
* ProducerEx.java 
* Created on  202016/11/30 9:48 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.producer;


import com.ifeng.kafka.exceptions.EmptyWorkThreadException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.ifeng.kafka.constances.KafkaConstances.BATCH_SIZE;
import static com.ifeng.kafka.constances.KafkaConstances.FLUSH_TIMEOUT;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class KafkaProducerEx extends KafkaProducer {
    private int batchSize = 1000;
    private int timeout = 3000;
    private long lastFlushTime = -1;
    private String topicName;
    private List<Future<RecordMetadata>> futures = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(KafkaProducerEx.class);
    private ConcurrentLinkedDeque<ProducerRecord> queue = new ConcurrentLinkedDeque<>();
    private AtomicInteger aliveWorker = new AtomicInteger(0);
    private CountDownLatch sendWorkCountDownLatch = new CountDownLatch(1);
    private ExecutorService executorService;
    private long maxBlockingSize = 10000000L;
    private long sendThreshold = maxBlockingSize / 2;
    private AtomicLong queueSize = new AtomicLong(0);

    public KafkaProducerEx(Map<String, Object> configs) {
        super(configs);
        batchSize = configs.get(BATCH_SIZE) == null ? batchSize : Integer.valueOf(configs.get(BATCH_SIZE).toString());
        timeout = configs.get(FLUSH_TIMEOUT) == null ? timeout : Integer.valueOf(configs.get(FLUSH_TIMEOUT).toString());
        executorService = Executors.newFixedThreadPool(20);
        start();
    }

    public KafkaProducerEx(Map<String, Object> configs, String topicName) {
        super(configs);
        batchSize = configs.get(BATCH_SIZE) == null ? batchSize : Integer.valueOf(configs.get(BATCH_SIZE).toString());
        timeout = configs.get(FLUSH_TIMEOUT) == null ? timeout : Integer.valueOf(configs.get(FLUSH_TIMEOUT).toString());
        executorService = Executors.newFixedThreadPool(20);
        this.topicName = topicName;
        start();
    }

    private boolean timeout() {
        return System.currentTimeMillis() - lastFlushTime > timeout;
    }

    public void start() {
        executorService.submit(new SendRecord());
        aliveWorker.incrementAndGet();
    }

    class SendRecord implements Runnable {
        @Override
        public void run() {
            ProducerRecord record = null;
            try {
                while (sendWorkCountDownLatch.getCount() > 0) {
                    try {
                        for (int processedEvents = 0; processedEvents < batchSize; processedEvents += 1) {

                            record = queue.poll();
                            if (record == null) {
                                logger.info("record is null,topic --" + topicName);
                                break;
                            }
                            queueSize.decrementAndGet();
                            try {
                                futures.add(send(record, new ProducerCallback()));
                            } catch (Exception ex) {
                                try {
                                    futures.add(send(record, new ProducerCallback()));
                                } catch (Exception e) {
                                    throw new Exception("send error");
                                }
                                logger.error(ex);
                            }
                        }
                        if (futures.size() > 0) {
                            flush();
                            logger.info("flush logs " + futures.size());
                            try {
                                futures.forEach(k -> {
                                    try {
                                        k.get();
                                    } catch (InterruptedException e) {
                                        logger.error(e);
                                    } catch (ExecutionException e) {
                                        logger.error(e);
                                    }
                                });
                            } catch (Exception e) {
                                logger.error(e);
                            } finally {
                                futures.clear();
                            }
                        }else{
                            logger.info("flush logs 0");
                            Thread.currentThread().sleep(1000);
                        }
                    } catch (Exception e) {
                        sendWorkCountDownLatch.countDown();
                        logger.error(e);
                    }
                }
            } finally {
                aliveWorker.decrementAndGet();
            }

        }
    }

    public void sendBatch(ProducerRecord record) {
        synchronized (queue) {
            queue.push(record);
        }
    }

    public int getAliveWorkCount() {
        return aliveWorker.get();
    }

    public void sendBatch(String record) {
        if (null == topicName) {
            throw new NullPointerException("topic name can not be null.");
        }
        if (null == record) {
            return;
        }

        try {
            if (queueSize.get() >= maxBlockingSize) {
                logger.error("queueSize:" + queueSize.get());
                Thread.currentThread().sleep(1000);
            }
        } catch (InterruptedException er) {
            logger.error(er);
        }

        if (queue.offer(new ProducerRecord<>(topicName, record))) {
            queueSize.incrementAndGet();
        }else{
            logger.error("offer queue error:"+record);
        }
    }
}

class ProducerCallback implements Callback {
    private static final Logger logger = Logger.getLogger(ProducerCallback.class);

    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            logger.error("Error sending message to Kafka {} ", exception);
        }
    }
}
