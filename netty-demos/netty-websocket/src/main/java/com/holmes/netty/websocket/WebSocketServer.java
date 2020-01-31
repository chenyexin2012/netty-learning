package com.holmes.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketServer {

    private int port;

    private ServerBootstrap bootstrap;

    private NioEventLoopGroup boss;

    private NioEventLoopGroup worker;

    public WebSocketServer() {
        this(8080);
    }

    public WebSocketServer(int port) {
        this.port = port;
        init();
        start();
    }

    private void init() {

        this.boss = new NioEventLoopGroup(1);
        this.worker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        this.bootstrap = new ServerBootstrap();
        this.bootstrap
                .group(this.boss, this.worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加Http协议编解码器
                        pipeline.addLast("http", new HttpServerCodec());
                        pipeline.addLast("httpAggregator", new HttpObjectAggregator(1024 * 1024 * 8));
                        // 自定义http请求处理
                        pipeline.addLast("httpRequestHandler", new HttpRequestHandler());
                        // webSocket协议处理器
                        pipeline.addLast("webSocket", new WebSocketServerProtocolHandler("/ws"));
                        // 自定义文本信息处理
                        pipeline.addLast("textWebSocketFrameHandler", new TextWebSocketFrameHandler());
                    }
                });
    }

    private void start() {
        ChannelFuture future = this.bootstrap.bind(this.port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    log.info("websocket server start success");
                } else {
                    log.info("failed to start websocket server");
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
        this.boss.shutdownGracefully();
        this.worker.shutdownGracefully();
    }
}
