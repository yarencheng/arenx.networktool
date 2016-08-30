package arenx.networktool;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

	private static Logger logger = LoggerFactory.getLogger(Server.class);

	private int port = 12345;
	private InetAddress bindAddress = null;

	private Server(){
		try {
			bindAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			logger.error("Failed to init server", e);
			throw new RuntimeException(e);
		}
	}

	public void start(){

		ServerSocket server;

		try {
			server = new ServerSocket(port, 0, bindAddress);
			logger.info("start server on {}:{}", server.getInetAddress(), server.getLocalPort());
		} catch (IOException e) {
			logger.error("Failed to init server", e);
			throw new RuntimeException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			try {
				server.close();
				logger.info("close server");
			} catch (IOException e) {
				logger.error("Failed to close server", e);
				throw new RuntimeException(e);
			}
		}));

		Socket client;

		while(true){
			try {
				client = server.accept();
			} catch (IOException e) {
				logger.error("Failed to accept socket", e);
				throw new RuntimeException(e);
			}

			new Thread(new Client(client)).start();
		}
	}

	private static class Client implements Runnable{

		private Socket client;

		private Client(Socket client){
			Validate.notNull(client);
			this.client = client;
		}

		@Override
		public void run() {

			InputStream is;
			try {
				is = client.getInputStream();
			} catch (IOException e) {
				logger.error("Failed to get InputStream", e);
				throw new RuntimeException(e);
			}

			byte[] buf = new byte[1024*1024];
			long length = 0;
			long all_length = 0;
			long second_length = 0;

			long logtime = System.currentTimeMillis();

			try {
				while((length=is.read(buf))>0){

					all_length+=length;
					second_length+=length;

					if (System.currentTimeMillis() - logtime > 1000) {
						logger.info("receive speed:{} / {}", Utils.readableNetworkSpeed(second_length), Utils.readableSize(all_length));
						logtime+=1000;
						second_length=0;
					}

					Utils.sleep(1);
				}
			} catch (IOException e) {
				logger.error("Failed to read data", e);
				throw new RuntimeException(e);
			}
			logger.info("close; total recieve {}", Utils.readableSize(all_length));
		}
	}

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder{

		private Server server = new Server();

		private Builder(){

		}

		public Builder setPort(int port){
			server.port=port;
			return this;
		}

		public Builder setBindAddress(String host){
			try {
				server.bindAddress=InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				logger.error("Failed to init server", e);
				throw new RuntimeException(e);
			}
			return this;
		}

		public Server build(){
			return server;
		}
	}
}
