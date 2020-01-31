package com.holmes.netty.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;

/**
 * 处理用户的Http请求
 */
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String WS_URI = "/ws";

    private static final String FAVICON = "/favicon.ico";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        log.info("receive message from {}, uri is {}", ctx.channel().remoteAddress(), msg.uri());
        if (WS_URI.equalsIgnoreCase(msg.getUri())) {
            // uri为 /ws, 直接将消息传递给下一个处理器
            // 此处必须手动增加引用次数（调用retain方法）
            ctx.fireChannelRead(msg.retain());
        } else if (FAVICON.equalsIgnoreCase(msg.getUri())) {
            // ctx.writeAndFlush(new DefaultHttpResponse(msg.getProtocolVersion(), HttpResponseStatus.OK));
            // do nothing
        } else {
            // 加载html文件
            String filePath = this.getClass().getClassLoader().getResource("templates/websocket.html").getPath();
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            HttpResponse response = new DefaultHttpResponse(msg.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, file.length());
            // 请求是否添加了keep-alive
            boolean keepAlive = HttpHeaders.isKeepAlive(msg);
            if (keepAlive) {
                response.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            // 写入响应信息
            ctx.write(response);
            ChannelFuture future = ctx.writeAndFlush(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            if (!keepAlive) {
                // 没有keep-alive则操作完成后关闭Channel
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
