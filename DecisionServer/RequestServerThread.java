import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
public class RequestServerThread extends Thread{
	private Socket socket;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	static JedisPoolConfig jedisPoolConfig;
	RequestServerThread(Socket s){
		socket = s;
		jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(128);
		setQueueList();
	}
	
	private static QueueList info;
	private static void setQueueList()
	{
		SubscribeServerThread st = new SubscribeServerThread(null);
		info = st.getQueueList();
	}
	private static void returnQueueList()
	{
		SubscribeServerThread st = new SubscribeServerThread(null);
		st.setQueueList(info);
	}
	public static double AHP(double var, double urgency, int len)	//TODO computing
	{
		// Implement AHP
		return var*urgency*len;
	}
	
	public static long toUnixTimeStamp(String season)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = dateFormat.parse(season);
		} catch (ParseException e) {
			System.out.println("Invalid date form");
			return -1;
		}
		long unixTime = (long) date.getTime()/1000;
		return unixTime;
	}
	public static int getAdID(int usersex, int userrating, double[] cat)
	{
		int AdID = -1;	// AdID = -1 -> ad for RTB system
		int qid = info.findNearestQueueID(usersex, userrating, cat);
		PriorityQueue<QueueEntity> selectedQueue = info.getQueue(qid);
		PriorityQueue<QueueEntity> calculatedQueue = new PriorityQueue<QueueEntity>();
		QueueEntity e = null;
		long unixTimeNow = System.currentTimeMillis() / 1000L;
		while((e = selectedQueue.poll()) != null)
		{
			QueueEntity rated = rating(e, unixTimeNow);
			if(rated == null)
			{
				info.deleteFromQueueList(e.ID);
				returnQueueList();
				continue;
			}
			else
			{
				calculatedQueue.add(rated);
			}
		}
		
		// send bidding request to bid server for 10% elements in calculatedQueue
		int cQueueLen = calculatedQueue.size() / 10;
		
		for(int i=0;i<cQueueLen; ++i)
		{
			
		}
		
		return AdID;
	}
	public static QueueEntity rating(QueueEntity e, long unixTimeNow)
	{
		JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379);
		String keyString = ((Integer) e.ID).toString();
		Jedis jedis = pool.getResource();
		JSONParser parser = new JSONParser();
		JSONObject data = null;
		try{
			data = (JSONObject) parser.parse(jedis.get(keyString));
		}catch(Exception error){ 
			pool.close();
			jedis.close();
			return null;
		}
		
		// compute priority with urgency, len
		long time = toUnixTimeStamp((String) data.get("season"))-unixTimeNow;
		if(time == 0)
		{
			pool.close();
			jedis.close();
			return null;
		}
		// TODO computing
		double urgency = 1 / (double)time;
		String str = null;
		try{
			str = jedis.get(keyString+"|len");
		}catch(Exception error){ 
			pool.close();
			jedis.close();
			return null;
		}
		int len = Integer.parseInt(str);
		
		e.var = AHP(e.var, urgency, len);
		
		jedis.close();
		pool.close();
		return e;
	}
	
	@Override
	public void run(){
		try{
			service();
		}catch(IOException e){
			System.out.println("Request Server connection end");
		}finally{
			try{
				closeAll();
			}catch(IOException e){
				System.out.println(e);
			}
		}
	}
	public void service() throws IOException{
		System.out.println("request start");
		dataInputStream = new DataInputStream(socket.getInputStream());
		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		System.out.println("stream get");
		JSONParser parser = new JSONParser();
		String strRslt = null;
		while(true)
		{
			if(socket == null)
			{
				System.out.println("socket end");
				break;
			}
			strRslt = dataInputStream.readUTF();
			System.out.println("check");
			if((strRslt=dataInputStream.readUTF()) != null)
			{
				System.out.println("read data");
				// parsing json
				try{
					JSONObject userData = (JSONObject) parser.parse(strRslt);
					System.out.println(userData.toString());
					int usersex = parseGender((String) userData.get("usersex"));
					int userrating = parseAge((String) userData.get("userrating"));
					double[] cat = pareseCategory((String) userData.get("cat"));
					//getAdID(usersex, userrating, cat);
				}catch(Exception e){
					System.out.println(e);
				}
			}
			else
			{
				System.out.println("Client write end");
				break;
			}
		}
	}
	private double[] pareseCategory(String string) {
		// TODO parsing
		return null;
	}
	private int parseAge(String string) {
		// TODO parsing
		return 0;
	}
	private int parseGender(String string) {
		// TODO parsing
		return 0;
	}
	public void closeAll() throws IOException{
		System.out.println("Close All");
		if(dataInputStream != null) dataInputStream.close();
		if(dataOutputStream != null) dataOutputStream.close();
		if(socket != null) socket.close();
		
	}
}