package com.holmes.rpc;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ResultMessage implements Serializable {

    /**
     * 请求序号
     */
    private int requestId;

    /**
     * 返回值
     */
    private Object result;

    /**
     * 发生的异常信息
     */
    private Throwable throwable;
}
