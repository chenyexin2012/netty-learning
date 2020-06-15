package com.holmes.rpc.netty.codec;

import com.holmes.rpc.serialize.ISerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Object> {

    private ISerializable iSerializable;

    public MessageEncoder(ISerializable iSerializable) {
        this.iSerializable = iSerializable;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = this.iSerializable.encode(msg);
        if(bytes != null) {
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        } else {
            // error
        }
    }
}
