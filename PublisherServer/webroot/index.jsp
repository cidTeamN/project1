<%@ page contentType="text/html;charset=EUC-KR"%>
<html>
  <body onload="login.id.focus()">
  
  <form name="login" action="loginProc.jsp" method="post">
    
      아이디 : <input type="text" name="id" size="8" maxlength="8"/><p>
      비밀번호 : <input type="password" name="pwd" size="8" maxlength="8"/><p>
      <input type="submit" value="로그인"/>
      <input type="reset" value="취소"/>
  </form>
  
  </body>
</html>