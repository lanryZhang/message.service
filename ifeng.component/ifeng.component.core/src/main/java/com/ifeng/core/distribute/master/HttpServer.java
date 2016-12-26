/*
* HttpServer.java 
* Created on  202016/12/14 15:08 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.master;

import com.ifeng.configurable.Configurable;
import com.ifeng.configurable.Context;
import com.ifeng.core.clean.CleanupAware;
import com.ifeng.core.distribute.handlers.http.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class HttpServer implements Configurable,CleanupAware {
    private String hostIp = "127.0.0.1";
    private int port = 80;
    private HttpRequestHandler handler;
    private String path;
    private int threadNum;
    private  EventLoopGroup workGroup;
    private EventLoopGroup masterGroup;

    private List<Runnable> exPlugin = new ArrayList<>();
    private ExecutorService executorService ;

    private static final Logger logger = Logger.getLogger(HttpServer.class);

    public HttpServer(HttpRequestHandler handler){
        this.handler = handler;
    }
    public void start() {
        workGroup = new NioEventLoopGroup();
        masterGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(masterGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    //.option(ChannelOption.SO_LINGER, -1)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 1024 * 64)
                    .option(ChannelOption.SO_SNDBUF, 1024 * 64)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline().addLast("encoder",     new HttpResponseEncoder());
                            channel.pipeline().addLast("decoder",     new HttpRequestDecoder());
                            channel.pipeline().addLast("aggegator",   new HttpObjectAggregator(65536));
                            channel.pipeline().addLast("chunkHandler",new ChunkedWriteHandler());
                            channel.pipeline().addLast("mainHandler", handler);
                        }
                    });

            //Run extend plugins
            exPlugin.forEach(r->{
                executorService.submit(r);
            });

            Channel channel = serverBootstrap.bind(new InetSocketAddress(hostIp,port)).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {

        } finally {
            masterGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            if (!executorService.isShutdown()){
                executorService.shutdown();
            }
        }
    }

    public void registPlugin(Runnable runnable){
        exPlugin.add(runnable);
    }

    @Override
    public void config(Context context) {
        this.path = context.getString("path");
        this.threadNum = context.getInt("threadNum",10);
        this.hostIp = context.getString("hostIp","0.0.0.0");
        this.port = context.getInt("port",80);

        executorService = Executors.newFixedThreadPool(threadNum);
        HttpRequestDispatcher dispatcher = new HttpRequestDispatcher();
        dispatcher.setMapper((HandlerMapper) context.getObject("handlerMapper"));
        handler.setDispatcher(dispatcher);
    }

    @Override
    public void cleanup() {
        logger.info("close http server.");
        if (null != masterGroup) {
            logger.info("close http server -- masterGroup.");
            masterGroup.shutdownGracefully();
        }
        if (null != workGroup){
            logger.info("close http server -- workGroup.");
            workGroup.shutdownGracefully();
        }
        if (!executorService.isShutdown()){
            logger.info("http server executorService shutdown.");
            executorService.shutdown();
        }
    }
}
