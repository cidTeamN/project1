import java.io.IOException;
import java.io.InputStream;


public class Request {
	
	private InputStream input;
	private String uri;
	private JSON info;
	private String id;
	
	public Request(InputStream input) {
		this.input = input;
	}
	
	public void parse() {
		StringBuffer requestbuffer = new StringBuffer(2048);
		int i;
		byte[] buffer = new byte[2048];
		
		try {
			i = input.read(buffer);
		}
		catch(IOException ie) {
			ie.printStackTrace();
			i = -1;
		}
		
		for(int j = 0; j<i; j++){
			requestbuffer.append((char)buffer[j]);
		}
		
		System.out.println(requestbuffer.toString());
		uri = parseUri(requestbuffer.toString());
		System.out.println("url "+ uri);
		if(uri.contains("%22") && ! (uri == null)) uri = remove(uri);
		if(!(uri.equals("/favicon.ico") || uri.equals("/result.html")))
		{
			info = getJSONInfo(uri);
			uri = removeParam(uri);
		}
	}
	public String getid(){
		return this.id;
	}
	
	public JSON getJSON(){
		return this.info;
	}
	
	private JSON getJSONInfo(String uri2) {
		// TODO Auto-generated method stub
		System.out.println("re2: "+uri2);
		String temp[] = uri2.split("\\?");
		String infoUri =  temp[1];
	
		
		String infoTemp[] = infoUri.split("\\&");
		String id = infoTemp[0].substring(3);
		this.id = id;
		System.out.println("id "+id);
		//gender=male
		String gender = infoTemp[1].substring(7);
		System.out.println("gender "+gender);
		//age=1020
		String age = infoTemp[2].substring(4);
		//cat=game
		String cat = infoTemp[3].substring(5);
		
		JSON info = new JSON(id, gender, age, cat);
		
		return info;
	}
	
	private String remove(String url){
		String valid[] = url.split("/");
		return valid[2];
	}

	private String parseUri(String requestString) {
		int index1, index2;
		
		index1 = requestString.indexOf(' ');
		
		System.out.println(index1);
		
		if(index1 != 1) {
			index2 = requestString.indexOf(' ', index1 + 1);
			
			System.out.println(index2);
			
			if(index2 > index1)
				return requestString.substring(index1 + 1, index2);
		}
		
		return null;
	}
	
	private String removeParam(String uriString){
		System.out.println(uriString);
		String temp[] = uriString.split("\\?");
		return temp[0];
	}
	
	public String getUri() {
		return this.uri;
	}

	public void setUri(String string) {
		// TODO Auto-generated method stub
		this.uri = string;
	}
}
