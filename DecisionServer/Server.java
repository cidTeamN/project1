import java.net.*;
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
	static int MAX_LEN = 10;
	static int NUM_OF_QUEUE = 10;
	static JedisPoolConfig jedisPoolConfig;
	public static void jedisMake(JSONObject data){
		JedisPool pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 8080, 100, "password");
		
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
		jedis.set(keyString, data.toString());
		jedis.expireAt(keyString, Integer.parseInt((String) data.get("season")));
		key = (key + 1) % MAX_AD;
		if(jedis != null){
			jedis.close();
		}
		Integer score = scoring(data);
		info.addInQueueList(key, score);
	}
	private static Integer scoring(JSONObject data)
	{
		Integer score = 0;
		return score;
	}
	public void queueFlush()
	{
		JedisPool pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 8080, 100, "password");
		
		Jedis jedis = pool.getResource();
		for(int i = 0;i<NUM_OF_QUEUE;++i)
		{
			for(int j=0;j<MAX_LEN;++j)
			{
				if(jedis.get(info.get(i,j)) == null)
				{
					info.remove(i, j);
				}
			}
		}
	}
	public static void main(String[] args) throws IOException
	{
		info = new QueueList(MAX_LEN, NUM_OF_QUEUE);
		ServerSocket serverSocket = null;
		Socket socket = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		JSONParser parser = new JSONParser();
		String strRslt = null;
		jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(128);
		try
		{
			serverSocket = new ServerSocket(88);
			
			while(true)
			{
				System.out.println("Waiting...\n");
				socket = serverSocket.accept();
				
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				
				while(true)
				{
					try
					{
						HttpNetwork net = new HttpNetwork("http://127.0.0.1");
						if((strRslt=net.strGetData()) != null)
						{
							// parsing json
							JSONObject data = (JSONObject) parser.parse(strRslt);
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
					catch(Exception e)	// Ŭ���̾�Ʈ ���� ����
					{
						break;
					}
				}
				socket.close();
			}
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			if(serverSocket != null) serverSocket.close();
		}
	}
}