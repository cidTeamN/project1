import java.io.*;
import java.net.*;
import java.util.LinkedList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SendPriceThread extends Thread{
	Socket socket;
	public static LinkedList<String[]> adList;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	public SendPriceThread(Socket s)
	{
		socket = s;
	}
	@Override
	public void run(){
		try{
			service();
		}catch(IOException e){
		}finally{
			try{
				closeAll();
			}catch(Exception e){}
		}
	}
	public static void getAdList()
	{
		adList = Command.adList;
	}
	public void service() throws IOException{
		dataInputStream = new DataInputStream(socket.getInputStream());
		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		
		getAdList();
		String strRslt = null;
		JSONParser parser = new JSONParser();
		String title = null;
		String price = null;
		while(true)
		{
			if((strRslt=dataInputStream.readUTF()) != null)
			{
				// parsing json
				try{
					JSONObject data = (JSONObject) parser.parse(strRslt);
					title = (String) data.get("title");
					if(title == null || title.equals(""))
					{
						strRslt = null;
						continue;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				price = findWithTitle(title);
				String sendStr = "{\"price\":\""+price+"\"}";
				dataOutputStream.writeUTF(sendStr);
				dataOutputStream.flush();
				//System.out.println("Request: "+title+", send: "+price+" won");
				
			}
			else{
				closeAll();
				return;
			}
		}
		
	}
	private String findWithTitle(String title) {
		for(int i=0;i<adList.size();++i)
		{
			if(adList.get(i)[0].equals(title))
			{
				return adList.get(i)[1];
			}
		}
		return "0";
	}
	public void closeAll() throws IOException{
		if(dataInputStream != null) dataInputStream.close();
		if(dataOutputStream != null) dataOutputStream.close();
		if(socket != null) socket.close();
	}
	public static LinkedList<String[]> getList()
	{
		return adList;
	}
	public static void setList(LinkedList<String[]> list)
	{
		adList = list;
	}
}