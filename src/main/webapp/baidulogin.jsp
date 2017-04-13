<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    </head>
    <body>
    <H1>欢迎光临</H1>
    <div>
    请您使用第三方平台账号登陆：
    <span id="login"></span>
    </div> 
    <script type="text/javascript" id="bd_soc_login_boot"></script>
    <script type="text/javascript">
    (function(){
        var t = new Date().getTime(),
            script = document.getElementById("bd_soc_login_boot"),
            redirect_uri = encodeURIComponent("http://127.0.0.1/appstore/user/userLogin.do"),
            domid = "login", 
            src = "http://openapi.baidu.com/social/oauth/2.0/connect/login?redirect_uri=" + redirect_uri + "&domid=" + domid + "&client_type=web&response_type=code&media_types=sinaweibo%2Cqqdenglu%2Cbaidu%2Cqqweibo%2Ckaixin&size=-1&button_type=3&client_id=m6fO55eQNQaU4MmSR8xOSqj7&view=embedded&t=" + t;
        script.src = src;
    })();
    </script>
    </body>
</html>
