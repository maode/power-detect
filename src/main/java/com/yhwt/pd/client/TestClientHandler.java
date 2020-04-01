package com.yhwt.pd.client;

import com.yhwt.pd.util.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Slf4j
public  class TestClientHandler extends SimpleChannelInboundHandler<byte[]> {

	private static final Logger logger=LoggerFactory.getLogger(TestClientHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bytes) throws Exception {

        log.debug("客户端收到的消息为："+ HexUtils.toHexString(bytes));
    }
}
