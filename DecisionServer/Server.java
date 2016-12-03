import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;
import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.*;
import org.apache.commons.*;

import redis.clients.jedis.*;
import redis.clients.util.*;

public class Server
{
	static int MAX_AD = 200000;
	static QueueList info;
	static Integer key = 0;
	static int MAX_LEN = 100;
	static int NUM_OF_QUEUE = 10;
	static JedisPoolConfig jedisPoolConfig;
	
	public static void jedisMake(JSONObject data){
		JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379);
		String keyString = key.toString();
		Jedis jedis = pool.getResource();
		
		if(data == null)
		{
			jedis.set("NULL", "TEST");
			jedis.close();
			pool.close();
			return;
		}
		
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
		jedis.set(keyString, data.toString());
		jedis.expireAt(keyString, toUnixTimeStamp((String) data.get("season")));
		info.addInQueueList(key, data);
		
		key = (key + 1) % MAX_AD;
		if(jedis != null){
			jedis.close();
		}
		
		pool.close();
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
	
	public static QueueEntity rating(QueueEntity e)
	{
		JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379);
		String keyString = ((Integer) e.ID).toString();
		Jedis jedis = pool.getResource();
		JSONParser parser = new JSONParser();
		JSONObject data = null;
		try{
			data = (JSONObject) parser.parse(jedis.get(keyString));
		}catch(Exception error){ e = null; }
		
		// compute priority with urgency, len
		
		jedis.close();
		pool.close();
		return e;
	}
	
	public static int getAdID(int usersex, int userrating, double[] cat)
	{
		int AdID = -1;	// AdID = -1 -> ad for RTB system
		int qid = info.findNearestQueueID(usersex, userrating, cat);
		PriorityQueue<QueueEntity> selectedQueue = info.getQueue(qid);
		PriorityQueue<QueueEntity> calculatedQueue = new PriorityQueue<QueueEntity>();
		QueueEntity e = null;
		while((e = selectedQueue.poll()) != null)
		{
			QueueEntity rated = rating(e);
			if(rated == null)
			{
				info.deleteFromQueueList(e.ID);
				continue;
			}
			else
			{
				calculatedQueue.add(rated);
			}
		}
		
		// send bidding request to bid server for 10% elements in calculatedQueue
		
		return AdID;
	}
	public static void main(String[] args) throws IOException
	{
		Comparator<QueueEntity> QueueEntityComparator = new QueueEntityComparator();
		PriorityQueue<QueueEntity> testQueue = new PriorityQueue<QueueEntity>(MAX_LEN, QueueEntityComparator);
		//System.out.println(testQueue.comparator());
		QueueEntity e1 = new QueueEntity(2, 1);
		QueueEntity e2 = new QueueEntity(3, 3);
		QueueEntity e3 = new QueueEntity(1, 2);
		testQueue.add(e1);
		testQueue.add(e2);
		testQueue.add(e3);
		System.out.println("Check1");
		System.out.print(testQueue.poll().ID);
		System.out.print(testQueue.poll().ID);
		System.out.print(testQueue.poll().ID);
		System.out.println(testQueue.poll());
		String x = "abc";
		String[] arr = x.split(":");
		System.out.println(arr[0]);
		System.out.println(arr.length);
		info = new QueueList(MAX_LEN);
		ServerSocket serverSocket = null;
		Socket socket = null;
		JSONParser parser = new JSONParser();
		String strRslt = null;
		jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(128);
		//jedisMake(null);
		try
		{
			while(true)
			{
				serverSocket = new ServerSocket(8765);
				while(true)
				{
					System.out.println("Waiting...\n");
					socket = serverSocket.accept();
					System.out.println("Server socket accepted");
					//BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					socket.setSoTimeout(3000);
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
					//dataOutputStream = new DataOutputStream(socket.getOutputStream());
					
					try
					{
						System.out.println("try to read input stream");
						//if(socket.getInputStream() == null) System.out.println("Cannot get inputstream");
						
						//HttpNetwork net = new HttpNetwork("http://192.168.0.9");
						
						if((strRslt=dataInputStream.readUTF()) != null)
						{
							System.out.println("read data");
							// parsing json
							JSONObject data = (JSONObject) parser.parse(strRslt);
							System.out.println(data.toString());
							// insert to db
							jedisMake(data);
						}
						/*for(int i = 1; i<=10;++i)
						{
							dataOutputStream.writeInt(i);
						}
						System.out.printf("Server:received  %s\n", dataInputStream.readUTF());
						*/
					}
					catch(Exception e)	// 
					{
						System.out.println("client disconnected");
						break;
					}
				}
				if(socket != null) socket.close();
				if(serverSocket != null) serverSocket.close();
			}
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			if(socket != null) socket.close();
		}
	}
}