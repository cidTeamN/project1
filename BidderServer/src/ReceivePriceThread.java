import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReceivePriceThread extends Thread{
	ServerSocket serverSocket;
	static LinkedList<String[]> adList;
	public ReceivePriceThread() throws IOException
	{
		try {
			serverSocket = new ServerSocket(9876);
			adList = Command.adList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run(){
		try{
			service();
		}finally{
		}
	}
	public void service(){
		while(true)
		{
			DataInputStream in = null;
			DataOutputStream out = null;
			Socket socket = null;
			try{
				socket = serverSocket.accept();
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				String result = null;
				while((result = in.readUTF()) == null)
				{
				}
				JSONParser parser = new JSONParser();
				try {
					adList = Command.adList;
					JSONObject data = (JSONObject) parser.parse(result);
					String cmd = (String) data.get("command");
					if(cmd.equals("0"))	// Sync
					{
						out.writeUTF(toJsonString());
						out.flush();
					}
					else if(cmd.equals("1"))	// add new AD
					{
						addNewAd((String) data.get("title"), (String) data.get("price"));
						System.out.println("add from client: "+(String)data.get("title")+","+(String)data.get("price"));
					}
					else if(cmd.equals("2"))	// change price
					{
						changeAdPrice(Integer.parseInt((String) data.get("AD")), (String) data.get("price"));
						System.out.println("change from client: "+(String)data.get("AD")+","+ (String)data.get("price"));
					}
					else if(cmd.equals("3"))	// delete AD
					{
						System.out.println("delete from client: "+(String) data.get("AD"));
						deleteAd(Integer.parseInt((String) data.get("AD")));
					}
					Command.adList = adList;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}catch(Exception e)
			{
			}
			finally{
				try{
					if(in!=null) in.close();
					if(out!=null) out.close();
					if(socket!=null) socket.close();
				}catch(Exception e){}
			}
		}
	}
	private void addNewAd(String title, String price) {
		String[] list = new String[2];
		list[0] = title;
		list[1] = price;
		adList.add(list);
		System.out.println();
		System.out.println();
		
	}
	private void changeAdPrice(int adNum, String price) {
		String[] list = adList.get(adNum);
		adList.remove(adNum);
		list[1] = price;
		adList.add(adNum, list);
	}
	private void deleteAd(int adNum) {
		adList.remove(adNum);
	}
	public String toJsonString()
	{
		JSONObject json = new JSONObject();
		for(int i=0; i<adList.size();++i)
		{
			json.put(i+"", adList.get(i)[0]+":"+adList.get(i)[1]);
		}
		String jsonStr = json.toString();
		System.out.println("jsonStr: "+jsonStr);
		return jsonStr;
	}
}