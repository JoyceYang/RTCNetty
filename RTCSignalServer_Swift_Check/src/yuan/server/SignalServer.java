package yuan.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.logging.Logger;

import yuan.codec.signal.SignalMsgDecoder;
import yuan.codec.signal.SignalMsgEncoder;
import yuan.server.room.RoomServiceHandler;

/**
 * �����Ƿ���˵�������
 * @author Jack Yuan
 *
 */
public class SignalServer {
	
	private  static Logger logger=Logger.getLogger(SignalServer.class.getName());
	
	private void start(int port) {
		EventLoopGroup bossgroup = new NioEventLoopGroup();
		EventLoopGroup workgroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossgroup, workgroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							//��pipline���ӶϿ�����handler
							ch.pipeline().addLast(new OutboundHandler());
							//��pipline����������Ϣ�Ľ�����
							ch.pipeline().addLast(
									new SignalMsgDecoder(1024 * 1024, 4, 4, -8,
											0));
							//��pipline����������Ϣ�ı�����
							ch.pipeline().addLast(new SignalMsgEncoder());
							ch.pipeline().addLast(new ReadTimeoutHandler(50));
							ch.pipeline().addLast(new RoomServiceHandler());
							ch.pipeline().addLast(new HeartBeatRepHandler());
							
							/**
							 * Ҫ����������һЩ�����ҵ���͵�handler
							 */
							
						}
					});
			
			logger.info("The signalserver is working");
			ChannelFuture future = bootstrap.bind(ServerConfig.SERVER_HOST,port).sync();
			future.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossgroup.shutdownGracefully();
			workgroup.shutdownGracefully();
		}
	}
	
	/**
	 * ������������
	 * @param args
	 */
	public static void main(String[] args) {
		//�����õĶ˿ڼ������Կͻ��˵���Ϣ
		new SignalServer().start(ServerConfig.SERVER_PORT);
	}
}
