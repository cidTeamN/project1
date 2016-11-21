import java.net.*;
import java.io.*;

    public class MessageClient {
        public static void main(String args[]) {
            Socket clientSocket = null;
            String recieve;
            String filename = args[1];
            
            String text = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);


            String user_info = reader.re
            try {
                clientSocket = new Socket("localhost", 8080);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));


                out.println(text);
/*
                out.println("GET /index.html HTTP/1.0\n");
                out.flush();
*/
                System.out.println("Response : "+in.readLine());

            } catch (Exception e) {
                System.out.println("deal with it...");
                e.printStackTrace();
            }


        }
    }