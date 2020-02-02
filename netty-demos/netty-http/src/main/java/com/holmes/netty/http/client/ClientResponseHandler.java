package com.holmes.netty.http.client;

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
public class ClientResponseHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        String message = "Hello World";
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "netty/test");
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        byte[] bytes = message.getBytes();
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        request.content().writeBytes(bytes);
        ctx.channel().writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {

        log.info("channel read");
        if(object instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) object;

            HttpHeaders headers = response.headers();
            Iterator<Map.Entry<String, String>> entryIterator = headers.iteratorAsString();
            while (entryIterator.hasNext()) {
                Map.Entry<String, String> entry = entryIterator.next();
                log.info(entry.getKey() + ": " + entry.getValue());
            }
            ByteBuf buf = response.content();
            log.info("massage content: {}", buf.toString(CharsetUtil.UTF_8));
        }
    }
}
