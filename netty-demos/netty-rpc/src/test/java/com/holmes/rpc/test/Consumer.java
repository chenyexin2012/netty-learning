package com.holmes.rpc.test;

import com.holmes.rpc.netty.NettyClient;
import com.holmes.rpc.test.service.Service;

import java.util.concurrent.TimeUnit;

public class Consumer {

    public static void main(String[] args) throws InterruptedException {

        NettyClient client = new NettyClient("127.0.0.1", 8080);
        TimeUnit.SECONDS.sleep(3);

        Service service = client.getProxy(Service.class);
        System.out.println(service);

        for (int i = 0; i < 1000; i++) {
            System.out.println(service.hello("aaa"));
        }
    }
}
