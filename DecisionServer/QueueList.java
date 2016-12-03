public class QueueList
{
	InfoQueue[] queues;
	int MAX_LEN = 10;
	int NUM_OF_QUEUE = 10;
	public QueueList()
	{
		double[][] cat =
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
		queues = new InfoQueue[NUM_OF_QUEUE];
		queues[0] = new InfoQueue(0, 0, 2, cat[7], MAX_LEN);
		queues[1] = new InfoQueue(1, 1, 3, cat[6], MAX_LEN);
		queues[2] = new InfoQueue(2, 0, 5, cat[11], MAX_LEN);
		queues[3] = new InfoQueue(3, 1, 1, cat[18], MAX_LEN);
		queues[4] = new InfoQueue(4, 0, 3, cat[2], MAX_LEN);
		queues[5] = new InfoQueue(5, 1, 2, cat[16], MAX_LEN);
		queues[6] = new InfoQueue(6, 1, 5, cat[1], MAX_LEN);
		queues[7] = new InfoQueue(7, 0, 1, cat[9], MAX_LEN);
		queues[8] = new InfoQueue(8, 0, 4, cat[17], MAX_LEN);
		queues[9] = new InfoQueue(9, 1, 4, cat[5], MAX_LEN);
	}
	public void addInQueueList(Integer ID, Integer var)
	{
		
	}
	public String get(int i, int j)
	{
		return null;
		//return queues[i].get(j);
	}
}