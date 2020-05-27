package com.holmes.rpc.netty;

import com.holmes.rpc.InvokeMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class InvokeMessageHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof InvokeMessage) {
            InvokeMessage invokeMessage = (InvokeMessage) msg;
        }
    }
}
