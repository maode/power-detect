package com.yhwt.pd.entity;

import com.yhwt.pd.util.CRC16;
import com.yhwt.pd.util.HexUtils;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

/**
 *
 */
@Getter
@Setter
public class PowerDetectCmd {
    /**
     * 读功能码
     */
    public static final byte READ_FUNCTION_CODE = 0x03;
    /**
     * 写功能码
     */
    public static final byte WRITE_FUNCTION_CODE = 0x10;

    private boolean ifReadCmd;

    private ReadCmd readCmd;
    private WriteCmd writeCmd;


    @Getter
    @Setter
    public
    class ReadCmd {
        /**
         * 站号（地址）
         */
        private byte stationCode;
        /**
         * @Fields functionCode : 功能码
         */
        private byte functionCode;
        /**
         * @Fields beginAddr : 寄存器起始地址
         */
        private short beginAddr;
        /**
         * @Fields dataLength : 数据长度
         */
        private short dataLength;
        /**
         * @Fields checksum : 校验和
         */
        private short checksum;

        public int getLength() {
            //站号+功能码+起始地址+数据长度+校验和
            return 8;
        }
    }

    @Getter
    @Setter
    public
    class WriteCmd {
        /**
         * 站号（地址）
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
         * @Fields dataLength : 数据字节长度（字节计数）
         */
        private byte dataLength;
        /**
         * 数据
         */
        private byte[] data;
        /**
         * @Fields checksum : 校验和
         */
        private short checksum;

        public int getLength() {
            //站号（地址）+功能码+起始地址+写寄存器数量+字节计数+数据+校验和
            return 1+1+2+2+1+dataLength+2;
        }
    }

    /**
     * 返回当前指令的字节数组表现形式-不包含校验和
     *
     * @return
     */
    public byte[] toNoCheckByteArray() {
        byte[] bytes = null;
        if (ifReadCmd) {
            bytes = ByteBuffer.allocate(readCmd.getLength())
                    .put(readCmd.getStationCode())
                    .put(readCmd.getFunctionCode())
                    .putShort(readCmd.getBeginAddr())
                    .putShort(readCmd.getDataLength())
                    .array();
        } else {
            bytes = ByteBuffer.allocate(writeCmd.getLength())
                    .put(writeCmd.getStationCode())
                    .put(writeCmd.getFunctionCode())
                    .putShort(writeCmd.getBeginAddr())
                    .putShort(writeCmd.getRegisterCount())
                    .put(writeCmd.getDataLength())
                    .put(writeCmd.getData())
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
        if (ifReadCmd) {
            return ByteBuffer.allocate(bytes.length + 2).put(bytes).putShort(readCmd.getChecksum()).array();
        } else {
            return ByteBuffer.allocate(bytes.length + 2).put(bytes).putShort(writeCmd.getChecksum()).array();
        }
    }

    public short computeAndSetCRC16() {
        int computeCrc = CRC16.calcCrc16(toNoCheckByteArray());
        if (ifReadCmd) {
            readCmd.setChecksum((short) computeCrc);
        } else {
            writeCmd.setChecksum((short) computeCrc);
        }
        return (short) computeCrc;
    }

    public PowerDetectCmd.ReadCmd initReadCmd() {
        PowerDetectCmd.ReadCmd readCmd = this.new ReadCmd();
        this.readCmd = readCmd;
        this.ifReadCmd=true;
        return readCmd;
    }
    public PowerDetectCmd.WriteCmd initWriteCmd() {
        PowerDetectCmd.WriteCmd writeCmd = this.new WriteCmd();
        this.writeCmd = writeCmd;
        this.ifReadCmd=false;
        return writeCmd;
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
