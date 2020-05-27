package com.holmes.rpc.serialize;

public interface ISerializable {

    byte[] encode(Object o);

    Object decode(byte[] bytes);
}
