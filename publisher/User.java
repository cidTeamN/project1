import java.util.ArrayList;
import java.util.Random;
import java.lang.StringBuffer;

public class User{
	public static void main(String args[]){
		int num = Integer.parseInt(args[0]);
		ArrayList<String> user_sex = new ArrayList<String>(2);
		ArrayList<String> userrating = new ArrayList<String>(5);
		ArrayList<String> cat = new ArrayList<String>(10);

		user_sex.add("male");
		user_sex.add("female");
		userrating.add("10");
		userrating.add("20");
		userrating.add("30");
		userrating.add("40");
		userrating.add("50");
		cat.add("health");
		cat.add("child");
		cat.add("politics");
		cat.add("fashion");
		cat.add("pet");
		cat.add("drama");
		cat.add("game");
		cat.add("leisure");
		cat.add("education");
		cat.add("health");

		Random random = new Random();

		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i = 0 ; i < num ; i++){
			int ran_ip = random.nextInt();

			String ran_sex = user_sex.get(random.nextInt(9999) % 2);
			String ran_rating = userrating.get(random.nextInt(9999) % 5);
			String ran_cat = cat.get(random.nextInt(9999) % 10);


			sb.append("{");
			sb.append("\"type\": \"request\",");
			sb.append("\"ip\": \"" + ran_ip + "\",");
			sb.append("\"user_sex\": \"" + ran_sex + "\",");
			sb.append("\"userrating\": \"" + ran_rating + "\",");
			sb.append("\"cat\": \"" + ran_cat + "\"");
			sb.append("}");
			if(i != num-1) sb.append(", ");		
		}

		sb.append("]");

		System.out.println(sb.toString());


	}
}