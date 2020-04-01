package com.yhwt.pd.util;

import com.yhwt.pd.entity.PowerDetectCmd;

public class CmdUtil {
    //设备连接成功后，首次读取设备信息
    public static final String FIRST_READ_HEX_CMD="00 03 00 04 00 01 C4 1A";

    /**
     * 获取“写设备地址”指令
     * @param addr
     * @return
     */
    public static  PowerDetectCmd getWriteAddr(byte addr){
        PowerDetectCmd powerDetectCmd=new PowerDetectCmd();
        PowerDetectCmd.WriteCmd writeCmd = powerDetectCmd.initWriteCmd();
        writeCmd.setStationCode((byte) 0x00);
        writeCmd.setFunctionCode((byte) 0x10);
        writeCmd.setBeginAddr((short) 0x04);
        writeCmd.setRegisterCount((short) 0x01);
        writeCmd.setDataLength((byte) 0x02);
        writeCmd.setData(new byte[]{addr,0x05});
        powerDetectCmd.computeAndSetCRC16();
        return powerDetectCmd;
    }

    /**
     * 获取 “读取电流参数” 指令
     * @param addr
     * @return
     */
    public static PowerDetectCmd getReadPower(byte addr){
        PowerDetectCmd powerDetectCmd=new PowerDetectCmd();
        PowerDetectCmd.ReadCmd readCmd = powerDetectCmd.initReadCmd();
        readCmd.setStationCode(addr);
        readCmd.setFunctionCode((byte) 0x03);
        readCmd.setBeginAddr((short) 0x48);
        readCmd.setDataLength((short) 0x0a);
        powerDetectCmd.computeAndSetCRC16();
        return powerDetectCmd;
    }

    /**
     * 获取 “清电量” 指令
     * @param addr
     * @return
     */
    public static PowerDetectCmd getWriteCleanPower(byte addr){
        PowerDetectCmd powerDetectCmd=new PowerDetectCmd();
        PowerDetectCmd.WriteCmd writeCmd = powerDetectCmd.initWriteCmd();
        writeCmd.setStationCode(addr);
        writeCmd.setFunctionCode((byte) 0x10);
        writeCmd.setBeginAddr((short) 0x0C);
        writeCmd.setRegisterCount((short) 0x02);
        writeCmd.setDataLength((byte) 0x04);
        writeCmd.setData(new byte[]{0,0,0,0});
        powerDetectCmd.computeAndSetCRC16();
        return powerDetectCmd;
    }
}
