package com.holme.netty.sample.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 入站消息处理器
 */
@Slf4j
public class EchoChannelInboundHandler implements ChannelInboundHandler {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> channelRegistered, channel: {}", ctx.channel());
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> channelUnregistered, channel: {}", ctx.channel());
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> channelActive, channel: {}", ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> channelInactive, channel: {}", ctx.channel());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> channelRead, channel: {}", ctx.channel());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> channelReadComplete, channel: {}", ctx.channel());
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> userEventTriggered, channel: {}", ctx.channel());
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>>>>>>>>>>>>> channelWritabilityChanged, channel: {}", ctx.channel());
        ctx.fireChannelWritabilityChanged();
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
