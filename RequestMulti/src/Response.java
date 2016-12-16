import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;


public class Response {

	private static final int BUFFER_SIZE = 99999999;
	Request request;
	OutputStream output;
	
	public Response(OutputStream output) {
		this.output = output;
	}
	
	public void setRequest(Request request) {
		this.request = request;
	}
	public void sendResponse() throws IOException{
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.1 200 OK\r\n");
        sb.append("Content-Type: text/html\r\n\r\n");
        sb.append("<html><head></head><body><h1>Hello</h1></body></html>");
		output.write(sb.toString().getBytes(Charset.forName("UTF-8")));
		

	}
	
	public void sendStaticResource() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			File file = new File(Server.WEB_ROOT, request.getUri());
			if(file.exists()) {
				fis = new FileInputStream(file);
		//		System.out.println("name: "+file.getName());
				int ch = fis.read(bytes, 0, BUFFER_SIZE);
				while(ch!=-1) {
		//			System.out.println("ch1 : " + ch);
		//			System.out.println(bytes.toString());
					output.write(bytes, 0, ch);
					ch = fis.read(bytes, 0, BUFFER_SIZE);
			//		System.out.println("ch2 : " + ch);
				}
			}
			else {
				String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + 
									  "Content-Type: text/html\r\n" +
									  "Content-Length: 23\r\n" +
									  "\r\n" +
									  "<h1>File Not Found</h1>";
				output.write(errorMessage.getBytes());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(fis!=null)
				fis.close();
		}
	}

}
