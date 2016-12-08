import java.util.PriorityQueue;
import org.json.simple.*;

public class QueueList
{
	InfoQueue[] queues;
	int MAX_LEN = 10;
	int NUM_OF_QUEUE = 10;
	int INF = 1000;
	int[] webCatIndex = 
		{ 1, 2, 3, 4, 5 };
	double[][] catIndex =
		{
			{	+0.0,	+0.0,	+0.0,	+0.0	},
			{	-4.5,	-4.0,	+4.5,	-0.5	},
			{	+3.5,	-0.5,	+4.0,	+5.0	},
			{	-2.0,	+5.0,	-0.5,	+3.0	},
			{	+2.5,	+3.5,	+4.5,	+0.5	},
			{	-3.5,	+4.0,	+1.5,	-2.5	},
			{	+2.5,	+1.0,	-1.0,	-2.5	},
			{	+5.0,	+0.0,	+4.5,	+2.5	},
			{	+1.0,	-3.5,	+4.0,	+3.0	},
			{	+4.5,	-5.0,	+5.0,	+4.0	},
			{	+2.5,	-3.5,	+4.0,	-1.5	},
			{	-0.5,	+5.0,	-4.5,	-3.5	},
			{	-3.0,	+3.5,	-4.5,	-1.0	},
			{	+0.5,	+4.0,	+4.5,	+5.0	},
			{	+3.0,	+3.5,	-5.0,	-1.5	},
			{	+0.5,	+1.0	+0.0,	-4.5	},
			{	+3.0,	-5.0,	+3.5,	-1.0	},
			{	+4.0,	-4.0,	-2.0,	-2.5	},
			{	+2.5,	-2.0,	+4.0,	+3.5	},
			{	-3.0,	+3.0,	+4.0,	+3.0	},
			{	+4.5,	-4.0,	+4.5,	-2.5	},
			{	-3.5,	+4.0,	+4.0,	+4.0	},
			{	+2.5,	-0.5,	+4.0,	+4.0	},
			{	+0.0,	-1.5,	+2.5,	-5.0	},
		};
	public QueueList(int max_len)
	{
		MAX_LEN = max_len;
		
		queues = new InfoQueue[NUM_OF_QUEUE];
		queues[0] = new InfoQueue(0, 0, 2, catIndex[7], MAX_LEN);
		queues[1] = new InfoQueue(1, 1, 3, catIndex[6], MAX_LEN);
		queues[2] = new InfoQueue(2, 0, 5, catIndex[11], MAX_LEN);
		queues[3] = new InfoQueue(3, 1, 1, catIndex[18], MAX_LEN);
		queues[4] = new InfoQueue(4, 0, 3, catIndex[2], MAX_LEN);
		queues[5] = new InfoQueue(5, 1, 2, catIndex[16], MAX_LEN);
		queues[6] = new InfoQueue(6, 1, 5, catIndex[1], MAX_LEN);
		queues[7] = new InfoQueue(7, 0, 1, catIndex[9], MAX_LEN);
		queues[8] = new InfoQueue(8, 0, 4, catIndex[17], MAX_LEN);
		queues[9] = new InfoQueue(9, 1, 4, catIndex[5], MAX_LEN);
	}
	public void addInQueueList(int key, JSONObject data)
	{
		
		for(int i=0;i<NUM_OF_QUEUE;++i)
		{
			QueueEntity e = new QueueEntity(key, 0);
			String usersexStr = (String) data.get("gen");
			String userratingStr = (String) data.get("year");
			String catStr = (String) data.get("cat");
			
			double[] usersex = {0, 0};
			double[] userrating = {0, 0, 0, 0, 0};
			double[] cat = {0, 0, 0, 0};
			
			// parsing
			String[] arr1 = usersexStr.split(",");
			for(int j = 0;j<arr1.length;++j)
			{
				if(arr1[j].equals("man"))
				{
					usersex[0] = 1;
				}
				else if(arr1[j].equals("woman"))
				{
					usersex[1] = 1;
				}
			}
			String[] arr2 = userratingStr.split(",");
			for(int j=0;j<arr2.length;++j)
			{
				userrating[Integer.parseInt(arr2[j].substring(arr2[j].length() - 1)) - 1] = 1;
			}
			cat = meanCategory(catStr);
			double result = queues[i].scoring(usersex, userrating, cat);
			e.var = result;
			queues[i].addInQueue(e);
		}
	}
	public double[] meanCategory(String catStr)
	{
		String[] arr = catStr.split(",");
		double[] result = { 0, 0, 0, 0 };
		for(int i = 0;i<arr.length;++i)
		{
			int index = Integer.parseInt(arr[i].substring(arr[i].length() - 1)) - 1;
			for(int j=0;j<4;++j)
			{
				result[j] += catIndex[webCatIndex[index]][j];
			}
		}
		for(int j=0;j<4;++j)
		{
			result[j] /= arr.length;
		}
		return result;
	}
	
	public void deleteFromQueueList(int key)
	{
		for(int i=0;i<NUM_OF_QUEUE;++i)
		{
			queues[i].delete(key);
		}
	}
	public int findNearestQueueID(int usersex, int userrating, double[] category)
	{
		double score = -1;
		int retID = -1;
		for(int i=0;i<NUM_OF_QUEUE;++i)
		{
			// Calculating rule can be changed
			double q_score = queues[i].scoring(usersex, userrating, category);
			if(score < q_score)
			{
				score = q_score;
				retID = i;
			}
		}
		return retID;
	}
	public PriorityQueue<QueueEntity> getQueue(int qid)
	{
		return queues[qid].info;
	}
	public void print()
	{
		for(int i=0; i<NUM_OF_QUEUE; ++i)
		{
			System.out.print("Q"+i+"  :  ");
			queues[i].print();
			System.out.println();
		}
	}
}