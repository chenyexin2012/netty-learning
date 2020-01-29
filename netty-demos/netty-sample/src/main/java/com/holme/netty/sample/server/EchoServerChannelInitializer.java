package com.holme.netty.sample.server;

import com.holme.netty.sample.handler.EchoChannelInboundHandler;
import com.holme.netty.sample.handler.EchoChannelOutboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class EchoServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new EchoChannelInboundHandler());
        pipeline.addLast(new EchoServerSimpleChannelInboundHandler());
        pipeline.addLast(new EchoChannelOutboundHandler());
    }
}
