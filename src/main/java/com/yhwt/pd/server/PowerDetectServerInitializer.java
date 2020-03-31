package com.yhwt.pd.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;



/**  
 * @Title: PlcServerInitializer.java
 * @Package com.hsmart.hjac.netty.server.plc
 * @Description: 处理器初始化绑定
 * @author ZhengMaoDe   
 * @date 2018年7月16日 上午10:05:20 
 */
public class PowerDetectServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("decoder", new PowerDetectServerDecoder());
		pipeline.addLast("encoder", new PowerDetectServerEncoder());
		pipeline.addLast("handler", new PowerDetectServerHandler());

	}
}