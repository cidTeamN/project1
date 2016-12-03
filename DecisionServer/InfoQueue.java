import java.util.PriorityQueue;

public class InfoQueue
{
	PriorityQueue<QueueEntity> info;
	int qid;
	int usersex;
	int userrating;
	double[] cat;
	public InfoQueue(int qid_, int usersex_, int userrating_, double[] cat_, int max_len)
	{
		qid = qid_;
		usersex = usersex_;
		userrating = userrating_;
		cat = cat_;
		info = new PriorityQueue<QueueEntity>(max_len);
	}
	public QueueEntity[] get(int i)
	{
		return null;
	}
}