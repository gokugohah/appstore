<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
</head>
<body>
<center>
<div style="padding: 100px 100px 10px;">
	<h1>用户登录</h1>
	<form class="bs-example bs-example-form" role="form" action="${pageContext.request.contextPath }/user/login.do" method="post">
		<div class="input-group input-group-lg">
			<span class="input-group-addon">昵称</span>
			<input type="text" name="loginname" class="form-control" placeholder="邮箱/手机号/昵称">
		</div><br>
		<div class="input-group input-group-lg">
			<span class="input-group-addon">密码</span>
			<input type="password" name="password" class="form-control" placeholder="请输入密码">
		</div><br>
		<button type="submit" class="btn btn-primary btn-lg">立即登录</button>
		<button type="button" onclick="javascript:location='${pageContext.request.contextPath }/user/toReg.do'" class="btn btn-primary btn-lg">注册</button>
		<br>
	</form>
</div>
</center>
</body>
</html>