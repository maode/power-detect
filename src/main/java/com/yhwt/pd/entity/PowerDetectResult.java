package com.yhwt.pd.entity;

import java.nio.ByteBuffer;

import com.yhwt.pd.util.CRC16;
import com.yhwt.pd.util.HexUtils;

import lombok.Getter;
import lombok.Setter;


/**
 * 功率检测设备反馈数据
 * @author ZhengMaoDe
 */
@Getter
@Setter
public class PowerDetectResult {
    /**
     * @Fields BASE_LEN : 固定部分的长度(站号加功能码)
     */
    public static final int BASE_LEN = 2;
    /**
     * @Fields CHECK_LEN : 校验和固定长度
     */
    public static final int CHECK_LEN = 2;
    /**
     * 是否为读反馈
     */
    private boolean ifReadResult;
    private ReadResult readResult;
    private WriteResult writeResult;


    @Getter
    @Setter
    public
    class ReadResult {
        /**
         * 站号
         */
        private byte stationCode;
        /**
         * @Fields functionCode : 功能码
         */
        private byte functionCode;
        /**
         * @Fields dataLength : 指令字节数（data的字节数）,仅读反馈有该属性值
         */
        private byte dataLength;
        /**
         * @Fields data : 具体数据
         */
        private byte[] data;
        /**
         * @Fields checksum : 校验和
         */
        private short checksum;

        public int getLength() {
            return BASE_LEN + 1 + dataLength + CHECK_LEN;
        }
    }

    @Getter
    @Setter
    public
    class WriteResult {
        /**
         * 站号
         */
        private byte stationCode;
        /**
         * @Fields functionCode : 功能码
         */
        private byte functionCode;
        /**
         * 起始地址
         */
        private short beginAddr;
        /**
         * 写寄存器数量
         */
        private short registerCount;
        /**
         * @Fields checksum : 校验和
         */
        private short checksum;

        public int getLength() {
            return BASE_LEN + 2 + 2 + CHECK_LEN;
        }
    }

    /**
     * 返回当前指令的字节数组表现形式-不包含校验和
     *
     * @return
     */
    public byte[] toNoCheckByteArray() {
        byte[] bytes = null;
        if (ifReadResult) {
            bytes = ByteBuffer.allocate(readResult.getLength()-CHECK_LEN)
                    .put(readResult.getStationCode())
                    .put(readResult.getFunctionCode())
                    .put(readResult.getDataLength())
                    .put(readResult.getData())
                    .array();
        } else {
            bytes = ByteBuffer.allocate(writeResult.getLength()-CHECK_LEN)
                    .put(writeResult.getStationCode())
                    .put(writeResult.getFunctionCode())
                    .putShort(writeResult.getBeginAddr())
                    .putShort(writeResult.getRegisterCount())
                    .array();
        }

        return bytes;
    }

    /**
     * 返回当前指令的字节数组表现形式-包含校验和
     *
     * @return
     */
    public byte[] toByteArray() {
        byte[] bytes = this.toNoCheckByteArray();

        if (ifReadResult) {
            return ByteBuffer.allocate(bytes.length + CHECK_LEN).put(bytes).putShort(readResult.getChecksum()).array();
        } else {
            return ByteBuffer.allocate(bytes.length + CHECK_LEN).put(bytes).putShort(writeResult.getChecksum()).array();
        }
    }

    public short computeAndSetCRC16(){
        int computeCrc= CRC16.calcCrc16LE(toNoCheckByteArray());
        if(isIfReadResult()){
            readResult.setChecksum((short)computeCrc);
        }else{
            writeResult.setChecksum((short)computeCrc);
        }
        return (short)computeCrc;
    }

    public  ReadResult initReadResult(){
        ReadResult readResult=this.new ReadResult();
        this.readResult= readResult;
        this.ifReadResult=true;
        return readResult;
    }
    public  WriteResult initWriteResult(){
        WriteResult writeResult=this.new WriteResult();
        this.writeResult= writeResult;
        this.ifReadResult=false;
        return writeResult;
    }

    /**
     * 返回当前指令十六进制形式字符串
     *
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return HexUtils.toHexString(this.toByteArray());
    }


}
