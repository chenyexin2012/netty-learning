package com.holme.netty.sample.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoServerSimpleChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("read message from {}", ctx.channel().remoteAddress());
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        log.info("message: {}", new String(bytes));

        ByteBuf byteBuf = Unpooled.wrappedBuffer("Hello Client".getBytes());
        ctx.channel().writeAndFlush(byteBuf);
    }
}
