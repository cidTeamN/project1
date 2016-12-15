import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.lang.StringBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

public class User{
	public static void main(String args[]) throws IOException{
		int num = Integer.parseInt(args[0]);
		ArrayList<String> user_sex = new ArrayList<String>(2);
		ArrayList<String> userrating = new ArrayList<String>(5);
		ArrayList<String> cat = new ArrayList<String>(10);
		ArrayList<String> urlList = new ArrayList<String>(num);

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
	//	sb.append("[");
		for(int i = 0 ; i < num ; i++){
			int ran_ip = random.nextInt(99999);

			String ran_sex = user_sex.get(random.nextInt(9999) % 2);
			String ran_rating = userrating.get(random.nextInt(9999) % 5);
			String ran_cat = cat.get(random.nextInt(9999) % 10);
			int ran_cat_id = (random.nextInt(9999) % 23) + 1;
			
			sb.append("?id="+ran_ip);
			sb.append("&gender="+ran_sex);
			sb.append("&age="+ran_rating);
			sb.append("&cat="+ran_cat_id);
			
			String urlParam = sb.toString();
			urlList.add(urlParam);
			sb.setLength(0);
		}

		
			String file = "webroot3/total.html";
			String newFile = "webroot3/result.html";
			

			
			File htmlTemplateFile = new File(file);
			String htmlString = FileUtils.readFileToString(htmlTemplateFile, Charset.forName("UTF-8"));
			
			for(int i = 0 ; i < num ; i++){
				htmlString = htmlString.replaceAll("%url"+(i+1), urlList.get(i));
			}
			
			
			File newHtmlFile = new File(newFile);
			FileUtils.writeStringToFile(newHtmlFile, htmlString,StandardCharsets.UTF_8);
			
			
			

/*
			sb.append("{");
			sb.append("\"type\": \"request\",");
			sb.append("\"ip\": \"" + ran_ip + "\",");
			sb.append("\"user_sex\": \"" + ran_sex + "\",");
			sb.append("\"userrating\": \"" + ran_rating + "\",");
			sb.append("\"cat\": \"" + ran_cat + "\"");
			sb.append("}");
			if(i != num-1) sb.append(", ");		
			*/
		}

		//sb.append("]");

	//	System.out.println(sb.toString());



}
