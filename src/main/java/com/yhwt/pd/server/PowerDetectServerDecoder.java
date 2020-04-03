package com.yhwt.pd.server;

import com.yhwt.pd.entity.PowerDetectResult;
import com.yhwt.pd.util.CRC16;
import com.yhwt.pd.util.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor.IndexOfProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @Package com.his.net.agv.nettybak.netty.client.plc
 * @Description: plc通信解码器-解码Plc反馈的信息
 * @author ZhengMaoDe   
 * @date 2018年7月24日 下午2:33:05 
 */
public class PowerDetectServerDecoder extends ByteToMessageDecoder {

	private static final Logger logger=LoggerFactory.getLogger(PowerDetectServerDecoder.class);
	
	/**
	 * 解码PLC返回的数据.
	 * <p>该方法执行结束,如果没有新的可读数据,即ridx==widx,则会释放该字节缓冲区资源
	 * @param ctx
	 * @param byteBuf
	 * @param out
	 * @throws Exception
	 * @see ByteToMessageDecoder#decode(ChannelHandlerContext, ByteBuf, List)
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
	
		
		//-----解码前先打印当前信道读到的所有数据 START
		byteBuf.markReaderIndex();
        int len=byteBuf.readableBytes();
        byte[] read=new byte[len];
        byteBuf.readBytes(read);
        logger.trace("上位机解码器接收到数据,数据字节长度为: {} ,值为：{}",len,HexUtils.toHexString(read));
        byteBuf.resetReaderIndex();
		//-----解码前先打印当前信道读到的所有数据 END
        
        
        
		//按照 地址码和功能码 组合做头部分帧（固定长度为2个字节），可读数据小于头部固定长度,则return继续等待写入
		if (byteBuf.readableBytes() < 2) {
			return;
		}
		byteBuf.markReaderIndex();
		PowerDetectResult powerDetectResult=new PowerDetectResult();
		byte[] head=new byte[2];
		short crc16LE = 0;
		byteBuf.readBytes(head);
		byte stationCode=head[0];
		byte functionCode=head[1];
		//"读"反馈
		if(functionCode==0x03){
			byte dataLength=byteBuf.readByte();
			byte[] data=new byte[dataLength];
			byteBuf.readBytes(data);
			crc16LE=byteBuf.readShort();
			PowerDetectResult.ReadResult readResult=powerDetectResult.initReadResult();
			readResult.setStationCode(stationCode);
			readResult.setFunctionCode(functionCode);
			readResult.setDataLength(dataLength);
			readResult.setData(data);
		//"写"反馈
		}else if(functionCode==0x10){
			short beginAddr=byteBuf.readShort();
			short registerCount=byteBuf.readShort();
			crc16LE=byteBuf.readShort();
			PowerDetectResult.WriteResult writeResult=powerDetectResult.initWriteResult();
			writeResult.setStationCode(stationCode);
			writeResult.setFunctionCode(functionCode);
			writeResult.setBeginAddr(beginAddr);
			writeResult.setRegisterCount(registerCount);
		}

		short computeCrc16=powerDetectResult.computeAndSetCRC16();
		if(crc16LE==computeCrc16){
			out.add(powerDetectResult);
		}

	}





}
