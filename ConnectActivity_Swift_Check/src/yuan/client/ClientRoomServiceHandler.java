package yuan.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

import yuan.client.SignalClient.SignalEvents;
import yuan.message.SignalMessageType;
import yuan.message.SignalMsg;

/**
 * �����Ƿ����
 * 
 * @author Jack Yuan
 *
 */
public class ClientRoomServiceHandler extends ChannelInboundHandlerAdapter {
	// ���ڻص������ø�client����
	private SignalEvents events;

	public ClientRoomServiceHandler(SignalEvents events) {
		super();
		this.events = events;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		SignalMsg signalmsg = (SignalMsg) msg;
		if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.FORWARD
						.getTypeCode()) {
			// �����Ϣͷ��Ϊ��,������Ϣ����ΪForWard��Ϊ����clientת����������Ϣ
			// ����û�зŵ�ҵ���̳߳أ����߻ص���ȥ
			Map<String, Object> attatchment = signalmsg.getHeader()
					.getAttachment();
			String remoteclient = (String) attatchment.get("sourceaddress");
			System.out.println(remoteclient + ":" + signalmsg.getBody());
		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.JOINROOM
						.getTypeCode()) {
			// ����Ƿ��������ڼ��뷿��Ļ�Ӧ
			// ��Ӧ����Ϣ�����1����2,1��ʾ�Ƿ����ߣ�2��ʾ�ǵڶ�λ���뷿����ˣ��������ֱ�ʾ���뷿��ʧ��
			events.joinRoomResult(signalmsg);
		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.ANSWER
						.getTypeCode()) {
			// ������������Ŀͻ��˵�answer sdpת������
			events.forWardAnswer(signalmsg);

		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.CANDIDATE
						.getTypeCode()) {
			// ������������Ŀͻ��˵�candidate ת������
			events.forWardCandidate(signalmsg);
		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.LEAVEROOM
						.getTypeCode()) {
			// �ͻ����յ�����˷��͵������ͻ���ת�����뿪����
			events.leaveRoomResult(signalmsg);
		} else {
			ctx.fireChannelRead(msg);
		}
	}
}
