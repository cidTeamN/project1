import java.util.ArrayList;

public class MakeUser{
	public static void main(Sring args[]){
		int num = args[1];
		ArrayList<String> user_sex = new ArrayList(2);
		ArrayList<String> userrating = new ArrayList(5);
		ArrayList<String> cat = new ArrayList(10);

		user_sex.insert("male");
		user_sex.insert("female");
		userrating.insert("10");
		userrating.insert("20");
		userrating.insert("30");
		userrating.insert("40");
		userrating.insert("50");
		cat.insert("health");
		cat.insert("child");
		cat.insert("politics");
		cat.insert("fashion");
		cat.insert("pet");
		cat.insert("drama");
		cat.insert("game");
		cat.insert("leisure");
		cat.insert("education");
		cat.insert("health");

		Random random = new Random();

		int ran_ip = random.nextInt();
		String ran_sex = user_sex.get(random.nextInt()%2);
		String ran_rating = userrating.get(random.nextInt()%5);
		String ran_cat = cat.get(random.nextInt()%10);

		String sb = new StringBuffer();
		sb.append("[");
		for(int i = 0 ; i < num ; i++){
			sb.append("{");
			sb.append("\"type\": \"request\",");
			sb.append("\"ip\": \"" + ran_ip + "\",");
			sb.append("\"user_sex\": \"" + ran_sex + "\",");
			sb.append("\"userrating\": \"" + ran_rating + "\",");
			sb.append("\"cat\": \"" + ran_cat + "\"");
			sb.append("}");
			if(i != n-1) sb.append(", ");		
		}

		sb.append("]");

		System.out.println(sb);


	}
}