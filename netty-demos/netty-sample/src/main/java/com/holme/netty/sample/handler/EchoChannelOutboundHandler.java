package com.holme.netty.sample.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * 出站消息处理器
 */
@Slf4j
public class EchoChannelOutboundHandler implements ChannelOutboundHandler {

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> bind, channel: {}", ctx.channel());
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> connect, channel: {}", ctx.channel());
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> disconnect, channel: {}", ctx.channel());
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> close, channel: {}", ctx.channel());
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> deregister, channel: {}", ctx.channel());
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> read, channel: {}", ctx.channel());
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> write, channel: {}", ctx.channel());
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> flush, channel: {}", ctx.channel());
        ctx.flush();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> handlerAdded, channel: {}", ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> handlerRemoved, channel: {}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(">>>>>>>>>>>>>>>>> exceptionCaught, channel: " + ctx.channel(), cause);
    }
}
