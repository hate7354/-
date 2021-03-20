package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	
	Socket socket;
	
	public Client(Socket socket) {
		this.socket = socket;
		receive();
	}
	
	public void receive() {
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				try {
					while(true) {
						InputStream in = socket.getInputStream();
						byte[] bf = new byte[512];
						int lh = in.read(bf);
						while(lh == -1 ) throw new IOException();
						System.out.println("[ 메세지 수신 성공 ]" + socket.getRemoteSocketAddress()+ " : " + Thread.currentThread().getName());
						String message = new String(bf,0,lh,"UTF-8");
						for(Client client : Main.clients) {
							client.send(message);
							
						}
					}
				}catch(Exception e) {
					try {
						System.out.println("[메세지 수신 오류]" + socket.getRemoteSocketAddress()+ " : " + Thread.currentThread().getName());
						
					} catch(Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
		};
		Main.threadPool.submit(thread);
	}
	
	public void send (String message) {
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] bf = message.getBytes("UTF-8");
					out.write(bf);
					out.flush();
				} catch(Exception e) {
					try {
						System.out.println("[ 메세지 송신 오류 ]" + socket.getRemoteSocketAddress() + " : " + Thread.currentThread().getName());
						Main.clients.remove(Client.this);
						socket.close();
					} catch(Exception e2) {
						
					}
				}
				
			}
		};
		Main.threadPool.submit(thread);
		
	}
}
