import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class RequestThread extends Thread{
	private Socket socket;
	DataInputStream dataInputStream;
	String ads = null;
	DataInputStream in2;
	DataOutputStream out2;
	Socket socket2;
	String result2;
	

	
	RequestThread(Socket s){
		socket = s;

	}
	
	
	
	@Override
	public void run(){
		try{
			service();
		}catch(IOException e){
			System.out.println("Subscribe Server connection end");
		}finally{
			try{
				closeAll();
			}catch(IOException e){
				System.out.println(e);
			}
		}
	}
	
	
	
	public void service() throws IOException{
		
		
			//Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			String json = null;
			
				try{
				//	socket = serverSocket.accept();
					input = socket.getInputStream();
					output = socket.getOutputStream();
					
					Request request = new Request(input);
					long time = System.currentTimeMillis();
					request.parse();
					
					
					
					if(!request.getUri().equals("/favicon.ico") && !request.getUri().equals("/result.html")){
						json = request.getJSON().toString();
			
					
					socket2 = new Socket("cidteamn.ddns.net", 7080);

					in2 = new DataInputStream(socket2.getInputStream());
					out2 = new DataOutputStream(socket2.getOutputStream());
					
					out2.writeUTF(json);
					result2 = null;
				
				//	Thread.sleep(800);
					
					while((result2=in2.readUTF())==null){}

					if(out2!=null) out2.close();
					if(in2!=null) in2.close();

					if(socket2!=null ) socket2.close();

					/*
					while(in2.available()>0)
			         {
			            // reads characters encoded with modified UTF-8
			            result2 = in2.readUTF();
			            System.out.println("text : " +result2);
			         }
					*/

					
					JSONParser parser = new JSONParser();
					JSONObject jsonObject = (JSONObject) parser.parse(result2);
					String imageUrl = (String) jsonObject.get("url");
					ads = imageUrl;
					
					
					System.out.println("ads :" + ads);
				
					}
			//		System.out.println("connectin done!!");
					
					
					if(!request.getUri().equals("/favicon.ico") && !request.getUri().equals("/result.html")){
			
			
					String file = "webroot3/" + request.getUri().substring(0);
					String newFile = "webroot3/" + request.getid()+".html";
					
					File htmlTemplateFile = new File(file);
					String htmlString = FileUtils.readFileToString(htmlTemplateFile, Charset.forName("UTF-8"));
					htmlString = htmlString.replace("$ads", ads);
					File newHtmlFile = new File(newFile);
					FileUtils.writeStringToFile(newHtmlFile, htmlString,StandardCharsets.UTF_8);
					request.setUri(request.getid()+".html");
					
					System.out.println();
					}
					
					
					
					Response response = new Response(output);
					response.setRequest(request);
					response.sendStaticResource();
					time = System.currentTimeMillis() - time;
			//		System.out.println("time: "+ time);
					

					socket.close();

					
						
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				
			
		}
		
	
	
	public void closeAll() throws IOException{
		if(dataInputStream != null) dataInputStream.close();
		if(socket != null) socket.close();
	}
		
	
	
}