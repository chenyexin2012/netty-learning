package com.holmes.rpc.netty;

import com.holmes.rpc.BeanRegistry;
import com.holmes.rpc.InvokeMessage;
import com.holmes.rpc.ResultMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class InvokeMessageHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info("接收到来自客户端的请求信息，{}", msg);
        if(msg instanceof InvokeMessage) {
            InvokeMessage invokeMessage = (InvokeMessage) msg;
            int requestId = invokeMessage.getRequestId();
            String type = invokeMessage.getType();
            String methodName = invokeMessage.getMethodName();
            Object[] paramValues = invokeMessage.getParamValues();
            String[] paramTypes = invokeMessage.getParamTypes();

            Object obj = BeanRegistry.getBean(type);
            // 通过反射调用方法
            Class clazz = Class.forName(type);
            Class[] types = new Class[paramTypes.length];
            for(int i = 0; i < types.length; i++) {
                types[i] = Class.forName(paramTypes[i]);
            }
            Method method = clazz.getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            Object result = method.invoke(obj, paramValues);

            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setRequestId(requestId);
            resultMessage.setResult(result);
            resultMessage.setThrowable(null);
            ctx.channel().writeAndFlush(resultMessage);
        }
    }
}
