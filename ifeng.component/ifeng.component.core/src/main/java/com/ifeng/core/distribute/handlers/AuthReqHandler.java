package com.ifeng.core.distribute.handlers;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageFactory;
import com.ifeng.core.distribute.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * Created by zhanglr on 2016/8/29.
 */
public class AuthReqHandler extends SimpleChannelInboundHandler<BaseMessage>{
    private Logger log = Logger.getLogger(AuthReqHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(MessageFactory.createLoginReqMessage(0));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }

            if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.AUTH_RESP) {
                byte body = Byte.parseByte(msg.getBody().toString());
                if (body != 0) {
                    ctx.close();
                } else {
                    System.out.println("Auth success!");
                    ctx.fireChannelRead(msg);
                }
            } else {
                ctx.fireChannelRead(msg);
            }
        }catch (Exception e){
            log.error(e);
        }
    }
}
