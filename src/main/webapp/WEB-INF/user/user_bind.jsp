<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>完善个人信息</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrapValidator.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrapValidator.js"></script>
</head>
<body>
<div style="padding:300px">
	<h3 align="center">是否完善您在本站的个人信息以方便下次登录？</h3><br>
  <button type="button" class="btn btn-primary btn-lg btn-block" onclick="javascript:location='${pageContext.request.contextPath }/user/toReg.do?id=${loginuser.id}&nickname=${loginuser.nickname}&social_uid=${loginuser.social_uid}'">我要完善信息</button>
  <button type="button" class="btn btn-default btn-lg btn-block" onclick="javascript:location='${pageContext.request.contextPath }/index.jsp'">直接进入</button>
</div>
</body>
</html>