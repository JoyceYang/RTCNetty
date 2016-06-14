package yuan.client;

import yuan.message.SignalMsg;

/**
 * ���������ͻ��˵Ĳ�����
 * 
 * @author Jack Yuan
 *
 */
public class TestClient implements SignalClient.SignalEvents {
	private static ClientRoomService service;
	// �����������¼��ص��Ľӿ�
	@Override
	public void joinRoomResult(SignalMsg resultmsg) {
		System.out.println("joinresult" + resultmsg);
		int membersize=(Integer)resultmsg.getBody();
		if(membersize<=0){
			System.out.println("���뷿��ʧ��");
			return;
		}
		if(membersize==1){
			service.sendOffSdp("offersdpoffersdp");
			service.sendCandidate("1", 1, "candidate");
		}else{
			service.sendAnswerSdp("answersdpanswersdp");
			service.sendCandidate("2", 2, "candidate");
		}
	}

	@Override
	public void leaveRoomResult(SignalMsg resultmsg) {

	}

	@Override
	public void forWardAnswer(SignalMsg resultmsg) {
		System.out.println("forwardanswer:"+resultmsg);
	}
	
	@Override
	public void forWardCandidate(SignalMsg resultmsg) {
		System.out.println("forwardanswer:"+resultmsg);
	}
	
	@Override
	public void onChannelClose() {

	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestClient testclient = new TestClient();
		SignalClient client = new SignalClient(testclient);
	    service = new ClientRoomService(client.getChannel());
		service.joinRoom("yuanyijie");
	}

	
}
