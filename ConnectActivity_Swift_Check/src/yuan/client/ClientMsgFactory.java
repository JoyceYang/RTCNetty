package yuan.client;

import java.util.HashMap;
import java.util.Map;

import yuan.message.SignalMessageType;
import yuan.message.SignalMsg;
import yuan.message.SignalMsgHeader;

/**
 * ����һ�������ͻ�����Ϣ�Ĺ�����
 * 
 * @author Jack Yuan
 *
 */
public class ClientMsgFactory {
	/**
	 * �������뷿�����Ϣ
	 * 
	 * @param roomname
	 * @return
	 */
	public static SignalMsg buildJoinMsg(String roomname) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		header.setMsgType(SignalMessageType.JOINROOM.getTypeCode());
		msg.setHeader(header);
		msg.setBody(roomname);
		return msg;
	}

	/**
	 * �����뿪�������Ϣ
	 * 
	 * @param roomname
	 * @return
	 */
	public static SignalMsg buildLeaveMsg(String roomname) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		header.setMsgType(SignalMessageType.LEAVEROOM.getTypeCode());
		msg.setHeader(header);
		msg.setBody(roomname);
		return msg;
	}

	/**
	 * ����ת���������������˵���Ϣ
	 * 
	 * @param roomname
	 * @param formsg
	 * @return
	 */
	public static SignalMsg buildForWardMsg(String roomname, String formsg) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		header.setMsgType(SignalMessageType.FORWARD.getTypeCode());
		// ��������Ϊ��չ��������
		Map<String, Object> attatchment = new HashMap<String, Object>();
		attatchment.put("roomname", roomname);
		header.setAttachment(attatchment);
		msg.setHeader(header);
		msg.setBody(formsg);
		return msg;
	}

	/**
	 * ����sdp offer��Ϣ �ṹ��body��sdp header.type:offer
	 * 
	 * @param offersdp
	 * @return
	 */
	public static SignalMsg buildOfferMsg(String offersdp) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		// ��Ϣ������Ϊoffer
		header.setMsgType(SignalMessageType.OFFER.getTypeCode());
		msg.setHeader(header);
		// ��sdp�ŵ�body��
		msg.setBody(offersdp);
		return msg;
	}

	/**
	 * ����sdp answer��Ϣ �ṹ��body:sdp header.type:answer
	 * 
	 * @param answersdp
	 * @return
	 */
	public static SignalMsg buildAnswerMsg(String answersdp) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		// ��Ϣ������Ϊanswer
		header.setMsgType(SignalMessageType.ANSWER.getTypeCode());
		msg.setHeader(header);
		// ��sdp�ŵ�body��
		msg.setBody(answersdp);
		return msg;
	}

	public static SignalMsg buildCandidateMsg(String id, int label,
			String candidate) {
		SignalMsg msg = new SignalMsg();
		SignalMsgHeader header = new SignalMsgHeader();
		//��Ϣ��������ΪCandidate
		header.setMsgType(SignalMessageType.CANDIDATE.getTypeCode());
		//��candidate�ŵ�map����Ϊattachment
		Map<String,Object> attachment=new HashMap<String,Object>();
		attachment.put("id", id);
		attachment.put("label", label);
		attachment.put("candidate", candidate);
		header.setAttachment(attachment);
		msg.setHeader(header);
		return msg;
	}
}
