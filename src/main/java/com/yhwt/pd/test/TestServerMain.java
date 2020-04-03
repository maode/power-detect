package com.yhwt.pd.test;

import com.yhwt.pd.entity.PowerDetectCmd;
import com.yhwt.pd.server.PowerDetectServerStart;
import com.yhwt.pd.util.CmdUtil;
import io.netty.channel.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class TestServerMain {

    public static Channel TEST_TO_CLIENT_CHANNEL;

    public static void test(){
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        PowerDetectCmd testCmd=null;
        try {
            while (true){
                String input=bufferedReader.readLine();
                switch (input){
                    case "0" : testCmd= CmdUtil.getFirstRead();break;
                    case "1" : testCmd= CmdUtil.getReadPower((byte) 0x01);break;
                    case "2" : testCmd= CmdUtil.getWriteAddr((byte) 0x01);break;
                    case "3" : testCmd= CmdUtil.getWriteCleanPower((byte) 0x01);break;
                }

                TEST_TO_CLIENT_CHANNEL.writeAndFlush(testCmd);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        PowerDetectServerStart server = new PowerDetectServerStart();
        CompletableFuture.runAsync(()->server.start(7782));
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                server.destroy();
            }
        });
        test();

    }
}
