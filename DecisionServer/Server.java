import java.io.*;

public class Server
{
	public static void main(String[] args) throws IOException
	{
		/*Comparator<QueueEntity> QueueEntityComparator = new QueueEntityComparator();
		PriorityQueue<QueueEntity> testQueue = new PriorityQueue<QueueEntity>(20000, QueueEntityComparator);
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
		*/
		
		PortThread pt1 = new PortThread(8765);	// request server connection
		pt1.start();
		PortThread pt2 = new PortThread(5678);	// bidder server connection
		pt2.start();
		
	}
}