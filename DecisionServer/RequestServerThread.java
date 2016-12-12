import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
public class RequestServerThread extends Thread{
	private Socket socket;
	BufferedReader dataInputStream;
	OutputStreamWriter dataOutputStream;
	static JedisPoolConfig jedisPoolConfig;
	static JSONParser parser;
	RequestServerThread(Socket s){
		parser = new JSONParser();
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
	public static String getAdUrl(int usersex, int userrating, double[] cat)
	{
		int AdID = -1;	// AdID = -1 -> ad for RTB system
		int qid = info.findNearestQueueID(usersex, userrating, cat);
		Comparator<QueueEntity> cmp = new QueueEntityComparator();
		PriorityQueue<QueueEntity> selectedQueue = info.getQueue(qid);
		PriorityQueue<QueueEntity> calculatedQueue = new PriorityQueue<QueueEntity>(cmp);
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
		
		if(calculatedQueue.size() == 0) return null;
		// TODO send bidding request to bid server for 10% elements in calculatedQueue. percent can be changed
		int cQueueLen = calculatedQueue.size() / 10 + 1;
		
		JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379);
		String keyString = ((Integer) AdID).toString();
		Jedis jedis = pool.getResource();
		JSONObject data = null;
		Iterator<QueueEntity> iter = calculatedQueue.iterator();
		PriorityQueue<QueueEntity> finalListQueue = new PriorityQueue<QueueEntity>(cmp);
		for(int i=0;i<cQueueLen; ++i)
		{
			QueueEntity it = iter.next();
			String url = null;
			String title = null;
			try {
				data = (JSONObject) parser.parse(jedis.get(""+it.ID));
				url = (String) data.get("url");
				title = (String) data.get("title");
			} catch (org.json.simple.parser.ParseException e1) {
				continue;
			}
			if(url==null) continue;
			if(title==null) continue;
			
			QueueEntity entity = ratingWithPrice(it, url, title);
			if(entity != null) finalListQueue.add(entity);
		}
		if(finalListQueue.size() == 0) 
		{
			pool.close();
			jedis.close();
			return null;
		}
		AdID = finalListQueue.peek().ID;
		keyString = ((Integer) AdID).toString();
		data = null;
		try{
			data = (JSONObject) parser.parse(jedis.get(keyString));
		}catch(Exception error)
		{
			pool.close();
			jedis.close();
			return null;
		}
		String upload = (String) data.get("upload");
		pool.close();
		jedis.close();
		return upload;
	}
	private static QueueEntity ratingWithPrice(QueueEntity it, String url, String title) {
		try {
			Socket smallSocket = new Socket(url, 4567);
			DataInputStream in = new DataInputStream(smallSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(smallSocket.getOutputStream());
			String strTitle = "{\"title\":\""+title+"\"}";
			out.writeUTF(strTitle);
			out.flush();
			String strRslt = null;
			while((strRslt=in.readUTF()) == null)
			{
			}
			
			JSONObject priceJSON = (JSONObject) parser.parse(strRslt);
			String strPrice = (String) priceJSON.get("price");
			if(out!=null) out.close();
			if(in!=null) in.close();
			if(smallSocket!=null) smallSocket.close();
			// TODO price reflection rate can be changed
			it.var = it.var*0.5 + Integer.parseInt(strPrice)*0.5;
			return it;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public static QueueEntity rating(QueueEntity e, long unixTimeNow)
	{
		JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379);
		String keyString = ((Integer) e.ID).toString();
		Jedis jedis = pool.getResource();
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
		dataInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "EUC-KR"));
		dataOutputStream = new OutputStreamWriter(socket.getOutputStream());
		
		System.out.println("stream get");
		String strRslt ="";
		char charRslt = (char) -1;
		boolean start = false;
		boolean end = false;
		while(true)
		{
			//System.out.println(socket.getInetAddress());
			//strRslt=dataInputStream.readLine();
			charRslt=(char) dataInputStream.read();
			if(charRslt=='{')
			{
				start = true;
				end = false;
			}
			if(start)
			{
				strRslt += charRslt;
				System.out.print((char)charRslt);
			}
			if(charRslt=='}')
			{
				start = false;
				end = true;
				break;
			}
		}
		//if(dataInputStream != null) dataInputStream.close();
		
		
		System.out.println("content : "+strRslt);
		System.out.println("length : "+strRslt.length());
		String content = strRslt;
		//strRslt = strRslt.toString();
		String imageUrl = "{\"url\": \"https://www.hello.com/img_/hello_logo_hero.png\"}";
		try{
			JSONObject userData = (JSONObject) parser.parse(content);
			int usersex = parseGender((String) userData.get("usersex"));
			int userrating = parseAge((String) userData.get("userrating"));
			double[] cat = parseCategory((String) userData.get("cat"));
			System.out.println((String) userData.get("usersex"));
			System.out.println((String) userData.get("userrating"));
			System.out.println((String) userData.get("cat"));
			String strUrl = getAdUrl(usersex, userrating, cat);
			if(strUrl != null) imageUrl = "{\"url\" \""+strUrl+"\"}";
		}catch(Exception e){
			e.printStackTrace();
		};
		
		try{
			dataOutputStream.write(imageUrl);
			System.out.println("Writing");
		}catch(Exception e){ e.printStackTrace(); }
		
		try{
			dataOutputStream.flush();
		}catch(Exception e){ e.printStackTrace(); }
		System.out.println("Write end");
		if(dataOutputStream != null) dataOutputStream.close();
		if(dataInputStream != null) dataInputStream.close();
	}
	private double[] parseCategory(String string) {
		
		if(string.equals("health"))
		{
			double[] result = { 5.0, 0.0, 4.5, 2.5 };
			return result;
		}
		else if(string.equals("child"))
		{
			double[] result = { 2.5, 1.0, -1.0, -2.5 };
			return result;
		}
		else if(string.equals("politics"))
		{
			double[] result = { -0.5, 5.0, -4.5, -3.5 };
			return result;
		}
		else if(string.equals("fashion"))
		{
			double[] result = { 2.5, -2.0, 4.0, 3.5 };
			return result;
		}
		else if(string.equals("pet"))
		{
			double[] result = { 3.0, -5.0, 3.5, -1.0 };
			return result;
		}
		else if(string.equals("drama"))
		{
			double[] result = { -4.5, -4.0, 4.5, -0.5 };
			return result;
		}
		else if(string.equals("game"))
		{
			double[] result = { 4.5, -5.0, 5.0, 4.0 };
			return result;
		}
		else if(string.equals("leisure"))
		{
			double[] result = { 4.0, -4.0, -2.0, -2.5 };
			return result;
		}
		else if(string.equals("education"))
		{
			double[] result = { -3.5, 4.0, 1.5, -2.5 };
			return result;
		}
		else
		{
			double[] result = { 0.0, 0.0, 0.0, 0.0 };
			return result;
		}
		
	}
	private int parseAge(String string) {
		try{
			int ageInt = Integer.parseInt(string);
			if(ageInt >= 10 && ageInt <= 19)
			{
				return 1;
			}
			else if(ageInt >= 20 && ageInt <= 29)
			{
				return 2;
			}
			else if(ageInt >= 30 && ageInt <= 39)
			{
				return 3;
			}
			else if(ageInt >= 40 && ageInt <= 49)
			{
				return 4;
			}
			else if(ageInt >= 50 && ageInt <= 59)
			{
				return 5;
			}
		}catch(Exception e){}
		return 0;
	}
	private int parseGender(String string) {
		if(string.equals("M")) return 0;
		if(string.equals("F")) return 1;
		else return -1;
	}
	public void closeAll() throws IOException{
		System.out.println("Close All");
		if(dataInputStream != null) dataInputStream.close();
		if(dataOutputStream != null) dataOutputStream.close();
		if(socket != null) socket.close();
		
	}
}