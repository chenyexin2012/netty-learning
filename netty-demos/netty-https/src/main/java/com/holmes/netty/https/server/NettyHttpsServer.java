package com.holmes.netty.https.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Slf4j
public class NettyHttpsServer {

    private int port;

    private NioEventLoopGroup boss;

    private NioEventLoopGroup worker;

    private ServerBootstrap serverBootstrap;

    public NettyHttpsServer(int port) {
        this.port = port;
        init();
    }

    public void init() {

        SslContext sslContext = null;
        try {
            // 加载证书
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("keystore.jks");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, "123456".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "123456".toCharArray());
            sslContext = SslContextBuilder.forServer(keyManagerFactory).build();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerHttpsChannelInitializer(sslContext));

            ChannelFuture future = serverBootstrap.bind(this.port).sync();
            if (future.isSuccess()) {
                log.info("server start success!");
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new NettyHttpsServer(9090);
    }
}
