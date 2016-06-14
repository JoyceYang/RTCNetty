package yuan.client.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

public class JsonDecoder extends LengthFieldBasedFrameDecoder {

	public JsonDecoder(int maxObjectsize) {
		//�������õ�ԭ��
		super(maxObjectsize, 0, 4, 0, 4);
	}

	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		// ���ø����������frame����ɵ�obj��byte[]
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		// frame�пɶ���byte��
		int size = frame.readableBytes();
		// ����byte����
		byte[] jsonbytes = new byte[size];
		// ��byte��ȡ��������
		frame.readBytes(jsonbytes);
		Object obj = JSON.parse(jsonbytes,
				Feature.DisableCircularReferenceDetect);
		return obj;
	}
}
