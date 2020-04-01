package com.yhwt.pd.client;

import com.yhwt.pd.util.HexUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;



public class TestClientStart {
	private Logger logger=LoggerFactory.getLogger(getClass());

    private NioEventLoopGroup workGroup = new NioEventLoopGroup();
    private Channel channel;
    private Bootstrap bootstrap;
    private final String host;
    private final int port;
    private final int localPort;


    public TestClientStart(String host, int port, int localPort){
        this.host = host;
        this.port = port;
        this.localPort=localPort;
    }



    public void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new TestClientInitializer())
                    .localAddress(localPort)
                    ;
            channel=bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



	/**
	 * 本地非集成环境测试方法
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			String readLine = in.readLine();
            byte[] result=null;
			switch(readLine) {
			case "1" :
                result=HexUtils.fromHexString("01 03 14 5f c0 01 8f 03 d5 00 00 07 c0 03 e8 00 00 01 e6 6e 5e 13 86 f9 f1");
			    break;
			}


			
			logger.debug("我是客户端，我准备发送的消息为：" + HexUtils.toHexString(result) );
			ByteBuf outWrite=channel.alloc().buffer();
            outWrite.writeBytes(result);
			channel.writeAndFlush(outWrite);
		}

	}
    
    public static void main(String[] args) throws Exception {
    	TestClientStart client = new TestClientStart("127.0.0.1",7782,7781);
        client.start();
        client.test();
    }

    
	
}









