package yuan.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

import yuan.message.SignalMessageType;
import yuan.message.SignalMsg;
import yuan.message.SignalMsgHeader;

/**
 * �������������
 * 
 * @author lenovo
 *
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

	// heartbeat
	private volatile ScheduledFuture<?> heartbeat;
	// ������������Ƿ��Ѿ�����
	private boolean isWorking = false;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		if (!isWorking) {
			// ����һ��ÿ������ִ��һ����������Ķ�ʱ����
			heartbeat = ctx.executor().scheduleWithFixedDelay(
					new HeartBeatTask(ctx), 0, 5, TimeUnit.SECONDS);
			isWorking = true;
		}
	}

	/**
	 * �����pipline�ﴫ�ݹ������뻯������Ϣ
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		SignalMsg signalmsg = (SignalMsg) msg;

		if (signalmsg.getHeader() != null
				&& signalmsg.getHeader().getMsgType() == SignalMessageType.HBREP
						.getTypeCode()) {
//			logger.info("rev a heartbeat from server");
		}else{
			ctx.fireChannelRead(msg);
		}
	}

	/**
	 * �쳣��׽,�����쳣��ʱ���ֹͣ�������
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(heartbeat!=null){
			heartbeat.cancel(true);
			heartbeat=null;
			isWorking=false;
		}
		ctx.fireExceptionCaught(cause);
	}

	private class HeartBeatTask implements Runnable {
		// ���ڷ�����Ϣ��netty������
		private ChannelHandlerContext ctx;
		// ������Ϣ
		private SignalMsg hbmsg;

		public HeartBeatTask(ChannelHandlerContext ctx) {
			this.ctx = ctx;
			hbmsg = buildHeartBeatMsg();
		}

		@Override
		public void run() {
//			logger.info("send a heartbeat msg to server");
			ctx.writeAndFlush(hbmsg);
		}

		private SignalMsg buildHeartBeatMsg() {
			SignalMsg msg = new SignalMsg();
			SignalMsgHeader header = new SignalMsgHeader();
			header.setMsgType(SignalMessageType.HBREQ.getTypeCode());
			msg.setHeader(header);
			return msg;
		}
	}
}
