package com.yhwt.pd.client;

import com.yhwt.pd.test.TestClientMain;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Getter
public class TestClientStart {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private NioEventLoopGroup workGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap;
    private final String host;
    private final int port;
    private final int localPort;


    public TestClientStart(String host, int port, int localPort) {
        this.host = host;
        this.port = port;
        this.localPort = localPort;
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
            TestClientMain.TEST_TO_SERVER_CHANNEL = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}









