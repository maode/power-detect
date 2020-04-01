package com.yhwt.pd.server;


import com.yhwt.pd.entity.PowerDetectCmd;
import com.yhwt.pd.entity.PowerDetectResult;
import com.yhwt.pd.util.CRC16;
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


/**
 * @Title: PlcServerHandler.java
 * @Package com.his.net.agv.nettybak.netty.server.plc
 * @Description: 自定义业务处理器
 * @author ZhengMaoDe   
 * @date 2018年7月16日 上午10:05:35 
 */
@Sharable
@Slf4j
public class PowerDetectServerHandler extends SimpleChannelInboundHandler<PowerDetectResult> {

    //-----TEST START----
    static int testi=1;
    public static Channel CLIENT_CHANNEL;
    //-----TEST END----

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PowerDetectResult result) throws Exception {
        log.debug("server处理器接收到{}指令反馈，数据内容为{}",result.isIfReadResult()?"读":"写",result.toString());
        PowerDetectCmd powerDetectCmd=new PowerDetectCmd();
        if(testi%2==0){
            powerDetectCmd.initReadCmd();
            PowerDetectCmd.ReadCmd readCmd = powerDetectCmd.getReadCmd();
            readCmd.setStationCode((byte) 0x01);
            readCmd.setFunctionCode((byte) 0x03);
            readCmd.setBeginAddr((short) 0x48);
            readCmd.setDataLength((short) 0x02);
            powerDetectCmd.computeAndSetCRC16();
        }else{
            powerDetectCmd.initWriteCmd();
            PowerDetectCmd.WriteCmd writeCmd = powerDetectCmd.initWriteCmd();
            writeCmd.setStationCode((byte) 0x01);
            writeCmd.setFunctionCode((byte) 0x10);
            writeCmd.setBeginAddr((short) 0x0C);
            writeCmd.setRegisterCount((short) 0x02);
            writeCmd.setDataLength((byte) 0x04);
            writeCmd.setData(new byte[]{0,0,0,0});
            powerDetectCmd.computeAndSetCRC16();
        }
        log.debug("server处理器准备发送数据：{}",powerDetectCmd);
        channelHandlerContext.channel().writeAndFlush(powerDetectCmd);
        testi++;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	log.debug("-- test server channelReadComplete----");
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)//4
//        .addListener(ChannelFutureListener.CLOSE);
    }
    
    

    
	/**
	 * 测试用-链接建立后发一些垃圾数据,测试数据过滤
	 * @param ctx
	 * @throws Exception
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

	    CLIENT_CHANNEL=ctx.channel();
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