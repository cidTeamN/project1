import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SocketThread extends Thread{
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	JedisPool pool;
	Jedis jedis;
	public SocketThread(Socket s)
	{
		socket = s;
	}
	@Override
	public void run(){
		try{
			service();
		}catch(Exception e)
		{
		}finally{
			try{
				closeAll();
			}catch(Exception e)
			{
			}
		}
	}

	private void closeAll() throws IOException {
		// TODO Auto-generated method stub
		if(socket!=null) socket.close();
		if(in!=null) in.close();
		if(out!=null) out.close();
		if(jedis!=null) jedis.close();
		if(pool!=null) pool.close();
	}

	private void service() throws IOException {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		pool = new JedisPool(jedisPoolConfig, "localhost", 6379);
		jedis = pool.getResource();
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		String strRslt = null;
		String strOut = null;
		while(true)
		{
			if((strRslt=in.readUTF()) != null)
			{
				if(!strRslt.contains("@"))
				{
					strOut = jedis.get(strRslt);
					out.writeUTF(strOut);
					out.flush();
					out.close();
					in.close();
				}
				else
				{
					String[] arr = strRslt.split("@");
					jedis.set(arr[0], arr[1]);
					in.close();
				}
			}
		}
	}
}