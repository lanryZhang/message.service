/*
* ErrorExecutor.java 
* Created on  202016/12/21 18:14 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.consumer;

import com.ifeng.configurable.Context;
import com.ifeng.core.AbsDispatcher;
import com.ifeng.core.clean.CleanupAware;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ErrorExecutor implements Runnable ,CleanupAware{
    private ConcurrentLinkedDeque<Context> faildQueue = new ConcurrentLinkedDeque();
    private AbsDispatcher dispatcher;
    private int retries;
    private Logger logger = Logger.getLogger(ErrorExecutor.class);
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    public ErrorExecutor(int retries) {
        this.retries = retries;
    }

    public void setDispatcher(AbsDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void offer(Context context) {
        faildQueue.offer(context);
    }

    @Override
    public void run() {
        while (true) {
            if (countDownLatch.getCount() <= 0){
                faildQueue.clear();
                break;
            }
            try {
                Context context = faildQueue.poll();
                if (context != null) {
                    for (int i = 0; i < retries; i++) {
                        try {
                            Object res = dispatcher.dispatch(context);
                            if (res instanceof Boolean) {
                                if ((Boolean) res) {
                                    break;
                                }
                            }
                        }finally {

                        }
                    }
                } else {
                    Thread.currentThread().sleep(5000);
                }
            }catch (Exception er) {
                logger.error(er);
            }
        }

    }

    @Override
    public void cleanup() {
        countDownLatch.countDown();
    }
}
