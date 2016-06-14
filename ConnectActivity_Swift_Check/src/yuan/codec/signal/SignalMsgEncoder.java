package yuan.codec.signal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Map;

import yuan.client.util.JsonEncoder;
import yuan.message.SignalMsg;

/**
 * ������Ϣ������
 * 
 * @author Jack Yuan
 *
 */
public class SignalMsgEncoder extends MessageToMessageEncoder<SignalMsg> {
	
	private JsonEncoder encoder;
	public SignalMsgEncoder() {
		encoder=new JsonEncoder();
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, SignalMsg msg,
			List<Object> out) throws Exception {
		if (msg == null || msg.getHeader() == null) {
			// the message is null,then throw exception
			throw new Exception("The SignalMsg is null");
		}
		//buff 
		ByteBuf sendBuf=Unpooled.buffer();
		sendBuf.writeInt(msg.getHeader().getCrcCode());
		sendBuf.writeInt(msg.getHeader().getLength());
		sendBuf.writeLong(msg.getHeader().getSessionId());
		sendBuf.writeByte(msg.getHeader().getMsgType());
		sendBuf.writeByte(msg.getHeader().getPriority());
		sendBuf.writeInt(msg.getHeader().getAttachment().size());
		
		String key=null;
		byte[] keyvalue=null;
		Object value=null;
		for(Map.Entry<String,Object> param:msg.getHeader().getAttachment().entrySet()){
			key=param.getKey();
			keyvalue=key.getBytes("utf-8");
			sendBuf.writeInt(keyvalue.length);
			sendBuf.writeBytes(keyvalue);
			value=param.getValue();
			encoder.encode(ctx, value, sendBuf);
		}
		key=null;
		keyvalue=null;
		value=null;
		
		if(msg.getBody()!=null){
			encoder.encode(ctx, msg.getBody(), sendBuf);
		}
		
		//�ڳ��ȵ�λ�ã���һ��int����4���ֽڴ����趨��Ϣ��ĳ���
		sendBuf.setInt(4, sendBuf.readableBytes());
		//��sendBuf��ӵ�List���ݸ���һ��handler
		out.add(sendBuf);
		
	}

}
