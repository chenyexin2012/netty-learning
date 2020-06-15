package com.holmes.rpc;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class InvokeMessage implements Serializable {

    /**
     * 请求序号
     */
    private int requestId;

    /**
     * 调用类全路径
     */
    private String type;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数列表
     */
    private Object[] paramValues;

    /**
     * 参数类型列表，类路径
     */
    private String[] paramTypes;
}
