package com.yhwt.pd.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TestClientDecode extends ByteToMessageDecoder {
    /**
     * 该解码器未做粘包分帧处理，仅供测试用
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
       byte[] bytes=new byte[byteBuf.readableBytes()];
       byteBuf.readBytes(bytes);
       list.add(bytes);
    }
}
