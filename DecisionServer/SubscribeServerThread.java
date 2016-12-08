import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
public class SubscribeServerThread extends Thread{
	private Socket socket;
	DataInputStream dataInputStream;
	static JedisPoolConfig jedisPoolConfig;
	SubscribeServerThread(Socket s){
		socket = s;
		jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(128);
		if(info == null)
		{
			info = new QueueList(MAX_AD);
		}
	}
	public QueueList getQueueList()
	{
		return info;
	}
	public void setQueueList(QueueList info_)
	{
		info = info_;
	}
	static int MAX_AD = 200000;
	private static QueueList info;
	static Integer key = 0;
	static int MAX_LEN = 100;
	static int NUM_OF_QUEUE = 10;
	
	
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
	public static void jedisMake(JSONObject data){
		JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379);
		String keyString = key.toString();
		Jedis jedis = pool.getResource();
				
		int keyFlag = key - 1;
		boolean databaseFull = false;
		while(true)
		{
			if(keyFlag == key)
			{
				databaseFull = true;
				break;
			}
			else if(jedis.get(keyString) == null)
			{
				break;
			}
			else
			{
				key = (key + 1) % MAX_AD;
				keyString = key.toString();
			}
		}
		if(databaseFull)
		{
			System.out.println("Database has no room for AD");
			jedis.close();
			pool.close();
			return;
		}
		info.addInQueueList(key, data);
		jedis.set(keyString, data.toString());
		jedis.expireAt(keyString, toUnixTimeStamp((String) data.get("season")));
		jedis.set(keyString+"|len", (String) data.get("len"));
		jedis.expireAt(keyString+"|len", toUnixTimeStamp((String) data.get("season")));
		
		key = (key + 1) % MAX_AD;
		if(jedis != null){
			jedis.close();
		}
		if(pool != null) pool.close();
	}
	
	public void service() throws IOException{
		dataInputStream = new DataInputStream(socket.getInputStream());
		JSONParser parser = new JSONParser();
		String strRslt = null;
		while(true)
		{
			if((strRslt=dataInputStream.readUTF()) != null)
			{
				System.out.println("read data");
				// parsing json
				try{
					JSONObject data = (JSONObject) parser.parse(strRslt);
					System.out.println("title = " + data.get("title"));
					if(data.get("title") == null || data.get("title").equals(""))
					{
						System.out.println("Null value accepted");
						strRslt = null;
						return;
					}
					else
					{
						System.out.println(data.toString());
						jedisMake(data);
						info.print();
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				// insert to db
				//jedisMake(data);
			}
			else
			{
				System.out.println("Client write end");
				
				break;
			}
		}
	}
	public void closeAll() throws IOException{
		if(dataInputStream != null) dataInputStream.close();
		if(socket != null) socket.close();
	}
}