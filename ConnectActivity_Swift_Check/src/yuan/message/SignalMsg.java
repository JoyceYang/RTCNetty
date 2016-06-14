package yuan.message;
/**
 * ������Ϣ�ṹ��
 * @author Jack Yuan
 *
 */
public class SignalMsg {
	//��Ϣͷ
	private SignalMsgHeader header;
	//��Ϣ��
	private Object body;
	
	//���캯��
	public SignalMsg(){
		
	}

	public final SignalMsgHeader getHeader() {
		return header;
	}

	public final void setHeader(SignalMsgHeader header) {
		this.header = header;
	}

	public final Object getBody() {
		return body;
	}

	public final void setBody(Object body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "SignalMsg [header=" + header + ", body=" + body + "]";
	}
	
	
}
