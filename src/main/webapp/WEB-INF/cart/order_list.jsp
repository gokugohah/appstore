<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>确认订单</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/Cookies.js"></script>
<script type="text/javascript">
	var addtime = '${orders.addtime}';
	var didi = setInterval(outdate, 100);
	function outdate(){
		var r = new Date(addtime).getTime() + 30*60*1000 - new Date().getTime();
		var hour = Math.floor(r/1000/60/60%24);
		var minute = Math.floor(r/1000/60%60);
		var second = Math.floor(r/1000%60);
		if(r>0){
			$("#outhour").text(geshi(hour));
			$("#outminute").text(geshi(minute));
			$("#outsecond").text(geshi(second));
		}else{
			clearInterval(didi);
			$(".orderstatus").text("订单失效");
			$("#showMoney").attr("disabled",true);
		}
	}
	
	function geshi(i){
		if(i<10){
			i = "0" + i ;
		}
		return i ;
	}
</script>
</head>
<body>
<div style="padding:0 300px">
<h1 style="text-align: center;font-size: 30px;">确认订单页面</h1>
<table class="table table-hover">
<thead>
	<tr align="center">
		<td>预览</td>
		<td>商品名称</td>
		<td>单价</td>
		<td>数量</td>
		<td>总价</td>
		<td>订单状态</td>
	</tr>
</thead>
<tbody>
<c:forEach items="${orders.itemlist}" var="item">
	<tr id="${item.appid}" align="center">
		<td><img style="height:100px;" src="/filepath/icon/${item.appid}_icon.jpg" /></td>
		<td>${item.appname}</td>
		<td>${item.price}</td>
		<td>${item.appnum }</td>
		<td>${item.price * item.appnum}元</td>
		<td>
			<span class="orderstatus">
				<c:if test="${orders.status==0 }">未支付</c:if>
				<c:if test="${orders.status==1 }">支付成功</c:if>
				<c:if test="${orders.status==2 }">支付失败</c:if>
				<c:if test="${orders.status==3 }">订单失效</c:if>
			</span><br>
		</td>
	</tr>
</c:forEach>
	<tr>
		<td colspan="11" align="right">
			<strong style="font-size: 20px;">合计：${orders.totalprice}元</strong>
		</td>
	</tr>
	<tr>
		<td colspan="11" valign="middle" >
			<span>收货人：${loginuser.nickname}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			收货地址：${orders.address}<span>
		</td>
	</tr>
	<tr>
		<td colspan="11" align="right">
			<span style="margin-right: 50px;font-weight: bold;">请在<span id="outhour" style="color: red;">00</span>时<span id="outminute" style="color: red;">00</span>分<span id="outsecond" style="color: red;">00</span>秒内完成付款</span>
			<button id="showMoney" type="button" onclick="location='${pageContext.request.contextPath }/pay/toBuy.do?id=${orders.id}'" class="btn btn-danger" ${orders.status==3?"disabled":"" }>支付</button>
		</td>
	</tr>
</table>
</div>
</body>
</html>