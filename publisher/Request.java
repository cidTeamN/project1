import java.io.IOException;
import java.io.InputStream;


public class Request {
	
	private InputStream input;
	private String uri;
	
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
	}
	
	private String parseUri(String requestString) {
		int index1, index2;
		
		index1 = requestString.indexOf(' ');
		
		System.out.println(index1);
		
		if(index1 != 1) {
			index2 = requestString.indexOf(' ', index1 + 1);
			
			//System.out.println(index2);
			
			if(index2 > index1)
				return requestString.substring(index1 + 1, index2);
		}
		
		return null;
	}
	
	public String getUri() {
		return this.uri;
	}
}
