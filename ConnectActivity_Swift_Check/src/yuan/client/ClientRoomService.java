package yuan.client;

import android.util.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * ����ඨ����һЩ�ͻ��˷���ҵ��ķ��� ���Ӧ����Android�Ļ�����Ҫ��һ���̳߳���������Ϊ���漰������ķ��ʣ�
 * 
 * @author Jack Yuan
 *
 */
public class ClientRoomService {
	// ������Ҫ����һ��channel�������˷�����Ϣ
	private Channel channel;

	public ClientRoomService(Channel channel) {
		this.channel = channel;
	}

	/**
	 * ���뷿��
	 */
	public void joinRoom(String roomname) {
		channel.writeAndFlush(ClientMsgFactory.buildJoinMsg(roomname));
	}

	/**
	 * �뿪����
	 */
	public void leaveRoom(String roomname) {
		//�ڷ��������뿪����Ϣ֮�󣬲Ż�ѡ��ر������뿪
		channel.writeAndFlush(ClientMsgFactory.buildLeaveMsg(roomname))
				.addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * ����Ϣת�����������������
	 * 
	 * @param roomname
	 * @param msg
	 */
	public void forWardMsg(String roomname, String msg) {
		channel.writeAndFlush(ClientMsgFactory.buildForWardMsg(roomname, msg));
	}

	/**
	 * ������˷���offer
	 * 
	 * @param sdp
	 */
	public void sendOffSdp(String sdp) {
		channel.writeAndFlush(ClientMsgFactory.buildOfferMsg(sdp));
	}

	/**
	 * ������˷���answer
	 * 
	 * @param sdp
	 */
	public void sendAnswerSdp(String sdp) {
		channel.writeAndFlush(ClientMsgFactory.buildAnswerMsg(sdp));
	}

	/**
	 * ������˷���candidate
	 * 
	 * @param id
	 * @param label
	 * @param candidate
	 */
	public void sendCandidate(String id, int label, String candidate) {
		Log.i("tesst", "sendcan"+candidate);
		channel.writeAndFlush(ClientMsgFactory.buildCandidateMsg(id, label,
				candidate));
	}

}
