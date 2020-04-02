package com.yhwt.pd.server;


import com.yhwt.pd.entity.PowerDetectCmd;
import com.yhwt.pd.entity.PowerDetectResult;
import com.yhwt.pd.test.TestServerMain;
import com.yhwt.pd.util.CRC16;
import com.yhwt.pd.util.CmdUtil;
import com.yhwt.pd.util.HexUtils;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.CharBuffer;



@Sharable
@Slf4j
public class PowerDetectServerHandler extends SimpleChannelInboundHandler<PowerDetectResult> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PowerDetectResult result) throws Exception {
        log.debug("server处理器接收到{}指令反馈，数据内容为{}",result.isIfReadResult()?"读":"写",result.toString());

        CmdUtil.analysisResult(result);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	log.debug("-- test server channelReadComplete----");
    }
    
    

    
	/**
	 * 测试用-链接建立后发一些垃圾数据,测试数据过滤
	 * @param ctx
	 * @throws Exception
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

	    TestServerMain.CLIENT_CHANNEL=ctx.channel();
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(HexUtils.fromHexString(""));
        ctx.writeAndFlush(buffer);

	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
	    log.error("抛异常了~~~看着办吧");
        cause.printStackTrace();               
        ctx.close();                           
    }
	

	
}