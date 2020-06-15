package com.holmes.rpc.netty;

import com.holmes.rpc.RpcInvoke;
import com.holmes.rpc.netty.codec.MessageDecoder;
import com.holmes.rpc.netty.codec.MessageEncoder;
import com.holmes.rpc.serialize.ISerializable;
import com.holmes.rpc.serialize.JdkSerializable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {

    private ISerializable serializable = new JdkSerializable();

    private String host;

    private int port;

    private Bootstrap bootstrap;

    private NioEventLoopGroup group;

    private Channel channel;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {

        this.group = new NioEventLoopGroup(1);
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageDecoder(serializable));
                        pipeline.addLast(new MessageEncoder(serializable));
                        pipeline.addLast(new ResultMessageHandler());
                    }
                });
        doConnect();
    }

    private void doConnect() {
        bootstrap.connect(new InetSocketAddress(this.host, this.port)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    NettyClient.this.channel = future.channel();
                    log.info("连接到服务端成功");
                } else {
                    log.info("连接服务端失败");
                    TimeUnit.SECONDS.sleep(3);
                    NettyClient.this.doConnect();
                }
            }
        });
    }

    public void sendMessage(Object message) {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.writeAndFlush(message);
        } else {
            log.info("未发送");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessage(message);
        }
    }

    public <T> T getProxy(Class clazz) {
        return (T) new RpcInvoke(clazz, this).createInstance();
    }
}
