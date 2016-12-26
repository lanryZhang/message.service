package com.ifeng.core.distribute.handlers;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageFactory;
import com.ifeng.core.distribute.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

public class HeartBeatReqHanlder extends SimpleChannelInboundHandler<BaseMessage> {

	private volatile ScheduledFuture<?> heartBeatFuture;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
		if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.AUTH_RESP) {
			System.out.println("Get response");
			heartBeatFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 5, 5000, TimeUnit.MILLISECONDS);
		} else if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.HEART_RESP) {
			System.out.println("Get heart beat response");
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (heartBeatFuture != null) {
			heartBeatFuture.cancel(true);
			heartBeatFuture = null;
		}
		ctx.fireExceptionCaught(cause);
	}

	private class HeartBeatTask implements Runnable {

		private ChannelHandlerContext ctx;

		public HeartBeatTask(ChannelHandlerContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public void run() {
			BaseMessage heartBaseMessage = MessageFactory.createHeartBeatReqMessage();
			ctx.writeAndFlush(heartBaseMessage);
		}
	}
}
