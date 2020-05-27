package com.holmes.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultRpcFuture<T> extends CompletableFuture<T> {

    private final static AtomicInteger REQUEST_COUNT = new AtomicInteger();

    private final static Map<Integer, DefaultRpcFuture> FUTURE_MAP = new ConcurrentHashMap<>();

    public static int getRequestId() {
        int id = REQUEST_COUNT.getAndIncrement();
        while (id == Integer.MAX_VALUE) {
            REQUEST_COUNT.compareAndSet(Integer.MAX_VALUE, 0);
        }
        return id;
    }

    public static void addRpcFuture(int requestId, DefaultRpcFuture future) {
        FUTURE_MAP.put(requestId, future);
    }

    public static void getRpcFuture(int requestId) {
        FUTURE_MAP.get(requestId);
    }
}
