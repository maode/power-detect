package com.yhwt.pd.test;

import com.yhwt.pd.client.TestClientStart;
import com.yhwt.pd.util.ByteUtil;
import com.yhwt.pd.util.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

@Slf4j
public class TestClientMain {
    public static Channel TEST_TO_SERVER_CHANNEL;
    /**
     * 本地非集成环境测试方法
     *
     * @throws Exception
     */
    public static void test() throws Exception {

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
            ByteBuf outWrite=TEST_TO_SERVER_CHANNEL.alloc().buffer();
            outWrite.writeBytes(result);
            TEST_TO_SERVER_CHANNEL.writeAndFlush(outWrite);
        }

    }

    /**模拟回应服务器下发的指令
     * @param serverCmd
     */
    public static void mockClientResult(byte[] serverCmd){
        String resultHex=null;
        byte stationCode = serverCmd[0];
        byte functionCode = serverCmd[1];
        byte[] beginAddrBytes = Arrays.copyOfRange(serverCmd, 2, 4);
        short beginAddr = ByteUtil.byteArray2Short(beginAddrBytes);
        //模拟读反馈
        if(functionCode==0x03){
            if(beginAddr==0x04){
                //读设备信息-反馈
                resultHex="01 03 02 01 05 79 D7";
                log.debug("客户端收到-读设备信息指令-将模拟反馈：{}",resultHex);
            }else if(beginAddr==0x48){
                //读电流参数-反馈
                resultHex="01 03 14 00 00 00 00 00 00 00 00 00 00 03 E8 00 00 00 00 00 00 13 87 B0 3E";
                log.debug("客户端收到-读电流参数指令-将模拟反馈：{}",resultHex);
            }
            //模拟写反馈
        }else if(functionCode==0x10){
            if(beginAddr==0x04){
                //写设备地址-反馈
                resultHex="01 10 00 04 00 01 40 08";
                log.debug("客户端收到-写设备地址指令-将模拟反馈：{}",resultHex);
            }else if(beginAddr==0x0C){
                //清电量-反馈
                resultHex="01 10 00 0C 00 02 81 CB";
                log.debug("客户端收到-清电量指令-将模拟反馈：{}",resultHex);
            }

        }
        ByteBuf outWrite=TEST_TO_SERVER_CHANNEL.alloc().buffer();
        outWrite.writeBytes(HexUtils.fromHexString(resultHex));
        TEST_TO_SERVER_CHANNEL.writeAndFlush(outWrite);

    }

    public static void main(String[] args) throws Exception {
//        TestClientStart client = new TestClientStart("139.198.16.241",7782,7781);
        TestClientStart client = new TestClientStart("127.0.0.1",7782,7781);
        client.start();
        test();
    }
}
