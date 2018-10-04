package pikachurun_network.v1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress("192.168.0.40", 5001));
		System.out.println("Server is Running at " + InetAddress.getLocalHost());
		List<ObjectOutputStream> oosList = new ArrayList<>();
		
		new Thread() {

			@Override
			public void run() {
				Scanner scan = new Scanner(System.in);
				scan.nextLine();
				int i = 0;
				for(ObjectOutputStream oos : oosList) {
					try {
						oos.writeObject(oosList.size());
						i++;
						oos.writeObject((Integer)i);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}.start();
		
		while(true) {
			Socket socket = serverSocket.accept();
			new Thread() {
	
				@Override
				public void run() {
					ObjectOutputStream oos;
					ObjectInputStream ois;
					try {
						oos = new ObjectOutputStream(socket.getOutputStream());
						ois = new ObjectInputStream(socket.getInputStream());
						System.out.println("Link : " + socket.getRemoteSocketAddress());
						oosList.add(oos);
						UserDisplay ud;
						while(true) {
							ud = (UserDisplay) ois.readObject();
							for(ObjectOutputStream eachOos : oosList) {
								eachOos.writeObject(ud);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}
