package yuan.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import yuan.server.room.Room;
import yuan.server.room.RoomControl;

/**
 * ��⴦��ͻ��˴ӷ���˶Ͽ����������handler�������pipline���ײ�
 * 
 * @author Jack Yuan
 *
 */
public class OutboundHandler extends ChannelHandlerAdapter {
	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		System.out.println("test�ͻ��� " + ctx.channel().remoteAddress()
				+ " �����˶Ͽ�����");
		// �ͻ�����Ϊ�쳣�ӷ�����˳�ʱ���ᴥ������Ļص�������������Ҫ�ͻ����ٷ������϶�Ӧ���߼�������Դ������

		Channel currentmember = ctx.channel();
		RoomControl control = new RoomControl();
		// �жϵ�ǰ��������ĳ�Ա�Ƿ������һ��������
		Room room = control.isInaRoom(currentmember);

		if (room != null) {
			// ���������һ��������,���뿪�������
			control.leaveRoom(room, currentmember);
		}

	}
}
