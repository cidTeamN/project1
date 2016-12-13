import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class InfoQueue
{
	double MIN_VAR = 20;
	double MAX_SCORE = 10;
	double CAT_RATE = 0.5;
	PriorityQueue<QueueEntity> info;
	int qid;
	double[] usersex;
	double[] userrating;
	double[] cat;
	public InfoQueue(int qid_, int usersex_, int userrating_, double[] cat_, int max_len)
	{
		qid = qid_;
		usersex = new double[2];
		for(int i=0;i<2;++i)
		{
			if(usersex_ == i)
				usersex[i] = 1;
			else
				usersex[i] = 0;
		}
		userrating = new double[5];
		for(int i=0;i<5;++i)
		{
			if(userrating_ == i + 1)
				userrating[i] = 1;
			else
				userrating[i] = 0;
		}
		cat = cat_;
		Comparator<QueueEntity> QueueEntityComparator = new QueueEntityComparator();
		info = new PriorityQueue<QueueEntity>(max_len, QueueEntityComparator);
	}
	public void addInQueue(QueueEntity e)
	{
		info.add(e);
	}
	public void delete(int key)
	{
		Iterator<QueueEntity> iter = info.iterator();
		while(iter.hasNext())
		{
			QueueEntity it = iter.next();
			if(it.ID == key)
			{
				info.remove(it);
				break;
			}
		}
	}
	// TODO calculating rule can be changed
	public double scoring(int[] usersex_, int[] userrating_, double[] cat_)
	{
		double genderScore = 0;
		
		for(int i=0;i<2;++i)
		{
			if(usersex_[i] == usersex[i])
				genderScore = genderScore + 1;
			else
				continue;
		}
		genderScore = genderScore / 2;
		double total = 0;
		double ageScore = 0;
		for(int i=0;i<5;++i)
		{
			total += userrating[i];
			if(userrating_[i]==1 && userrating[i]==1)
				ageScore += 1;
			else
				continue;
		}
		ageScore = ageScore / total;

		double catScore = catDistance(cat_);
		catScore = (MIN_VAR)/(catDistance(cat_)+MIN_VAR);
		double finalScore = (genderScore + ageScore + catScore) / 3;
		return finalScore;
	}
	// TODO scoring functions should be changed identically
	public double scoring(int usersex_, int userrating_, double[] cat_)
	{
		double genderScore = 0;
		double genderTotal = 0;
		for(int i=0;i<2;++i)
		{
			genderTotal += usersex[i];
			if((i == usersex_) && (1 == usersex[i]))
				genderScore = genderScore + 1;
			else
				continue;
		}
		genderScore = genderScore / genderTotal;
		double ageScore = 0;
		double ageTotal = 0;
		for(int i=0;i<5;++i)
		{
			ageTotal += userrating[i];
			if((i == userrating_-1) && (1 == userrating[i]))
				ageScore += 1;
			else
				continue;
		}
		ageScore = ageScore / ageTotal;
		double catScore = catDistance(cat_);
		catScore = (MIN_VAR)/(catDistance(cat_)+MIN_VAR);
		double finalScore = (genderScore + ageScore + catScore) / 3;
		return finalScore;
	}
	
	public double catDistance(double[] cat_)
	{
		// Category reflection rate can be changed
		double dist = 0;
		for(int i=0;i<4;++i)
		{
			dist += (cat[0]-cat_[0]) * (cat[0]-cat_[0]);
		}
		return dist;
	}
	public void print()
	{
		Iterator<QueueEntity> iter = info.iterator();
		while(iter.hasNext())
		{
			QueueEntity it = iter.next();
			System.out.print(it.ID +":"+ String.format("%.2f", it.var)+'\t');
		}
	}
	
}