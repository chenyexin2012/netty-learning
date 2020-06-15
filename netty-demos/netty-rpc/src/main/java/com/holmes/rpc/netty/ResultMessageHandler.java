package com.holmes.rpc.netty;

import com.holmes.rpc.DefaultRpcFuture;
import com.holmes.rpc.ResultMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResultMessageHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info("接收到服务端响应信息，{}", msg);
        if(msg instanceof ResultMessage) {
            ResultMessage result = (ResultMessage) msg;
            DefaultRpcFuture future = DefaultRpcFuture.getRpcFuture(result.getRequestId());
            future.complete(result.getResult());
        }
    }
}
