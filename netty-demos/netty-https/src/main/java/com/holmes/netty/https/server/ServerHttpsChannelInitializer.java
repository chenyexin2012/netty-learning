package com.holmes.netty.https.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;

@AllArgsConstructor
@Slf4j
public class ServerHttpsChannelInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        SSLEngine sslEngine = sslContext.newEngine(channel.alloc());
        ChannelPipeline pipeline = channel.pipeline();

        SslHandler sslHandler = new SslHandler(sslEngine);
        // handshakeFuture()方法会返回一个握手完成后的ChannelFuture对象，
        // 若之前已经完成握手，则会返回一个包含之前握手结果的ChannelFuture对象
        sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
            @Override
            public void operationComplete(Future<? super Channel> future) throws Exception {
                if(future.isSuccess()) {
                    log.info("handshake success");
                } else {
                    log.info("handshake failed");
                }
            }
        });

        pipeline.addLast(sslHandler);
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 8));
        pipeline.addLast(new ServerRequestHandler());
    }
}
