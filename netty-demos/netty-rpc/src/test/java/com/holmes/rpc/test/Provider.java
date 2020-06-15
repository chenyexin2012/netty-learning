package com.holmes.rpc.test;

import com.holmes.rpc.BeanRegistry;
import com.holmes.rpc.netty.NettyServer;
import com.holmes.rpc.test.service.Service;
import com.holmes.rpc.test.service.ServiceImpl;

public class Provider {

    public static void main(String[] args) {

        BeanRegistry.registe(Service.class.getCanonicalName(), new ServiceImpl());
        new NettyServer(8080);
    }
}
