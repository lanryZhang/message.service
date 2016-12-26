/*
* KafkaConsumer.java 
* Created on  202016/12/12 13:41 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.consumer;

import com.ifeng.configurable.Context;
import com.ifeng.core.AbsDispatcher;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class AsyncKafkaConsumerProxy<K,V> extends KafkaConsumer<K,V> implements Callable{
    private Collection topics;
    private AbsDispatcher dispatcher;
    private int retries = 3;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private Logger logger = Logger.getLogger(SyncKafkaConsumerProxy.class);
    private ExecutorService failedExecutorService = Executors.newFixedThreadPool(1);
    private ErrorExecutor errorExecutor ;

    public AsyncKafkaConsumerProxy(Context context) {
        super(context.getContext());
        this.topics = (Collection<String>) context.getObject("topics");
        this.retries = context.getInt("retries",3);
        errorExecutor = new ErrorExecutor(retries);
    }

    @Override
    public Object call() throws Exception {
        if (null == dispatcher){
            throw new NullPointerException("dispatch can not be null.");
        }

        try {
            failedExecutorService.submit(errorExecutor);

            this.subscribe(topics, new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    doCommitSync();
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {}
            });

            while (true) {
                ConsumerRecords<K, V> records = this.poll(Long.MAX_VALUE);
                records.forEach(record -> {
                    Context context = new Context();
                    context.put("data", record);
                    try {
                        Object res = dispatcher.dispatch(context);
                        if (res instanceof Boolean){
                            if (!(Boolean) res){
                                errorExecutor.offer(context);
                            }
                        }
                    }catch (Exception e){
                        logger.error(e+"Async process error:"+record);
                        errorExecutor.offer(context);
                    }
                });
                this.commitAsync();
            }
        } catch (WakeupException e) {
            logger.error(Thread.currentThread().getName() + " --- get wakeup signal, close this consumer.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        } finally {
            try {
                doCommitSync();
            } finally {
                this.close();
                shutdownLatch.countDown();
            }
            errorExecutor.cleanup();
            if (!failedExecutorService.isShutdown()) {
                failedExecutorService.shutdown();
            }
        }
        return shutdownLatch;
    }

    public void setDispatcher(AbsDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        errorExecutor.setDispatcher(dispatcher);
    }

    private void doCommitSync() {
        try {
            this.commitSync();
        } catch (WakeupException e) {
            // we're shutting down, but finish the commit first and then
            // rethrow the exception so that the main loop can exit
            doCommitSync();
            throw e;
        } catch (CommitFailedException e) {
            // the commit failed with an unrecoverable error. if there is any
            // internal state which depended on the commit, you can clean it
            // up here. otherwise it's reasonable to ignore the error and go on
            logger.debug("Commit failed", e);
        }
    }
}