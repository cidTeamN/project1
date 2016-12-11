import java.io.*;

public class Server
{
	public static void main(String[] args) throws IOException
	{
		PortThread pt1 = new PortThread(8765);	// request server connection
		pt1.start();
		PortThread pt2 = new PortThread(5678);	// bidder server connection
		pt2.start();
		
	}
}