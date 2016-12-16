import java.io.*;
import java.net.*;

public class PortThread extends Thread{
	private ServerSocket serverSocket = null;
	private int portNumber;
	public PortThread(int port)
	{
		portNumber = port;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("port listen : " + port);
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
			try{
				closeAll();
			}catch(IOException e){
				System.out.println(e);
			}
		}
	}
	public void closeAll() throws IOException{
		if(serverSocket != null)
			serverSocket.close();
	}
	public void service() throws IOException{
		while(true)
		{
			System.out.println("Waiting...\n");
			Socket socket = serverSocket.accept();
			System.out.println("Server socket accepted");
			if(portNumber == 9999)
			{
				RequestThread st = new RequestThread(socket);
				st.start();
			}else {
				System.out.println("port not allowd : "+ portNumber);
			}
		}
	}
}