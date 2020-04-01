package com.yhwt.pd.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;


/**  
 * @Title: PlcClientInitializer.java
 * @Package com.hsmart.hjac.netty.client.plc
 * @Description: plc通信处理器初始化
 * @author ZhengMaoDe   
 * @date 2018年7月11日 上午9:34:15 
 */
public class TestClientInitializer extends ChannelInitializer<SocketChannel> {


		@Override
	    public void initChannel(SocketChannel ch) throws Exception {
	        ChannelPipeline pipeline = ch.pipeline();
	        
	       // pipeline.addLast(new IdleStateHandler(AgvConstants.DEFAULT_HEARTBEAT_SECOND, 0, 0));//心跳
			//pipeline.addLast("encode",new StringEncoder());
			pipeline.addLast("decode",new TestClientDecode());
	        pipeline.addLast("handler", new TestClientHandler());
	    }
	}