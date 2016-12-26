package com.ifeng.core.distribute.handlers;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageFactory;
import com.ifeng.core.distribute.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by zhanglr on 2016/9/8.
 */
public class SpiderMonitorRespHandler extends SimpleChannelInboundHandler<BaseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        if (msg.getHeader().getType() == MessageType.MONITOR_REQ){
           // SpiderMonitor.analysis();
            ctx.writeAndFlush(MessageFactory.createMonitorRespMessage(null));
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //ctx.fireExceptionCaught(cause);
        cause.printStackTrace();
    }
}
