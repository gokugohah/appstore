<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品已添加到购物车</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
</head>
<body>
<div style="padding:300px">
<h1 align="center">商品已添加到购物车！</h1>
<button type="button" class="btn btn-primary btn-lg btn-block" onclick="javascript:history.back()">返回继续购买</button>
<button type="button" class="btn btn-default btn-lg btn-block" onclick="javascript:location='${pageContext.request.contextPath }/cart/toCart.do'">进入购物车结算</button>
</div>
</body>
</html>