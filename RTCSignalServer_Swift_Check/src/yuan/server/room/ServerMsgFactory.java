package yuan.server.room;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yuan.message.SignalMessageType;
import yuan.message.SignalMsg;
import yuan.message.SignalMsgHeader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ServerMsgFactory {

	/**
	 * ����һ������ת���������ͻ��˵��ı���Ϣ(��Ҫ���ǽ�handler�Ǳߵ�����msg��һ��remoteaddress)
	 * 
	 * @param message
	 * @param channel
	 * @return
	 */
	public static SignalMsg buildTextMsg(SignalMsg message, Channel channel) {
		message.getHeader().getAttachment()
				.put("sourceaddress", channel.remoteAddress().toString());
		return message;
	}

	/**
	 * ����һ�����ڿͻ��˼��뷿�����Ӧ��Ϣ ��Ϣ��λ��ǰ���������0,1,2 ����һ�����������ֱ�Ϊ�����Ա��sdp,candidatelist
	 * 
	 * @return
	 */
	public static SignalMsg buildJoinResponse(int membernum,
			Map<String, String> sdp,
			Map<String, List<Map<String, String>>> candidatelist) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		header.setMsgType(SignalMessageType.JOINROOM.getTypeCode());
		/**
		 * ���϶��󲢲���ֱ��ʹ��jbossmarshalling����룬��Ҫ��������� �������Ƚϴ�������߸��ӽṹ�ĸ���������json
		 * ���ʹ��fastjson ��˵Android�ͻ��˲�ͬ�汾
		 */
		// ��sdpmap ת����jsonstring
		String sdpString = JSON.toJSONString(sdp);
		// ��candidateת����jsontring,����Ĳ������Ա���ref����
		String candidateString = JSON.toJSONString(candidatelist,
				SerializerFeature.DisableCircularReferenceDetect);
		Map<String, Object> attatchment = new HashMap<String, Object>();
		// ���sdp����
		attatchment.put("offer", sdpString);
		// ���candidatelist����
		attatchment.put("candidatelist", candidateString);
		header.setAttachment(attatchment);
		msg.setHeader(header);
		msg.setBody(membernum);
		return msg;
	}
	/**
	 * ����һ�����뷿��ʧ�ܵ���Ϣ
	 * @param membernum
	 * @return
	 */
	public static SignalMsg buildJoinFailResponse(int membernum){
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		header.setMsgType(SignalMessageType.JOINROOM.getTypeCode());
		msg.setHeader(header);
		msg.setBody(membernum);
		return msg;
	}
	/**
	 * ����һ���뿪����Ϣ�����ڷ��͸�������������ͻ���
	 * body����뿪�ͻ��˵�ID
	 * @param member
	 * @return
	 */
	public static SignalMsg buildLeaveFor(Channel member) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		header.setMsgType(SignalMessageType.LEAVEROOM.getTypeCode());
		msg.setHeader(header);
		msg.setBody(member.id().asShortText());
		return msg;
	}

	/**
	 * ���ʹ��main����������һ��
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "string1");
		map.put("2", "string2");
		map.put("3", "string3");
		Map<String, List<Map<String, String>>> mapf = new HashMap<String, List<Map<String, String>>>();
		for (int i = 0; i < 3; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			list.add(map);
			mapf.put("list" + i, list);
		}
		String jsonString = JSON.toJSONString(mapf,
				SerializerFeature.DisableCircularReferenceDetect);
		System.out.println(jsonString);
		
	}
}
