
public class JSON {
	private String userSexString;
	private String userRatingString;
	private String catString;
	private String id;
	private String price;
	private String url;
	
	public static void main(String[] args){
		
	}
	
	public JSON(String id, String gender, String rating, String cat){
		this.userSexString = gender;
		this.userRatingString = rating;
		this.catString = cat;
		this.id = id;
	}
	
	
	public void parse(String input){
		String[] split = input.split("&");
	//	this.id = split[0].substring();
		
	}
	
	public String toString(){
		StringBuffer print = new StringBuffer("{");
		print.append("\"type\": \"request\",");
		print.append("\"id\": \""+id+"\",");
		print.append("\"usersex\": \""+userSexString+"\",");
		print.append("\"userrating\": \""+userRatingString+"\",");
		print.append("\"cat\": \""+catString+"\"");
		print.append("}");
		
		return print.toString();
	}

}
