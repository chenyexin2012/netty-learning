package com.holmes.rpc.netty;

import com.holmes.rpc.netty.codec.MessageDecoder;
import com.holmes.rpc.netty.codec.MessageEncoder;
import com.holmes.rpc.serialize.ISerializable;
import com.holmes.rpc.serialize.JdkSerializable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer {

    private final static int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private final static int DEFAULT_RETRY_TIMES = 3;

    private ISerializable serializable = new JdkSerializable();

    private int retryTimes = 0;

    private int port;

    private ServerBootstrap serverBootstrap;

    private NioEventLoopGroup boss;

    private NioEventLoopGroup worker;

    public NettyServer(int port) {
        this.port = port;
        init();
    }

    private void init() {

        this.boss = new NioEventLoopGroup(1);
        this.worker = new NioEventLoopGroup(DEFAULT_THREADS);
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageDecoder(serializable));
                        pipeline.addLast(new MessageEncoder(serializable));
                        pipeline.addLast(new InvokeMessageHandler());
                    }
                });
        doBind();
    }

    private void doBind() {
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("成功开启服务端");
                } else {
                    if (NettyServer.this.retryTimes++ < DEFAULT_RETRY_TIMES) {
                        log.info("开启服务端失败，3秒后重试");
                        TimeUnit.SECONDS.sleep(3);
                    } else {
                        log.info("开启服务端失败");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync().addListener((ChannelFutureListener) future -> {
                log.info("服务端已关闭");
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.boss != null) {
                    this.boss.shutdownGracefully().sync();
                }
                if (this.worker != null) {
                    this.worker.shutdownGracefully().sync();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new NettyServer(8080);
    }
}
