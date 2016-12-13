<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Input Check</title>
<script src="//code.jquery.com/jquery.js"></script>
</head>
<body>
<% 
JSONObject Info = new JSONObject();
request.setCharacterEncoding("UTF-8");
String name = request.getParameter("name");
String title = request.getParameter("title");
String[] tmpgen = request.getParameterValues("gen");
String[] tmpcat = request.getParameterValues("cat");
String[] tmpyear = request.getParameterValues("year");
String len = request.getParameter("len");
String season = request.getParameter("season");
String upload = request.getParameter("upload");
String gen = null;
String cat = null;
String year = null;

Info.put("name",name);
Info.put("title",title);
if(tmpgen != null) {
	for(int i=0; i<tmpgen.length; i++) {
		if(gen == null)
			gen = tmpgen[i];
		else gen += ","+tmpgen[i];
	}
}
else {
	gen = "man,woman";
}
Info.put("gen",gen);
if(tmpcat != null) {
	for(int i=0; i<tmpcat.length; i++) {
		if(cat == null)
			cat = tmpcat[i];
		else cat += ","+tmpcat[i];
	}
}
else {
	cat = "cat1,cat2,cat3,cat4,cat5,cat6,cat7,cat8,cat9,cat10,cat11,cat12,cat13,cat14,cat15,cat16,cat17,cat18,cat19,cat20,cat21,cat22,cat23";
}
Info.put("cat",cat);
if(tmpyear != null) {
	for(int i=0; i<tmpyear.length; i++) {
		if(year == null)
			year = tmpyear[i];
		else year += ","+tmpyear[i];
	}
}
else {
	year = "year1,year2,year3,year4,year5";
}
Info.put("year",year);
Info.put("len",len);
Info.put("season",season);
Info.put("upload",upload);
out.print(Info);

String sResult = "";
String sTargetUrl = "cidteamn.ddns.net";
Socket socket = new Socket(sTargetUrl, 8888);
System.out.println("Connected to host");
try {
	DataOutputStream output = null;
	output = new DataOutputStream(socket.getOutputStream());
	output.writeUTF(Info.toString());
	output.flush();
	output.close();
	socket.close();
	System.out.println("Disconnected from host");
} catch (IOException e) {
	System.out.println("It can't");
}

%>
<br/>
이름 : <%=name%><br />
제목 : <%=title%><br />
타겟 :
<%if(tmpgen != null) { %>
<%for(int i=0;i<tmpgen.length;i++){ %>
<%=tmpgen[i] %>
<%} %>
<%} %>
<%if(tmpgen == null) { %>
<%=gen %>
<%} %>

<%if(tmpcat != null) { %>
<%for(int i=0;i<tmpcat.length;i++){
%>
<%=tmpcat[i] %>
<%} %>
<%} %>
<%if(tmpcat == null) { %>
<%=cat %>
<%} %>

<%if(tmpyear != null) { %>
<%for(int i=0;i<tmpyear.length;i++){
%>
<%=tmpyear[i] %>
<%} %>
<%} %>
<%if(tmpyear == null) { %>
<%=gen %>
<%} %>
<br/>
횟수 : <%=len%><br />
기간 : <%=season%><br />
파일 : <%=upload%><br />
    <hr color= "red" />
</body>
</html>