package yuan.message;

//��Ϣ����ö������
public enum SignalMessageType {

	JOINROOM((byte) 1),  //���뷿������ͣ��������ͻ��˶����յ������͵���Ϣ
	LEAVEROOM((byte) 2), //�뿪���������
	FORWARD((byte) 3),   //�򷿼�����������ͻ���ת����Ϣ
	HBREQ((byte) 4),     //��������
	HBREP((byte) 5),     //������Ӧ
	OFFER((byte) 6),     //offer���ͣ��������ͻ����յ���������Ϣʱ�������ǲ�һ����
	ANSWER((byte) 7),    //answer���ͣ��������ͻ����յ���������Ϣʱ������Ҳ�ǲ�һ����
	CANDIDATE((byte) 8);  //candidate ����,�������ͻ����յ�������ʱ������Ҳ�ǲ�һ����

	private byte typecode;

	private SignalMessageType(byte typecode) {
		this.typecode = typecode;
	}

	public byte getTypeCode() {
		return this.typecode;
	}
}
