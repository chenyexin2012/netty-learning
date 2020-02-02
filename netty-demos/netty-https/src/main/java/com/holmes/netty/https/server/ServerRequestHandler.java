package com.holmes.netty.https.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;

@Slf4j
public class ServerRequestHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {

        if (object instanceof FullHttpRequest) {

            FullHttpRequest request = (FullHttpRequest) object;

            log.info("receive message from " + channelHandlerContext.channel().remoteAddress());
            log.info("request url is " + request.uri());
            log.info("header info");
            HttpHeaders headers = request.headers();
            Iterator<Map.Entry<String, String>> entryIterator = headers.iteratorAsString();
            while (entryIterator.hasNext()) {
                Map.Entry<String, String> entry = entryIterator.next();
                log.info("\t" + entry.getKey() + ": " + entry.getValue());
            }
            ByteBuf buf = request.content();
            log.info("massage content: {}", buf.toString(CharsetUtil.UTF_8));

            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello World!".getBytes());

            FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            response.content().writeBytes(byteBuf);

            channelHandlerContext.channel().writeAndFlush(response);
        }
    }
}
