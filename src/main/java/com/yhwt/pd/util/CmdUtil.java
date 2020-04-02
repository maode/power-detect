package com.yhwt.pd.util;

import com.yhwt.pd.entity.PowerDetectCmd;
import com.yhwt.pd.entity.PowerDetectResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.security.spec.ECField;
import java.text.DecimalFormat;
import java.util.Arrays;

@Slf4j
public class CmdUtil {


    /**
     * 设备连接成功后，首次读取设备信息指令
     * @return
     */
    public static PowerDetectCmd getFirstRead(){
        PowerDetectCmd powerDetectCmd=new PowerDetectCmd();
        PowerDetectCmd.ReadCmd readCmd = powerDetectCmd.initReadCmd();
        readCmd.setStationCode((byte) 0x00);
        readCmd.setFunctionCode((byte) 0x03);
        readCmd.setBeginAddr((short) 0x04);
        readCmd.setDataLength((short) 0x01);
        powerDetectCmd.computeAndSetCRC16();
        return powerDetectCmd;
    }

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

    /**解析设备返回的指令
     * @param result
     */
    public static void analysisResult(PowerDetectResult result){
        //解析“读反馈”
        if(result.isIfReadResult()){
            PowerDetectResult.ReadResult readResult = result.getReadResult();
            byte stationCode = readResult.getStationCode();
            byte dataLength = readResult.getDataLength();
            if(dataLength==2){
                //“读取设备地址” 反馈
                log.debug("服务器收到-读取设备地址反馈-设备地址为:{}",stationCode);

            }else if(dataLength==0x14){
                //"读取电流参数" 反馈
                byte[] data = readResult.getData();
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                //电压
                byte[] voltageBytes = Arrays.copyOfRange(data, 0, 2);
                short voltageSigned = ByteUtils.byteArray2Short(voltageBytes);
                int voltageUnsigned = ByteUtils.getUnsignedShort(voltageSigned);
                double voltage=numberRoundHalfUp2(voltageUnsigned*1.0/100);
                //电流
                byte[] electricityBytes = Arrays.copyOfRange(data, 2, 4);
                short electricitySigned = ByteUtils.byteArray2Short(electricityBytes);
                int electricityUnsigned = ByteUtils.getUnsignedShort(electricitySigned);
                double electricity=numberRoundHalfUp2(electricityUnsigned*1.0/100);
                //有功功率
                byte[] activePowerBytes = Arrays.copyOfRange(data, 4, 6);
                short activePowerSigned = ByteUtils.byteArray2Short(activePowerBytes);
                int activePower = ByteUtils.getUnsignedShort(activePowerSigned);
                //有功总电能
                byte[] totalActivePowerBytes = Arrays.copyOfRange(data, 6, 10);
                int totalActivePowerSigned = ByteUtils.byteArray2Int(totalActivePowerBytes);
                long totalActivePowerUnsigned = ByteUtils.getUnsignedInt(totalActivePowerSigned);
                double totalActivePower = numberRoundHalfUp2(totalActivePowerUnsigned * 1.0 / 3200);
                //功率因数
                byte[] powerFactorBytes = Arrays.copyOfRange(data, 10, 12);
                short powerFactorSigned = ByteUtils.byteArray2Short(powerFactorBytes);
                int powerFactoryUnsigned = ByteUtils.getUnsignedShort(powerFactorSigned);
                double powerFactory = numberRoundHalfUp2(powerFactoryUnsigned * 1.0 / 1000);
                //二氧化碳排量
                byte[] co2Bytes = Arrays.copyOfRange(data, 12, 16);
                int co2Signed = ByteUtils.byteArray2Int(co2Bytes);
                long co2Unsigned = ByteUtils.getUnsignedInt(co2Signed);
                double co2 = numberRoundHalfUp2(co2Unsigned * 1.0 / 1000);
                //温度[保留，此模块无该功能]
                byte[] temperatureBytes = Arrays.copyOfRange(data, 16, 18);
                short temperatureSigned = ByteUtils.byteArray2Short(temperatureBytes);
                int temperatureUnsigned = ByteUtils.getUnsignedShort(temperatureSigned);
                //频率
                byte[] frequencyBytes = Arrays.copyOfRange(data, 18, 20);
                short frequencySigned = ByteUtils.byteArray2Short(frequencyBytes);
                int frequencyUnsigned = ByteUtils.getUnsignedShort(frequencySigned);
                double frequency = numberRoundHalfUp2(frequencyUnsigned * 1.0 / 100);
                log.debug("服务器收到-读取电流参数-设备地址为:{}，电流信息为-> " +
                        "电压：{}" +
                        "电流：{}" +
                        "有功功率：{}" +
                        "有功总电能：{}" +
                        "功率因数：{}" +
                        "二氧化碳排量：{}" +
                        "温度：{}" +
                        "频率：{}",stationCode,voltage,electricity,activePower,totalActivePower,powerFactory,co2,temperatureUnsigned,frequency);
            }

        }else{
            //解析“写反馈”
            PowerDetectResult.WriteResult writeResult = result.getWriteResult();
            byte stationCode = writeResult.getStationCode();
            short beginAddr = writeResult.getBeginAddr();
            if(beginAddr==0x04){
                //“写地址” 反馈
            }else if(beginAddr==0x0C){
                //“清电量” 反馈
            }


        }

    }

    /**浮点数四舍五入保留两位小数
     * @param number
     * @return
     */
    private static double numberRoundHalfUp2(double number){
        return new BigDecimal(number).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
