package com.yhwt.pd.test;

import com.yhwt.pd.client.TestClientStart;
import com.yhwt.pd.util.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
public class TestClientMain {
    /**
     * 本地非集成环境测试方法
     *
     * @throws Exception
     */
    public static void test(TestClientStart client) throws Exception {
        Channel channel = client.getChannel();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String readLine = in.readLine();
            byte[] result=null;
            switch(readLine) {
                case "1" :
                    result= HexUtils.fromHexString("01 03 14 5f c0 01 8f 03 d5 00 00 07 c0 03 e8 00 00 01 e6 6e 5e 13 86 f9 f1");
                    break;
            }



            log.debug("我是客户端，我准备发送的消息为：" + HexUtils.toHexString(result) );
            ByteBuf outWrite=channel.alloc().buffer();
            outWrite.writeBytes(result);
            channel.writeAndFlush(outWrite);
        }

    }

    public static void main(String[] args) throws Exception {
        TestClientStart client = new TestClientStart("127.0.0.1",7782,7781);
        client.start();
        test(client);
    }
}
