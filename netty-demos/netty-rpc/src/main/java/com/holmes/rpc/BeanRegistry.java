package com.holmes.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册服务提供方实现类的对象
 */
public class BeanRegistry {

    /**
     * key 接口全类名
     * value 实现对象
     */
    private final static Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>();

    public static void registe(String key, Object value) {
        BEAN_MAP.put(key, value);
    }

    public static Object getBean(String key) {
        return BEAN_MAP.get(key);
    }

    public static void remove(String key) {
        BEAN_MAP.remove(key);
    }
}
