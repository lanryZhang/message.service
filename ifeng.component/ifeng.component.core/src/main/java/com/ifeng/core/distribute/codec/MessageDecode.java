package com.ifeng.core.distribute.codec;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.SchemaGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class MessageDecode extends LengthFieldBasedFrameDecoder {
    public MessageDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        BaseMessage message = new BaseMessage();
        Schema<BaseMessage> schema = SchemaGenerator.getSchema(BaseMessage.class);
        byte[] bs = new byte[in.readableBytes()];
        in.readBytes(bs);
        ProtostuffIOUtil.mergeFrom(bs, message, schema);
        return message;
    }
}
