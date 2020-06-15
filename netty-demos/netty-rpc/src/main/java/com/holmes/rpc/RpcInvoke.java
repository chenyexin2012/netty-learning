package com.holmes.rpc;

import com.holmes.rpc.netty.NettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理消费端方法
 */
public class RpcInvoke implements InvocationHandler {

    /**
     * 接口
     */
    private Class target;

    private NettyClient client;


    public RpcInvoke(Class target, NettyClient client) {
        this.target = target;
        this.client = client;
    }

    public Object createInstance() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {target}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(method.getDeclaringClass() == Object.class) {
            return null;
        }
        InvokeMessage invokeMessage = new InvokeMessage();
        invokeMessage.setRequestId(DefaultRpcFuture.getRequestId());
        invokeMessage.setType(this.target.getName());
        invokeMessage.setMethodName(method.getName());
        if(args != null) {
            String[] paramTypes = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass().getName();
            }
            invokeMessage.setParamTypes(paramTypes);
            invokeMessage.setParamValues(args);
        }
        client.sendMessage(invokeMessage);
        DefaultRpcFuture<ResultMessage> future = new DefaultRpcFuture<>();
        DefaultRpcFuture.addRpcFuture(invokeMessage.getRequestId(), future);
        return future.get();
    }
}
