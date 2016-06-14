package yuan.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yuan.codec.signal.SignalMsgDecoder;
import yuan.codec.signal.SignalMsgEncoder;
import yuan.message.SignalMsg;
import android.util.Log;

/**
 * �ͻ������е�����
 * 
 * @author Jack Yuan
 *
 */
public class SignalClient {

	// ����һ��ֻ��һ���̵߳��̳߳أ����ڶ�������
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	// Channel�����ṩ���ⲿʹ��
	private Channel clientchannel;

	private Object channelLock = new Object();

	// �ص��Ľӿ�
	// ����������������Դ
	private WeakReference<SignalEvents> events;
	// I/O �̳߳�
	private EventLoopGroup workgroup;

	// ���ڻص��Ľӿ�
	/**
	 * ��ʱ��δ����
	 * 
	 * @author Jack Yuan
	 *
	 */
	public static interface SignalEvents {
		// ���뷿��Ļص���������ص������һЩ��Ϣ
		void joinRoomResult(SignalMsg resultmsg);

		// �뿪����Ļص�����
		void leaveRoomResult(SignalMsg resultmsg);

		// �յ������ͻ���ת��������answer�Ļص�����
		void forWardAnswer(SignalMsg resultmsg);

		// �յ������ͻ���ת��������candidate�Ļص�����
		void forWardCandidate(SignalMsg resultmsg);

		// �������channel�رյĻص�����
		void onChannelClose();
	}

	/**
	 * ���캯������Ҫ����һ���ص��Ľӿ���Ϊ����
	 * 
	 * @param events
	 */
	public SignalClient(SignalEvents events) {
		this.events=new WeakReference<SignalClient.SignalEvents>(events);
		// �����ڹ��캯����ʱ������ò����첽���������������
		executor.execute(new Runnable() {

			@Override
			public void run() {
				SignalClient.this.connect(ClientConfig.REMOTEHOST,
						ClientConfig.REMOTEPORT);
			}
		});

	}

	/**
	 * ���ⲿһ��channel����
	 * 
	 * @return
	 */
	public Channel getChannel() {
		synchronized (channelLock) {
			if (clientchannel == null)
				try {
					channelLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			return clientchannel;
		}

	}

	/**
	 * ������connect���ܻ�ȡclientchannel
	 * 
	 * @param host
	 * @param port
	 */
	private void connect(String host, int port) {
		 workgroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(workgroup).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(
									new SignalMsgDecoder(1024 * 1024, 4, 4, -8,
											0));
							ch.pipeline().addLast(new SignalMsgEncoder());
							ch.pipeline().addLast(new ReadTimeoutHandler(50));
							ch.pipeline().addLast(new HeartBeatReqHandler());
							ch.pipeline().addLast(
									new ClientRoomServiceHandler(events.get()));
							/**
							 * ���滹��Ҫ���һЩҵ�񼶵�handler
							 */
						}
					});

			ChannelFuture future = bootstrap.connect(host, port).sync();
			synchronized (channelLock) {
				clientchannel = future.channel();
				channelLock.notify();
			}
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.w("newTag", e.toString());
		} finally {
			workgroup.shutdownGracefully();
			executor.shutdown();
			if(clientchannel!=null)
				//�����ӷ������ɹ��󣬲Ż��֪Channel�Ĺر�
			events.get().onChannelClose();
		}
	}
	
	/**
	 * ������������
	 * �÷���Ŀǰֻ��������û�����Ϸ�����֮ǰ�����ٵ����
	 */
	public void disconnect(){
		workgroup.shutdownGracefully();
		executor.shutdownNow();
	}
}
