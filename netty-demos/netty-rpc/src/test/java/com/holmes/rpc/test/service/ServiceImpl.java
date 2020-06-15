package com.holmes.rpc.test.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceImpl implements Service {
    @Override
    public String hello(String name) {
        log.info("hello {}", name);
        return "hello " + name;
    }
}
