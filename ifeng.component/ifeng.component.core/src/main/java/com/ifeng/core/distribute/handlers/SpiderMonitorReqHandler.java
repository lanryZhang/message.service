package com.ifeng.core.distribute.handlers;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageFactory;
import com.ifeng.core.distribute.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhanglr on 2016/9/8.
 */
public class SpiderMonitorReqHandler extends ChannelInboundHandlerAdapter {

    private volatile ScheduledFuture<?> monitorFuture;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (monitorFuture == null){
            monitorFuture = ctx.executor().scheduleAtFixedRate(new MonitorTask(ctx),5, 60 * 1000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        BaseMessage msg = (BaseMessage) obj;
        if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.MONITOR_RESP) {
            //System.err.println("get monitor response");
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (monitorFuture != null) {
            monitorFuture.cancel(true);
            monitorFuture = null;
        }
        //ctx.fireExceptionCaught(cause);
        cause.printStackTrace();
    }

    class MonitorTask implements Runnable{

        private ChannelHandlerContext ctx;

        public MonitorTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            BaseMessage monitorReqMessage = MessageFactory.createMonitorReqMessage();
            ctx.writeAndFlush(monitorReqMessage);
        }
    }
}
