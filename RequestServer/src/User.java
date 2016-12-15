import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.lang.StringBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

public class User{
	public static void main(String args[]) throws IOException, InterruptedException{
		String[] catIndex = 
			{
					"예술/연예",
					"자동차",
					"사업/사무",
					"직업/학업",
					"교육",
					"가정/육아",
					"건강/운동",
					"식문화",
					"취미생활",
					"인테리어/원예",
					"정치/법",
					"뉴스",
					"경제",
					"사회",
					"과학",
					"애완동물",
					"스포츠",
					"패션",
					"기술/컴퓨팅",
					"여행",
					"부동산",
					"쇼핑",
					"종교",
			};
		while(true){
		int num = 5;
		ArrayList<String> user_sex = new ArrayList<String>(2);
		ArrayList<String> userrating = new ArrayList<String>(5);
		ArrayList<String> cat = new ArrayList<String>(10);
		ArrayList<String> urlList = new ArrayList<String>(num);
		ArrayList<String> userList = new ArrayList<String>(num);

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
		StringBuffer sbResult = new StringBuffer();
	//	sb.append("[");
		for(int i = 0 ; i < num ; i++){
			int ran_ip = random.nextInt(99999);

			String ran_sex = user_sex.get(random.nextInt(9999) % 2);
			String ran_rating = userrating.get(random.nextInt(9999) % 5);
		//	String ran_cat = cat.get(random.nextInt(9999) % 10);
			int ran_cat_id = (random.nextInt(9999) % 23) + 1;
			
			sb.append("?id="+ran_ip);
			sb.append("&gender="+ran_sex);
			sbResult.append("gender: "+ran_sex);
			sb.append("&age="+ran_rating);
			sbResult.append(" age: "+ran_rating);
			sb.append("&cat="+ran_cat_id);
			sbResult.append(" cat: "+ catIndex[ran_cat_id]);
			
			String urlParam = sb.toString();
			String userData = sbResult.toString();
			userList.add(userData);
			urlList.add(urlParam);
			sbResult.setLength(0);
			sb.setLength(0);
		}

		
			String file = "webroot3/total.html";
			String newFile = "webroot3/result.html";
			

			
			File htmlTemplateFile = new File(file);
			String htmlString = FileUtils.readFileToString(htmlTemplateFile, Charset.forName("UTF-8"));
			
			for(int i = 0 ; i < num ; i++){
				htmlString = htmlString.replace("%user"+ (i+1), userList.get(i));
				htmlString = htmlString.replace("%url"+(i+1), urlList.get(i));
			}
			
			
			File newHtmlFile = new File(newFile);
			FileUtils.writeStringToFile(newHtmlFile, htmlString,StandardCharsets.UTF_8);
			Thread.sleep(3000);
		}
			
	}


}
