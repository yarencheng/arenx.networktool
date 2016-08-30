package arenx.networktool;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	private static Logger logger = LoggerFactory.getLogger(Client.class);

	private int port = 12345;
	private long time = 10l;
	private InetAddress serverAddress = null;
	private int threadCount = 1;

	private byte[] outdata ;

	private Client(){
	}

	public void start(){
		for(int i=0;i<threadCount;i++){
			Thread t = new Thread(()->{
				startSingle();
			});
			t.setDaemon(false);
			t.start();
		}
	}

	private void startSingle(){
		Socket client = null;
		try {
			client = new Socket(serverAddress, port);
			logger.info("start client to {}:{}, port:{}", serverAddress.getHostAddress(), port, client.getLocalPort());

			client.setSendBufferSize(outdata.length);

		} catch (IOException e) {
			logger.error("Failed to connect to server", e);
			throw new RuntimeException(e);
		}

		OutputStream os;
		try {
			os = client.getOutputStream();
		} catch (IOException e) {
			logger.error("Failed to get outputstream", e);
			throw new RuntimeException(e);
		}

		long start = System.currentTimeMillis();
		long logtime = System.currentTimeMillis();
		long all_length = 0;
		long second_length = 0;

		while(true){
			try {
				os.write(outdata);
			} catch (IOException e) {
				logger.error("Failed to write data", e);
				throw new RuntimeException(e);
			}

			all_length+=outdata.length;
			second_length+=outdata.length;

			if (System.currentTimeMillis() - logtime > 1000) {
				logger.info("send speed:{} / {}", Utils.readableNetworkSpeed(second_length), Utils.readableSize(all_length));
				logtime+=1000;
				second_length = 0;
			}

			if (System.currentTimeMillis() - start > time*1000) {
				break;
			}
		}


		try {
			os.flush();
			os.close();
		} catch (IOException e) {
			logger.error("Failed to flush data", e);
			throw new RuntimeException(e);
		}

		try {
			client.close();
		} catch (IOException e) {
			logger.error("Failed to close client", e);
			throw new RuntimeException(e);
		}

		logger.info("disconnect; total send {}", Utils.readableSize(all_length));
	}

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder{

		private Client client = new Client();

		private Builder(){

		}

		public Builder setPort(int port){
			client.port=port;
			return this;
		}

		public Builder setTime(Long time){
			client.time=time;
			return this;
		}

		public Builder setThread(int count){
			client.threadCount=count;
			return this;
		}

		public Builder setSendingBufferSize(int size){
			client.outdata=new byte[size];
			for(int i=0;i<client.outdata.length;i++){
				client.outdata[i] = (byte)(48 + (i%10));
			}
			return this;
		}

		public Builder setServerAddress(String server){
			try {
				client.serverAddress=InetAddress.getByName(server);
			} catch (UnknownHostException e) {
				logger.error("Failed to init client", e);
				throw new RuntimeException(e);
			}
			return this;
		}

		public Client build(){
			return client;
		}
	}
}
