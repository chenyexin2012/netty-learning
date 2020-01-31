package com.holmes.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理webSocket文本信息
 */
@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        log.info("receive message from {}, message is {}", ctx.channel().remoteAddress(), msg.text());
        TextWebSocketFrame request = new TextWebSocketFrame("receive message " + msg.text());
        ctx.writeAndFlush(request);
    }
}
