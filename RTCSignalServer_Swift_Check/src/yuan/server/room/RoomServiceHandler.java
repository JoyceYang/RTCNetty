package yuan.server.room;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yuan.message.SignalMessageType;
import yuan.message.SignalMsg;

/**
 * 
 * @author Jack Yuans
 *
 */
public class RoomServiceHandler extends ChannelHandlerAdapter {
	// �������ķ������
	private static RoomControl control = new RoomControl();

	// ���ӵ�ҵ��ŵ�ҵ���̳߳���
	// �½�һ������Ӧ���̳߳�
	private static ExecutorService executors = Executors.newCachedThreadPool();

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
			throws Exception {
		final SignalMsg signalmsg = (SignalMsg) msg;
		// ���ݲ�ͬ��Ϣ�����ͽ��в�ͬ�Ĳ���
		// ��߲��ʺ���switch���,��if else
		// ����ǿͻ���������뷿�������
		if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.JOINROOM
						.getTypeCode()) {
			executors.execute(new Runnable() {
				// ���뷿���ʱ����Ҫһ����ʱ�ȴ��Ĳ���
				@Override
				public void run() {
					// ��������message �� body��ȡ
					String roomname = (String) signalmsg.getBody();
					// ע�����membernum�ǳ�Ա���뷿��֮��ĳ�Ա��
					int membernum = control.joinRoom(roomname, ctx.channel());
					if (membernum == -1) {
						// -1������뷿��ʧ��
						control.sendJoinFailResponse(membernum, ctx.channel());
					} else
						control.sendJoinSuccessResponse(membernum,
								ctx.channel());
				}
			});

		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.LEAVEROOM
						.getTypeCode()) {
			// �������û������뿪����
			String roomname = (String) signalmsg.getBody();
			control.leaveRoom(roomname, ctx.channel());
		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.FORWARD
						.getTypeCode()) {
			// ��ͨ��ת����Ϣ��������������Ա
			Map<String, Object> attatchment = signalmsg.getHeader()
					.getAttachment();
			String roomname = (String) attatchment.get("roomname");
			control.forWardMsg(
					ServerMsgFactory.buildTextMsg(signalmsg, ctx.channel()),
					roomname, ctx.channel());
		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.OFFER
						.getTypeCode()) {
			// �ͻ��������˷���offer��Ϣ
			control.saveRoomSdpAndForWard(signalmsg, ctx.channel());
		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.ANSWER
						.getTypeCode()) {
			// �ͻ��������˷���answer��Ϣ
			control.saveRoomSdpAndForWard(signalmsg, ctx.channel());
		} else if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.CANDIDATE
						.getTypeCode()) {
			// �ͻ��������˷���candidate
			// ����˱���ͻ��˴��ݹ��������ݲ���
			control.saveRoomCandidateAndForWard(signalmsg, ctx.channel());
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	/**
	 * ��Ա�ļ�����뿪��Ӧ����ͻ����Ƿ�����ʾ���Ա��ÿͻ��˶�̬�����peerconnection����Ⱦ��
	 */

}
