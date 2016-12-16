import java.io.*;

public class Server
{
	public static final String WEB_ROOT = "./webroot3";

	public static void main(String[] args) throws IOException
	{
		PortThread pt1 = new PortThread(9999);	// request server connection
		pt1.start();
		
		
	}
}