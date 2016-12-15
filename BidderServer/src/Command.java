import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opencsv.CSVWriter;

public class Command extends Thread{
	public static LinkedList<String[]> adList;
	public static Scanner input;
	public Command()
	{
		adList = new LinkedList<String[]>();
		input = new Scanner(System.in);
	}
	@Override
	public void run(){
		try{
			service();
		}catch(Exception e){
			System.out.println("Port connection end");
		}finally{
		}
	}
	
	public void service() throws IOException, ParseException{
		
		while(true)
		{
			System.out.println("====================");
			System.out.println("0. your IP");
			System.out.println("1. show all AD");
			System.out.println("2. add new AD");
			System.out.println("3. change price");
			System.out.println("4. delete AD");
			System.out.println("5. get .csv file");
			System.out.println("6. get all .csv file");
			System.out.println("====================");
			int menu = nextIntSafe();
			if(menu == 0)
			{
				System.out.println(InetAddress.getLocalHost().getHostAddress());
			}
			else if(menu == 1)
			{
				printAllAd();
			}
			else if(menu == 2)
			{
				System.out.print("AD title: ");
				String title = nextLineSafe();
				System.out.print("price: ");
				String price = nextLineSafe();
				addNewAd(title, price);
			}
			else if(menu == 3)
			{
				System.out.print("AD number: ");
				int adNum = nextIntSafe();
				System.out.print("price: ");
				String price = nextLineSafe();
				changeAdPrice(adNum, price);
			}
			else if(menu == 4)
			{
				System.out.print("AD number: ");
				int adNum = nextIntSafe();
				deleteAd(adNum);
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
			else
			{
				System.out.println("Invalid menu: try again");
				continue;
			}
		}
	}
	private void getCsvFile(String title) throws IOException, ParseException {
		String address = InetAddress.getLocalHost().getHostAddress();
				
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
	private void deleteAd(int adNum) {
		adList.remove(adNum);
	}
	private void printAllAd() {
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
	public int nextIntSafe()
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
	public String nextLineSafe()
	{
		String inputStr = "";
		while(inputStr.trim().equals(""))
		{
			inputStr = input.nextLine();
		}
		return inputStr;
	}
	public String jedisRead(String key)
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
}