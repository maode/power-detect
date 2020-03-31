package com.yhwt.pd.server;

import com.yhwt.pd.entity.PowerDetectCmd;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * @Package com.hsmart.hjac.netty.client.plc
 * @Description: plc通信编码器-编码发送给PLC的指令
 * @author ZhengMaoDe   
 * @date 2018年7月24日 下午2:33:39 
 */
public class PowerDetectServerEncoder extends MessageToByteEncoder<PowerDetectCmd>  {

	@Override
	protected void encode(ChannelHandlerContext ctx, PowerDetectCmd cmd, ByteBuf out) throws Exception {
		byte[] bytes=cmd.toByteArray();
		out.writeBytes(bytes);
	}

}
