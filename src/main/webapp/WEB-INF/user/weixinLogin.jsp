<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信扫一扫登录</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
</head>
<body>
<script type="text/javascript">
	var uuid = '${uuid}';
	var count = 0;
	function lunxun(){
		$.post(
			"${pageContext.request.contextPath }/user/WeixinLogin.do",
			{uuid:uuid},
			function(msg){
				count++;
				if(msg){
					clearInterval(interval);
					location = "${pageContext.request.contextPath }/index.jsp";
				}
			}
		);
		if(count>20){
			clearInterval(interval);
			$("img").attr("src","/filepath/temp/loading.gif");
			$("img").after("<h4>二维码已过期，请<a href='javascript:void(0)' onclick='location.reload()'>刷新</a>页面！</h4>");
		}
	}
	var interval = setInterval("lunxun()", 3000);
</script>
<div style="padding:100px 300px;text-align: center;">
	<h3 align="center">请使用微信手机端扫描下面的二维码登录</h3><br>
	<img alt="微信扫一扫登录" src="/filepath/${uuid}.jpg" width="300" height="300"><br>
</div>
</body>
</html>