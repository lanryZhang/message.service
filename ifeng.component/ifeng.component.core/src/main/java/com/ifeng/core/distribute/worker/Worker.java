package com.ifeng.core.distribute.worker;

import com.ifeng.core.distribute.codec.MessageDecode;
import com.ifeng.core.distribute.codec.MessageEncode;
import com.ifeng.core.distribute.handlers.AuthReqHandler;
import com.ifeng.core.distribute.handlers.SpiderMonitorRespHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class Worker {
    private String hostIp = "36.110.204.91";
    private int port = 27018;
    private static int counter = 0;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    public void connect(){
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel arg0) throws Exception {
                            arg0.pipeline().addLast(new MessageDecode(1024 * 1024,1,1,0,0));
                            arg0.pipeline().addLast(new MessageEncode());
                            //arg0.pipeline().addLast("timeout",new ReadTimeoutHandler(60));
                            arg0.pipeline().addLast(new AuthReqHandler());
                            arg0.pipeline().addLast(new SpiderMonitorRespHandler());
                        }
                    });

            ChannelFuture f = bootstrap.connect(hostIp, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            System.out.println("reconnect:"+ ++counter);
            executorService.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    try {
                        connect();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            });
        }
    }
}
