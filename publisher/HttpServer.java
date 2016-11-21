import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class HttpServer {

//	public static final String WEB_ROOT = "C:" + File.separator + "webroot";
	public static final String WEB_ROOT = File.separator + "Users" + File.separator+ "sonhajeong" + File.separator + "Downloads" + File.separator+ "WebServerTest001" + File.separator +"src" + File.separator + "webserver";
//	public static final String WEB_ROOT = "./webroot";
	
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	
	private boolean shutdown = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpServer server = new HttpServer();
		server.await();
	}
	
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
		} catch(IOException ie) {
			ie.printStackTrace();
			System.exit(1);
		}
		
		while(!shutdown){
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				
				Request request = new Request(input);
				request.parse();
				System.out.println("input: " + convertStreamToString(input));
				System.out.println("request: " + request);

								
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();
				
				socket.close();
				
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
}
}
