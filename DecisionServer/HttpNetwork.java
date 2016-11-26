import java.io.*;
import java.net.*;

public class HttpNetwork {
   String strEncodeURL;
   public HttpNetwork(String URL){
      String strHtmlSource = this.strGetData();
      strEncodeURL = URL;
      System.out.println("Receive HTML ("+getCurrentDate24() + ") \n"+strHtmlSource);
   }
   public String strGetData(){
      BufferedReader oBufReader = null;
      HttpURLConnection httpConn = null;
      String strBuffer = "";
      String strRslt = "";
      
      try
      {
         
         URL oOpenURL = new URL(strEncodeURL);
         
         httpConn = (HttpURLConnection) oOpenURL.openConnection();
         httpConn.setRequestMethod("POST");
         httpConn.connect();
         oBufReader = new BufferedReader(new InputStreamReader(oOpenURL.openStream()));
         
         while((strBuffer = oBufReader.readLine()) != null)
         {
            if(strBuffer.length() > 1)
            {
               strRslt += strBuffer;
            }
         }
      }
      catch(Exception e)
      {
         return null;
      }
      
      return strRslt;
   }
   private static String getCurrentDate24()
   {
      java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.KOREA);
      return formatter.format(new java.util.Date());
   }
}