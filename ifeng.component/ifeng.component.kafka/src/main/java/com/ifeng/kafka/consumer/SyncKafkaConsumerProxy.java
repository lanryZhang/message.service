/*
* KafkaConsumer.java 
* Created on  202016/12/12 13:41 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.consumer;

import com.ifeng.configurable.Context;
import com.ifeng.core.AbsDispatcher;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class SyncKafkaConsumerProxy<K,V> extends KafkaConsumer<K,V> implements Callable{
    private Collection<String> topics;
    private AbsDispatcher dispatcher;
    private int retries = 3;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private Logger logger = Logger.getLogger(SyncKafkaConsumerProxy.class);
    private ExecutorService failedExecutorService = Executors.newFixedThreadPool(1);
    private ErrorExecutor errorExecutor ;

    public SyncKafkaConsumerProxy(Context context) {
        super(context.getContext());
        this.topics = (Collection<String>) context.getObject("topics");
        this.retries = context.getInt("retries",3);
        Thread.currentThread().setName("SyncKafkaConsumerProxy---"+Thread.currentThread().getId());
        errorExecutor = new ErrorExecutor(retries);
    }

    public void setDispatcher(AbsDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        errorExecutor.setDispatcher(dispatcher);
    }

    @Override
    public Object call() throws Exception {
        if (null == dispatcher){
            throw new NullPointerException("dispatch can not be null.");
        }
        try {
            failedExecutorService.submit(errorExecutor);
            this.subscribe(topics);

            while (true) {
                ConsumerRecords<K, V> records = this.poll(Long.MAX_VALUE);
                if (doCommit())
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
                            logger.error(e + " Sync process error:"+record);
                            errorExecutor.offer(context);
                        }
                    });
            }
        } catch (WakeupException e) {
            logger.error(Thread.currentThread().getName()+" --- get wakeup signal, close this consumer.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        } finally {
            this.close();
            shutdownLatch.countDown();
            errorExecutor.cleanup();
            if (!failedExecutorService.isShutdown()) {
                failedExecutorService.shutdown();
            }
        }
        return shutdownLatch;
    }

    private boolean doCommit(){
        try {
            this.commitSync();
            return true;
        } catch (CommitFailedException e) {
            logger.debug("Commit failed ", e);
            return false;
        }
    }
}