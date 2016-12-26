package com.ifeng.core.distribute.handlers;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageFactory;
import com.ifeng.core.distribute.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhanglr on 2016/8/29.
 */
public class AuthRespHandler extends ChannelInboundHandlerAdapter {

    private static ConcurrentHashMap<String, Boolean> loginNode = new ConcurrentHashMap<String, Boolean>();
    private String[] ips = { "36.110.\\d+.\\d+","210.51.19.2", "127.0.0.1", "172.31.0.7", "172.31.0.6" ,"123.103.93.240"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        BaseMessage msg = (BaseMessage)obj;
        if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.AUTH_REQ) {
            System.out.println("Get LOGIN_REQ ");
            String remoteIp = ctx.channel().remoteAddress().toString();
            BaseMessage loginResp = null;
            System.out.println(remoteIp);
            if (loginNode.containsKey(remoteIp)) { // 已登陆
                loginResp = MessageFactory.createLoginReqMessage(-1);
                System.out.println("Login in");
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                System.out.println(ip);
                boolean isOk = false;
                for (String item : ips) {
                    if (ip.matches(item)) {
                        isOk = true;
                        break;
                    }
                }
                loginResp = isOk ? MessageFactory.createLoginResMessage(0) : MessageFactory.createLoginResMessage(-1);
                if (isOk) {
                    System.out.println("Login in isok=" + isOk);
                    loginNode.put(remoteIp, true);
                }
            }
            ctx.writeAndFlush(loginResp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
