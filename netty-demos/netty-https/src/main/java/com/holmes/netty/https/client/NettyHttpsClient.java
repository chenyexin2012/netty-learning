package com.holmes.netty.https.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class NettyHttpsClient {

    private String host;

    private int port;

    /**
     * 最大重连重试次数
     */
    private int retries = 3;

    private NioEventLoopGroup group;

    private Bootstrap bootstrap;

    private Channel channel;

    public NettyHttpsClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    public void init() {

        SslContext sslContext = null;
        try {
            sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientHttpsChannelInitializer(sslContext));
    }

    public boolean isActive() {
        return this.channel != null && this.channel.isActive();
    }

    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(this.host, this.port)).await();
            if (future.isSuccess()) {
                this.channel = future.channel();
                log.info("success to connect");
                this.retries = 3;
            } else {
                log.info("failed to connect");
                if (this.retries-- > 0) {
                    connect();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if(this.isActive()) {
            log.info("send message: {}", message);
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "netty/test");
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            byte[] bytes = message.getBytes();
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
            request.content().writeBytes(bytes);
            this.channel.writeAndFlush(request);
        }
    }

    public static void main(String[] args) {

        NettyHttpsClient client = new NettyHttpsClient("127.0.0.1", 9090);
        client.connect();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.next();
            client.sendMessage(message);
        }
    }
}
