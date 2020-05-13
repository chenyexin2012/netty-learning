package com.holmes.netty.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyHttpClient {

    private String host;

    private int port;

    /**
     * 最大重连重试次数
     */
    private int retries = 3;

    private NioEventLoopGroup group;

    private Bootstrap bootstrap;

    private Channel channel;

    public NettyHttpClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    public void init() {

        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 8));
                        pipeline.addLast(new HttpContentDecompressor());
                        pipeline.addLast(new ClientResponseHandler());
                    }
                });
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
                if(this.retries-- > 0) {
                    connect();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NettyHttpClient("127.0.0.1", 9090).connect();
    }
}
