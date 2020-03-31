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
	 * @param in
	 * @param out
	 * @throws Exception
	 * @see ByteToMessageDecoder#decode(ChannelHandlerContext, ByteBuf, List)
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
	
		
		//-----TEST START
		in.markReaderIndex();
        int len=in.readableBytes();
        byte[] read=new byte[len];
        in.readBytes(read);
        logger.trace("decode解码PLC反馈的数据,接受到的数据字节长度为: {} ,值为：{}",len,HexUtils.toHexString(read));    
        in.resetReaderIndex();
		//-----TEST END
        
        
        
		//按照 地址码和功能码 组合做头部分帧（固定长度为2个字节），可读数据小于头部固定长度,则return继续等待写入
		if (in.readableBytes() < 2) {
			return;
		}
		in.markReaderIndex();
		PowerDetectResult powerDetectResult=new PowerDetectResult();
		byte[] head=new byte[2];
		short crc16LE = 0;
		in.readBytes(head);
		byte stationCode=head[0];
		byte functionCode=head[1];
		//"读"反馈
		if(functionCode==0x03){
			byte dataLength=in.readByte();
			byte[] data=new byte[dataLength];
			in.readBytes(data);
			crc16LE=in.readShortLE();
			PowerDetectResult.ReadResult readResult=powerDetectResult.initReadResult();
			readResult.setStationCode(stationCode);
			readResult.setFunctionCode(functionCode);
			readResult.setDataLength(dataLength);
			readResult.setData(data);
		//"写"反馈
		}else if(functionCode==0x10){
			short beginAddr=in.readShort();
			short registerCount=in.readShort();
			crc16LE=in.readShortLE();
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
