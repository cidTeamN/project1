import java.io.*;
import java.net.*;

public class SendPrice extends Thread{
	ServerSocket serverSocket;
	public SendPrice() throws IOException
	{
		try {
			serverSocket = new ServerSocket(4567);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run(){
		try{
			service();
		}catch(IOException e){
			System.out.println("Port connection end");
		}finally{
		}
	}
	public void service() throws IOException{
		while(true)
		{
			Socket socket = serverSocket.accept();
			SendPriceThread spt = new SendPriceThread(socket);
			spt.start();
		}
	}
}