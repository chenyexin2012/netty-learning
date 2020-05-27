package com.holmes.rpc.netty.codec;

import com.holmes.rpc.serialize.ISerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private ISerializable iSerializable;

    public MessageDecoder(ISerializable iSerializable) {
        this.iSerializable = iSerializable;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4) return;
        int length = in.readInt();
        if(length == 0) return;
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = iSerializable.decode(bytes);
        out.add(obj);
    }
}
