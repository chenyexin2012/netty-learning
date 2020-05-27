package com.holmes.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcInvoke implements InvocationHandler {

    /**
     * 接口
     */
    private Object target;


    public RpcInvoke(Object target) {
        this.target = target;
    }

    public Object createInstance() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {target.getClass()}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        InvokeMessage invokeMessage = new InvokeMessage();
        invokeMessage.setRequestId(DefaultRpcFuture.getRequestId());
        invokeMessage.setType(proxy.getClass().getName());
        invokeMessage.setMethodName(method.getName());
        String[] paramTypes = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass().getName();
        }
        DefaultRpcFuture<ResultMessage> future = new DefaultRpcFuture<>();
        DefaultRpcFuture.addRpcFuture(invokeMessage.getRequestId(), future);
        return future.get();
    }
}
