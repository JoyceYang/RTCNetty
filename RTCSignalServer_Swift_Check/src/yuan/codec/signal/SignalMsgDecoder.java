package yuan.codec.signal;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

import yuan.client.util.JsonDecoder;
import yuan.message.SignalMsg;
import yuan.message.SignalMsgHeader;

public class SignalMsgDecoder extends LengthFieldBasedFrameDecoder {
	// private SignalMarshallingDecoder decoder;
	private JsonDecoder decoder;

	/**
	 * 
	 * @param maxFrameLength
	 *            buf���ĳ���
	 * @param lengthFieldOffset
	 *            buf����ֵ��ʼ��λ��
	 * @param lengthFieldLength
	 *            buf����
	 * @param lengthAdjustment
	 * @param initialBytesToStrip
	 *            ��ȡ����֮ǰ��
	 */
	public SignalMsgDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip);
		// decoder = MarshallingCodeCFactory.buildMarshallingDecoder();
		decoder = new JsonDecoder(1024 << 2);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {

		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		// ������Ϣ����Ϣͷ,Ҫ�������˳��һ��
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		header.setCrcCode(frame.readInt());
		header.setLength(frame.readInt());
		header.setSessionId(frame.readLong());
		header.setMsgType(frame.readByte());
		header.setPriority(frame.readByte());

		// ��ȡ�����ĸ���
		int attachsize = frame.readInt();
		if (attachsize > 0) {
			Map<String, Object> attachment = new HashMap<String, Object>(
					attachsize);
			int keySize = 0;
			byte[] keyArray = null;
			String key = null;
			for (int i = 0; i < attachsize; i++) {
				keySize = frame.readInt();
				keyArray = new byte[keySize];
				frame.readBytes(keyArray);
				key = new String(keyArray, "utf-8");
				attachment.put(key, decoder.decode(ctx, frame));
			}
			key = null;
			keyArray = null;
			header.setAttachment(attachment);
		}

		// ByteBuf��ʣ������ݴ���0��˵����body����
		if (frame.readableBytes() > 0) {
			msg.setBody(decoder.decode(ctx, frame));
		}
		msg.setHeader(header);
		return msg;
	}

}
