package yuan.server.room;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import yuan.message.SignalMessageType;
import yuan.message.SignalMsg;
import yuan.server.ServerConfig;

/**
 * �����ۺϿ����� ����ģʽ �������ݿ�洢����Ļ��Ͳ���Ҫ����ģʽ(����Ŀǰ����Ҫʹ�õ���ģʽ)
 * 
 * @author Jack Yuan
 *
 */
public class RoomControl {
	// �洢�����������еķ���
	private static Map<String, Room> roommap = new HashMap<String, Room>();
	// ���Ʒ���������ͬ��������
	private static Object maplock = new Object();

	private static Logger logger = Logger
			.getLogger(RoomControl.class.getName());

	private static int MAXMEMBER = ServerConfig.MAXMEMBER;

	/**
	 * ˽�з�������ӷ���
	 * 
	 * @param roomname
	 * @param room
	 */
	private void addRoom(String roomname, Room room) {
		synchronized (maplock) {
			roommap.put(roomname, room);
		}

	}

	/**
	 * ˽�з������Ƴ�����
	 * 
	 * @param roomname
	 */
	private void removeRoom(String roomname) {
		synchronized (maplock) {
			roommap.remove(roomname);
		}

	}

	/**
	 * ���캯��
	 */
	public RoomControl() {
		// roommap = new HashMap<String, Room>();
	}

	/**
	 * ���뷿��,���ҷ��ؼ��뷿��󣬷��������
	 * 
	 * @param roomname
	 *            member
	 */
	public int joinRoom(String roomname, Channel member) {
		int size = 0;
		// ���ݷ���������ͬ��������
		synchronized (roomname) {
			// ��������Ѿ�������
			if (roommap.containsKey(roomname)) {
				size = roommap.get(roomname).memberSize();
				if (size < MAXMEMBER && size > 0) {
					// ����������г�Ա���ҷ����ԱС������
					roommap.get(roomname).addMember(member);
					size++;
				} else {
					// ��������Ա�Ѿ����˻����������򷵻�һ��-1
					size = -1;
				}
			} else {
				// ����������ӳ�Ա
				Room newRoom = new Room(roomname);
				newRoom.addMember(member);
				addRoom(roomname, newRoom);
				size = 1;
			}
		}
		logger.info("Client " + member.remoteAddress() + " join room "
				+ roomname + " current size " + size);
		return size;
	}

	/**
	 * �뿪���䣬���ҷ����뿪����󣬷��������
	 * 
	 * @param roomname
	 * @param member
	 * @return
	 */
	public int leaveRoom(String roomname, Channel member) {
		int size = 0;
		// ���ݷ���������ͬ��������
		synchronized (roomname) {
			if (!roommap.containsKey(roomname))
				return 0;
			Room currentRoom = roommap.get(roomname);
			// �ӷ������Ƴ���Ա
			currentRoom.rmvMember(member);
			/**
			 * ��Ա�뿪ʱ���Ƴ������ڷ����sdp��candidatelist
			 */
			currentRoom.removeInfo(member);
			// �ر�member�˿ڣ���֪��Ч��
			member.close();
			size = currentRoom.memberSize();
			// ���һ����Ա�뿪����ʱ���Ƴ�����
			if (size == 0) {
				removeRoom(roomname);
			}
			forWardMsg(ServerMsgFactory.buildLeaveFor(member), roomname, member);
		}
		logger.info("Client " + member.remoteAddress() + " leave room "
				+ roomname + " current size " + size);
		// ��member��Ϊnull������gc����
		member = null;
		return size;
	}

	/**
	 * �뿪��������غ��������ҷ����뿪����󣬷��������
	 * 
	 * @param room
	 * @param member
	 * @return
	 */
	public int leaveRoom(Room room, Channel member) {
		String roomname = room.RoomName();
		// ֱ�ӵ��ú���������roomname�����غ���
		return this.leaveRoom(roomname, member);
	}

	/**
	 * �ж�ĳ��channel�Ƿ��ڴ���һ����һ�������� ��������򷵻ط�����󣬲������򷵻�null
	 * 
	 * @param channel
	 * @return
	 */
	public Room isInaRoom(Channel channel) {
		Room ownroom = null;
		for (Map.Entry<String, Room> room : roommap.entrySet()) {
			if (room.getValue().hasMember(channel)) {
				ownroom = room.getValue();
				break;
			}
		}
		return ownroom;
	}

	/**
	 * ���ͻ��˷�������msgת����������������ͻ���
	 * 
	 * @param msg
	 */
	public void forWardMsg(SignalMsg msg, String roomname, Channel imember) {
		// ȡ����ǰ�ͻ������ڷ���
		Room tgroom = roommap.get(roomname);
		if (tgroom == null) {
			// ������䲻������
			return;
		}
		List<Channel> memberlist = tgroom.memberList();
		for (Channel smember : memberlist) {
			// ���
			if (smember != imember) {
				smember.writeAndFlush(msg);
			}
		}
	}

	/**
	 * ���ͻ��˷���һ�����뷿��ɹ��Ļ�Ӧ
	 * 
	 * @param membernum
	 * @param channel
	 */
	public void sendJoinSuccessResponse(int membernum, Channel channel) {
		// ������Ҫ�õ�channel��Ӧ��room����
		Room room = isInaRoom(channel);
		// ������Ӧ��Ϣ
		SignalMsg responsemsg = ServerMsgFactory.buildJoinResponse(membernum,
				room.getSdpMap(), room.getCandidatesMap());
		System.out.println("�����candidate��"+room.getCandidatesMap());
		// ��ԭ���ŵ�д���Ӧ��Ϣ
		channel.writeAndFlush(responsemsg);
		System.out.println("����һ�������Ӧ����" + channel.remoteAddress());
	}
	/**
	 * ���ͻ��˷���һ�����뷿��ʧ�ܵĻ�Ӧ
	 * @param membernum
	 */
	public void sendJoinFailResponse(int membernum,Channel channel){
		//������Ӧ��Ϣ
		SignalMsg responsemsg=ServerMsgFactory.buildJoinFailResponse(membernum);
		channel.writeAndFlush(responsemsg);
	}
	/**
	 * ���ͻ��˷��͹�����sdp�����ڶ�Ӧ�����������,�������answerת���������ͻ���
	 * 
	 * @param sdp
	 * @param channel
	 */
	public void saveRoomSdpAndForWard(SignalMsg msg, Channel channel) {
		Room room = isInaRoom(channel);
		// ���û�п��ǵ��쳣�����
		room.addSdp(channel, (String) msg.getBody());
		logger.info("�����һ������" + channel.remoteAddress() + "��sdp");
		if (room.memberSize() > 1) {
			// �����ʱ�����Ա����1����Ҫ��֮ǰ�ķ���ת��sdp answer
			// ת��֮ǰ�����һ��channelID
			addChannelId(msg, channel);
			forWardMsg(msg, room.RoomName(), channel);
		}
	}

	/**
	 * ���ͻ��˷��͹�����candidate�����ڶ�Ӧ�����������,������ǵ�һ�����뷿�������ת���������ͻ���
	 * 
	 * @param msg
	 * @param channel
	 */
	public void saveRoomCandidateAndForWard(SignalMsg msg, Channel channel) {
		Room room = isInaRoom(channel);
		Map<String, String> candidate = new HashMap<String, String>();
		candidate.put("id", (String) msg.getHeader().getAttachment().get("id"));
		candidate.put("label", msg.getHeader().getAttachment().get("label")
				.toString());
		candidate.put("candidate", (String) msg.getHeader().getAttachment()
				.get("candidate"));
		room.addCandidate(channel, candidate);
		logger.info("������һ������" + channel.remoteAddress() + "��candidate"+candidate);
		if (room.memberSize() > 1) {
			// ת��֮ǰ�����һ��candidate
			addChannelId(msg, channel);
			forWardMsg(msg, room.RoomName(), channel);
		}
	}

	/**
	 * �ڲ��������������������ͻ���ת��sdp��candidateʱ��������-�ͻ������channelid��������
	 * 
	 * @param msg
	 * @param channel
	 *            sdp�Ļ������attatchment�candidate�����body��
	 */
	private void addChannelId(SignalMsg msg, Channel channel) {
		String channelid = channel.id().asShortText();
		if (msg.getHeader().getMsgType() == SignalMessageType.ANSWER
				.getTypeCode()) {
			// �������Ϣ��ת����answer
			msg.getHeader().getAttachment().put("channelid", channelid);
		} else if (msg.getHeader().getMsgType() == SignalMessageType.CANDIDATE
				.getTypeCode()) {
			// �������Ϣʱת����candidate
			msg.setBody(channelid);

		} else {
			return;
		}
	}
}
