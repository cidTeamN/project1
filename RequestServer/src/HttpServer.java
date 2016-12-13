import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class HttpServer {

//	public static final String WEB_ROOT = "C:" + File.separator + "webroot";
//	public static final String WEB_ROOT = File.separator + "Users" + File.separator+ "sonhajeong" + File.separator + "Downloads" + File.separator+ "WebServerTest001" + File.separator +"src" + File.separator + "webserver";
	public static final String WEB_ROOT = "./webroot3";
	
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
		String ads= "\"http://nv1.tveta.naver.net/libs/1143/1143435/20161130103255-sNIZbSaG.jpg\"";
		
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
			String json = null;
			
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				
				Request request = new Request(input);
				long time = System.currentTimeMillis();
				request.parse();
				
				
				if(!request.getUri().equals("/favicon.ico") && !request.getUri().equals("/result.html")){
					json = request.getJSON().toString();
		
				Socket socket2 = new Socket("cidteamn.ddns.net", 7080);
				DataInputStream in2 = new DataInputStream(socket2.getInputStream());
				DataOutputStream out2 = new DataOutputStream(socket2.getOutputStream());
				
				out2.writeUTF(json);
				String result2 = null;
				while((result2=in2.readUTF())==null){}
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(result2);
				String imageUrl = (String) jsonObject.get("url");
				ads = imageUrl;
				
				}
				
				
				
				if(!request.getUri().equals("/favicon.ico") && !request.getUri().equals("/result.html")){
		//		FileWriter fWriter = null;
		//		BufferedWriter writer = null;
		
				String file = "webroot3/" + request.getUri().substring(0);
				String newFile = "webroot3/" + request.getid()+".html";
				
				File htmlTemplateFile = new File(file);
				String htmlString = FileUtils.readFileToString(htmlTemplateFile, Charset.forName("UTF-8"));
				htmlString = htmlString.replace("$ads", ads);
			//	htmlString = htmlString.replace("$body", body);
				File newHtmlFile = new File(newFile);
		//		FileUtils.writeStringToFile(newHtmlFile, htmlString,Charset.forName("UTF-8"));
				FileUtils.writeStringToFile(newHtmlFile, htmlString,StandardCharsets.UTF_8);
				request.setUri(request.getid()+".html");
				
				}
				
				
				
				/*
				Path path = Paths.get("webroot/index.html");
				Charset charset = StandardCharsets.UTF_8;

				String content = new String(Files.readAllBytes(path), charset);
				System.out.println("hell: "+ content);
				content = content.replace("$ads", ads);
				Files.write(path, content.getBytes(charset));
				
				*/
				
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

		String url = "http://45.249.161.242:8765";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Accept-Charset","UTF-8"); 
		String urlParameters = content;
	//	System.setProperty("http.keepAlive", "false");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
		writer.write(urlParameters);
		
		writer.close();
	//	wr.close();
	//	wr.writeBytes(urlParameters);
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
	
	private void sendGet() throws Exception {

		String url = "http://35.165.179.247/index2.html";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
//		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
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

	}


}
