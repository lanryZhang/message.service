package com.ifeng.core.distribute.codec;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class MessageEncode extends MessageToMessageEncoder<BaseMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseMessage baseMessage, List<Object> list) throws Exception {
        if (baseMessage == null){
            return;
        }
        ByteBuf byteBuf = MessageSerializer.serialize(baseMessage);
        list.add(byteBuf);
    }
}
