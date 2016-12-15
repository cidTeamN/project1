import java.io.IOException;
import java.net.*;
public class DatabaseSocket
{
	static ServerSocket ss;
	public static void main(String[] args) throws IOException
	{
		ss = new ServerSocket(7760);
		while(ss!=null)
		{
			Socket s = ss.accept();
			SocketThread st = new SocketThread(s);
			st.start();
		}
	}
	
}