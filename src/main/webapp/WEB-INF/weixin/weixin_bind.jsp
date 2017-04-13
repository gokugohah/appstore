<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>绑定界面</title>
</head>
<style>
	.ui-font-gray{color:#999; font-size:12px;}
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery_mobile/jquery.mobile-1.4.5.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery_mobile/jquery.mobile-1.4.5.js"></script>
</head>
<body> 

<div id="user" data-role="page">

	<div data-role="header">
    	<a href="" data-role="button" data-icon="home">返回首页</a>
		<h1>新用户绑定</h1>
        <a href="" data-role="button" data-icon="arrow-r">快速注册</a>
	</div>

	<div data-role="content">	
		<form>
        	<ul data-role="listview" data-inset="true">
                <li data-role="fieldcontain">
                    <label for="nickname">昵称:</label>
                    <input type="text" name="nickname" id="nickname" value="${user.nickname}"  />
                </li>
                <li data-role="fieldcontain">
                    <label for="loginName">账号:</label>
                    <input type="text" name="name" id="loginName" value="${user.openid}"  />
                </li>
                <li data-role="fieldcontain">
                    <label for="loginPwd">密码:</label>
                    <input type="password" name="password" id="loginPwd" value=""  />
                </li>
                <li data-role="fieldcontain">
                    <label for="phone">电话:</label>
                    <input type="text" name="phone" id="phone" value=""  />
                </li>
                <li data-role="fieldcontain">
                    <input type="checkbox" name="autologin" id="loginAuto" checked  />
                    <label for="loginAuto">自动登录</label>
                </li>
                <li data-role="fieldcontain">
                    <input type="submit" value="绑定" data-theme="b" />
                </li>
            </ul>
                    <a href="#myPopup" data-rel="popup" data-position-to="window">
                   	 <img src="${user.headimgurl}" style="width:30%;height:30%;" />
                    </a>
        </form>	
	</div>
	
	<div data-role="popup" id="myPopup" data-overlay-theme="b">
	  <img src="${user.headimgurl}">
	</div>
	
	<div data-role="footer">
		<h4>MADE IN LILI</h4>
	</div>
</div>

</body>
</html>