package com.ifeng.core.distribute.handlers;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageFactory;
import com.ifeng.core.distribute.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HeartBeatRespHandler extends SimpleChannelInboundHandler<BaseMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
		if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.HEART_REQ) {
			System.out.println("get heartbeat request");
			BaseMessage heartBaseMessage = MessageFactory.createHeartBeatRespMessage();
			ctx.writeAndFlush(heartBaseMessage);
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}
}
