package com.holme.netty.sample.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EchoNettyClient {

    private String host;

    private int port;

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private Channel channel;

    public EchoNettyClient() {
        this("127.0.0.1", 8080);
    }

    public EchoNettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap
                .group(this.group)
                .channel(NioSocketChannel.class)
                .handler(new EchoClientChannelInitializer());
        connect();
    }

    private void connect() {
        this.bootstrap.connect(this.host, this.port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("success to connect to server");
                    EchoNettyClient.this.channel = future.channel();
                } else {
                    log.info("connect failed, waiting for reconnect");
                    TimeUnit.SECONDS.sleep(3);
                    EchoNettyClient.this.connect();
                }
            }
        });
    }

    public boolean isActive() {
        return this.channel != null && this.channel.isActive();
    }

    public void sendMessage(String message) {
        if(this.isActive()) {
            this.channel.writeAndFlush(message);
        }
    }

    public static void main(String[] args) {
        new EchoNettyClient();
    }
}
