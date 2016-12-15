import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opencsv.CSVWriter;

public class Bidder1Client{
	static LinkedList<String[]> adList;
	static Scanner input;
	public static void main(String[] args) throws UnknownHostException, IOException, ParseException
	{
		adList = new LinkedList<String[]>();
		input = new Scanner(System.in);
		
		while(true)
		{
			System.out.println("====================");
			System.out.println("0. synchronize with server");
			System.out.println("1. add new AD");
			System.out.println("2. change price");
			System.out.println("3. delete AD");
			System.out.println("4. show all AD");
			System.out.println("5. get.csv file");
			System.out.println("6. get all .csv file");
			System.out.println("7. exit");
			System.out.println("====================");
			int menu = nextIntSafe();
			Socket socket = new Socket("cidteamn.ddns.net", 9876);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String command = null;
			if(menu == 0)
			{
				command = "{\"command\":\"0\"}";
				out.writeUTF(command);
				String result = null;
				while((result = in.readUTF()) == null) {}
				setList(result);
			}
			else if(menu == 1)
			{
				System.out.print("AD title: ");
				String title = nextLineSafe();
				System.out.print("price: ");
				String price = nextLineSafe();
				addNewAd(title, price);
				command = "{\"command\":\"1\",\"title\":\""+title+"\",\"price\":\""+price+"\"}";
				out.writeUTF(command);
			}
			else if(menu == 2)
			{
				System.out.print("AD number: ");
				int adNum = nextIntSafe();
				System.out.print("price: ");
				String price = nextLineSafe();
				changeAdPrice(adNum, price);
				command = "{\"command\":\"2\",\"AD\":\""+adNum+"\",\"price\":\""+price+"\"}";
				out.writeUTF(command);
			}
			else if(menu == 3)
			{
				System.out.print("AD number: ");
				int adNum = nextIntSafe();
				deleteAd(adNum);
				command = "{\"command\":\"3\",\"AD\":\""+adNum+"\"}";
				out.writeUTF(command);
			}
			else if(menu == 4)
			{
				printAllAd();
			}
			else if(menu == 5)
			{
				System.out.print("AD title: ");
				String title = nextLineSafe();
				getCsvFile(title);
			}
			else if(menu == 6)
			{
				for(int i=0;i<adList.size();++i)
				{
					getCsvFile(adList.get(i)[0]);
				}
			}
			else if(menu == 7)
			{
				if(in != null) in.close();
				if(out != null) out.close();
				if(socket != null) socket.close();
				System.out.println("Bye!");
				return;
			}
			else
			{
				System.out.println("Invalid menu: try again");
				continue;
			}
			if(in != null) in.close();
			if(out != null) out.close();
			if(socket != null) socket.close();
			
		}
	}
	private static void setList(String result) {
		JSONParser parser = new JSONParser();
		JSONObject data = null;
		LinkedList<String[]> adListLocal = new LinkedList<String[]>();
		try{
			data = (JSONObject) parser.parse(result);
		}catch(Exception e){}
		Integer i=0;
		String node = null;
		while((node = (String) data.get(i.toString())) !=null)
		{
			String[] arr = node.split(":");
			adListLocal.add(arr);
			++i;
		}
		adList = adListLocal;
	}
	public static int nextIntSafe()
	{
		String inputStr = "";
		int inputNumber = -1;
		while(inputStr.trim().equals(""))
		{
			inputStr = input.nextLine();
		}
		try{
			inputNumber = Integer.parseInt(inputStr.trim());
		}catch(Exception e)
		{
		}
		return inputNumber;
	}
	public static String nextLineSafe()
	{
		String inputStr = "";
		while(inputStr.trim().equals(""))
		{
			inputStr = input.nextLine();
		}
		return inputStr;
	}
	private static void deleteAd(int adNum) {
		adList.remove(adNum);
	}
	private static void printAllAd() {
		System.out.println("---------------------------------");
		System.out.printf("%-10s%-16s%-8s\n", "AD_ID", "TITLE", "PRICE");
		System.out.println("---------------------------------");
		for(int i=0; i<adList.size();++i)
		{
			System.out.printf("%-10d%-16s%-8s\n", i, adList.get(i)[0], adList.get(i)[1]);
		}
		System.out.println("---------------------------------");
		System.out.println();
		System.out.println();
	}
	private static void addNewAd(String title, String price) {
		String[] list = new String[2];
		list[0] = title;
		list[1] = price;
		adList.add(list);
		System.out.println();
		System.out.println();
		
	}
	private static void changeAdPrice(int adNum, String price) {
		String[] list = adList.get(adNum);
		adList.remove(adNum);
		list[1] = price;
		adList.add(adNum, list);
	}
	public static String jedisRead(String key)
	{
		
		String result = null;
		Socket s = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		try{
			s = new Socket("cidteamn.ddns.net", 7760);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			System.out.println("get stream");
			out.writeUTF(key);
			while((result = in.readUTF())==null)
			{
			}
			return result;
		}catch(Exception e){ 
			System.out.println("cannot connect to server");
			return null; 
		}
		finally{
			try{
				if(in!=null) in.close();
				if(out!=null) out.close();
				if(s!=null) s.close();
			}catch(Exception e){ 
				System.out.println("cannot close stream");
				return null;
			}
				
		}
		
	}
	private static void getCsvFile(String title) throws IOException, ParseException {
		String address = "192.168.219.107";
				
		String logLengthStr = jedisRead(address+"|"+title);
		if(logLengthStr == null)
		{
			System.out.println("No such AD connected to this server");
			return;
		}
		else
		{
			int logLength = Integer.parseInt(logLengthStr);
			String fileName = "./"+title+".csv";
			CSVWriter cw = new CSVWriter(new FileWriter(fileName, false), ',', '"');
			cw.writeNext(new String[] { "PUBLISHER", "TIME", "GENDER", "AGE", "INTEREST" });
			JSONParser parser = new JSONParser();
			for(int i=0;i<logLength;++i)
			{
				// TODO log data key setting
				JSONObject data = (JSONObject) parser.parse(jedisRead(address+"|"+title+":"+i));
				String cat = (String) data.get("cat");
				String[] s = new String[] { (String) data.get("client"), (String) data.get("time"),
						(String) data.get("usersex"), (String) data.get("userrating"), cat };
				cw.writeNext(s);
			}
			cw.close();
			System.out.println(".csv file received");
		}
	}
}