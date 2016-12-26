package com.ifeng.core.distribute.master;

import com.ifeng.core.distribute.codec.MessageDecode;
import com.ifeng.core.distribute.codec.MessageEncode;
import com.ifeng.core.distribute.handlers.AuthRespHandler;
import com.ifeng.core.distribute.handlers.SpiderMonitorReqHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;


/**
 * Created by zhanglr on 2016/8/28.
 */
public class Master {
    private String hostIp = "0.0.0.0";
    private int port = 27018;

    public void start() {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        EventLoopGroup masterGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(masterGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    //.option(ChannelOption.SO_LINGER, -1)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 1024)
                    .option(ChannelOption.SO_SNDBUF, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        protected void initChannel(Channel channel) {
                            channel.pipeline().addLast(new MessageDecode(1024 * 1024, 1, 1, 0, 0));
                            channel.pipeline().addLast(new MessageEncode());
                            //channel.pipeline().addLast("timeout", new ReadTimeoutHandler(60));
                            channel.pipeline().addLast(new AuthRespHandler());
                            channel.pipeline().addLast(new SpiderMonitorReqHandler());
                        }
                    });

            Channel channel = serverBootstrap.bind(new InetSocketAddress(27018)).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {

        } finally {
            masterGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
