package com.yhwt.pd.server;


import com.yhwt.pd.entity.PowerDetectCmd;
import com.yhwt.pd.entity.PowerDetectResult;
import com.yhwt.pd.util.CRC16;
import com.yhwt.pd.util.HexUtils;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;



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

    static int testi=1;

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
//	@Override
//	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		// TODO Auto-generated method stub
//		int i=1;
//		do {
//
//			
//			ByteBuf ou=ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap("wwwwwww\n"), CharsetUtil.UTF_8);
//			ByteBuf ou2=ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap("ooooo\n"), CharsetUtil.UTF_8);
//			ByteBuf ou3=ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap("3333"), CharsetUtil.UTF_8);
//			ByteBuf ou4=ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap("4444\n"), CharsetUtil.UTF_8);
//			PlcResult result=new PlcResult();
//			result.setCode((byte)3);
//			ByteBuf ou5=ctx.alloc().buffer();
//			ByteBuf ou6=ctx.alloc().buffer();
//			ou5.writeBytes(HexUtils.fromHexString("01030A2CBA00010001000100011384"));
//			ou6.writeBytes(HexUtils.fromHexString("01030A2CBA00010001000100011384"));
//			//ctx.write(ou2);
//			//ctx.write(ou4);
//			ctx.write(ou3);
//			ctx.flush();
//			ctx.write(ou5);
//			ctx.flush();
//			ctx.write(ou);
//			ctx.write(ou6);
//			ctx.flush();
//			System.out.println("写了第"+i+"遍,即将沉睡");
//				
//		}while(false);
//		
//		//ctx.write(msg)
//	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
        cause.printStackTrace();               
        ctx.close();                           
    }
	

	
}