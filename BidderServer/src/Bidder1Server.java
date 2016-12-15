import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Bidder1Server{
	
	public static void main(String[] args) throws IOException
	{
		
		Command c = new Command();
		c.start();
		SendPrice t = new SendPrice();
		t.start();
		ReceivePriceThread rpt = new ReceivePriceThread();
		rpt.start();
		
		File file = new File("./bidder_list.txt");
		if(file.isFile()){
			BufferedReader br = null;
			try{
				br = new BufferedReader(new FileReader(file));
				String str = null;
				while((str = br.readLine())!=null){
					String[] arr = str.split(",");
					Command.adList.add(arr);
				}
			}catch (Exception e){}
		}
	}
}