import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;



public class HttpServer {

//	public static final String WEB_ROOT = "C:" + File.separator + "webroot";
//	public static final String WEB_ROOT = File.separator + "Users" + File.separator+ "sonhajeong" + File.separator + "Downloads" + File.separator+ "WebServerTest001" + File.separator +"src" + File.separator + "webserver";
	public static final String WEB_ROOT = "./webroot";
	
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	private static final String USER_AGENT = null;
	
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
		// 통신을 위한 소
		ServerSocket serverSocket = null;
		
		int port = 9999;
		
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
				long time = System.currentTimeMillis();
				request.parse();
				
				/*
				 * 여기서 다시 http reqeust 보내야
				 */
				
				
				JSON user = new JSON("123", "male", "1020", "game");
				String json = user.toString();
				
				
				String campaign = sendPost(json);
				
				
				
								
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();
				time = System.currentTimeMillis() - time;
				System.out.println("time: "+ time);
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
	
	
	private String sendPost(String content) throws Exception {

		String url = "https://naver.com";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = content;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return response.toString();

	}

}
