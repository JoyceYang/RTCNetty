package yuan.server.room;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ������
 * 
 * @author Jack Yuan
 *
 */
public class Room {

	// ������ĳ�Աʹ��Netty��Channel����������
	// ��Ա�б�
	private List<Channel> memberlist;
	// ������
	private String roomname = "defaultname";
	// �洢�ŷ������г�Ա��sdp��Ϣ
	// ����ʹ��ChannelId��shortString����Ϊkey��netty5��֤��Ψһ��
	private Map<String, String> allsdps;
	// �洢�ŷ������г�Ա��candidates(�����Ĵ洢�ṹ�е㵣��)
	// ����ʹ��ChannelId��shortString����Ϊkey��netty5��֤��Ψһ��
	private Map<String, List<Map<String, String>>> allcandidates;
	// �������sdp��һ����
	private Object sdplock=new Object();
	
	public Room(String roomname) {
		memberlist = new ArrayList<Channel>();
		allsdps = new HashMap<String, String>();
		allcandidates = new HashMap<String, List<Map<String, String>>>();
		this.roomname = roomname;
	}

	/**
	 * ��Ӷ�Ӧ�ͻ��˵�sdp��Ϣ
	 * 
	 * @param member
	 */
	public void addSdp(Channel member, String sdp) {
		synchronized (sdplock) {
			allsdps.put(member.id().asShortText(), sdp);
			//�������������ļ��뷿����߳�
			sdplock.notifyAll();
		}
		/**
		 * �����Ҫ�õ�ͬ���ȴ�����
		 */
	}

	/**
	 * ���ط����sdp map
	 * 
	 * @return
	 */
	public Map<String, String> getSdpMap() {
		
		synchronized (sdplock) {
			
			while(memberSize()-allsdps.size()>1){
				//����������������Ա��û������sdp����ǰ���뷿����߳�һֱ����
				try {
					sdplock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return allsdps;
			/**
			 *�����Ҫ�õ�ͬ���ȴ�����
			 */
		}
		
	}

	/**
	 * ��Ӧ�Ŀͻ������һ��candidate candidate ����map����ʽ�����
	 * 
	 * @param member
	 * @param candidate
	 */
	public void addCandidate(Channel member, Map<String, String> candidate) {
		// �����Ա��δ�ڷ��䴴��candidate list,���ȴ���
		List<Map<String, String>> candidatelist = allcandidates.get(member.id().asShortText());
		if (candidatelist == null) {
			candidatelist = new ArrayList<Map<String, String>>();
			allcandidates.put(member.id().asShortText(), candidatelist);
		}
		candidatelist.add(candidate);
	}

	/**
	 * �������пͻ��˵�candidatelist
	 * 
	 * @return
	 */
	public Map<String, List<Map<String, String>>> getCandidatesMap() {
		return allcandidates;
	}

	/**
	 * ���ط��������
	 * 
	 * @return
	 */
	public String RoomName() {
		return roomname;
	}

	/**
	 * �������ӳ�Ա
	 * 
	 * @param member
	 */
	public void addMember(Channel member) {
		// ���������û�иó�Ա����Ӹó�Ա
		if (!memberlist.contains(member))
			memberlist.add(member);
	}

	/**
	 * ������ٳ�Ա
	 * 
	 * @param member
	 */
	public void rmvMember(Channel member) {
		memberlist.remove(member);
	}

	/**
	 * ���ط����Ա�ĸ���
	 * 
	 * @return
	 */
	public int memberSize() {
		return memberlist.size();
	}

	public List<Channel> memberList() {
		return memberlist;
	}

	/**
	 * �жϳ�Ա�Ƿ��Ǹ÷�����ĳ�Ա
	 * 
	 * @param tgmember
	 * @return
	 */
	public boolean hasMember(Channel tgmember) {
		boolean isIn = false;
		for (Channel member : memberlist) {
			// ����÷����иó�Ա
			if (tgmember == member) {
				isIn = true;
				break;
			}
		}
		return isIn;
	}

	/**
	 * �ڳ�Ա�뿪����ʱ����Ҫ�Ƴ�����Ա��Ϣ
	 * 
	 * @param member
	 */
	public void removeInfo(Channel member) {
		String memberid = member.id().asShortText();
		synchronized (sdplock) {
			allsdps.remove(memberid);
		}
		allcandidates.remove(memberid);
	}
}
