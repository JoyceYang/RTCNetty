package yuan.client.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ������һ��json���빤���࣬ʹ��FastJson,��Object����ɶ������ٷŵ�ByteBuf��
 * 
 * @author lenovo
 *
 */
public class JsonEncoder extends MessageToByteEncoder<Object> {

	// ��λbyte���������洢jsonByte�ĳ���
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	@Override
	public void encode(ChannelHandlerContext ctx, Object object, ByteBuf out)
			throws Exception {
		int lengthPos = out.writerIndex();
		out.writeBytes(LENGTH_PLACEHOLDER);
		byte[] jsonbytes = JSON.toJSONBytes(object,
				SerializerFeature.DisableCircularReferenceDetect);
		out.writeBytes(jsonbytes);

		// �ڳ��ȿ�ͷ��λ��д��jsonbytes�ĳ���
		out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
	}

}
