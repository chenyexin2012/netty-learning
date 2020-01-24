package com.holme.netty.sample.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoClientSimpleChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("channelActive, channel: {}", ctx.channel());
        ByteBuf byteBuf = Unpooled.wrappedBuffer("Hello Server".getBytes());
        ctx.channel().writeAndFlush(byteBuf);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("read message from {}", ctx.channel().remoteAddress());
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        log.info("message: {}", new String(bytes));
    }
}
