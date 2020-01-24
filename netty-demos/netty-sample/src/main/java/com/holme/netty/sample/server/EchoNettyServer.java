package com.holme.netty.sample.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EchoNettyServer {

    private int port;

    private EventLoopGroup boss = null;

    private EventLoopGroup worker = null;

    private ServerBootstrap bootstrap = null;

    public EchoNettyServer() {
        this(8080);
    }

    public EchoNettyServer(int port) {
        this.port = port;
        init();
    }

    private void init() {

        this.boss = new NioEventLoopGroup(1);
        this.worker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        this.bootstrap = new ServerBootstrap();
        this.bootstrap
                .group(this.boss, this.worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new EchoServerChannelInitializer());
        bind();
    }

    private void bind() {
        try {
            ChannelFuture future = this.bootstrap.bind(this.port).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.info("server start success");
                    } else {
                        log.info("server start failed, waiting for restart");
                        TimeUnit.SECONDS.sleep(1);
                        bind();
                    }
                }
            });
            future.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.info("server closed");
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new EchoNettyServer();
    }
}
