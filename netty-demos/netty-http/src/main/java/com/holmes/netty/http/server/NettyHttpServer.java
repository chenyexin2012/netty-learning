package com.holmes.netty.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpServer {

    private int port;

    private NioEventLoopGroup boss;

    private NioEventLoopGroup worker;

    private ServerBootstrap serverBootstrap;

    public NettyHttpServer(int port) {
        this.port = port;
        init();
    }

    public void init() {
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 8));
                            pipeline.addLast(new ServerRequestHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(this.port).sync();
            if (future.isSuccess()) {
                log.info("server start success!");
            }
            future.channel().closeFuture().sync();
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
        new NettyHttpServer(9090);
    }
}
