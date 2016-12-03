import java.util.Comparator;

public class QueueEntityComparator implements Comparator<QueueEntity> {
	@Override
	public int compare(QueueEntity e1, QueueEntity e2)
	{
		Double var1 = e1.var;
		Double var2 = e2.var;
		if(var1 != var2)
			return -var1.compareTo(var2);
		else
		{
			Integer ID1 = e1.ID;
			Integer ID2 = e2.ID;
			return ID1.compareTo(ID2);
		}
	}
}