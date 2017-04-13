<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes">
<title>我的订单</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath }/bootstrap/bootstrap.css"
	type="text/css"></link>
<script type="text/javascript"
	src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath }/js/Cookies.js"></script>
</head>
<body>
	<div style="width:80%;margin-left: auto;margin-right: auto;">
		<script language="javascript" type="text/javascript">
			var interval = 1000;
			function ShowCountDown(year, month, day, h, m, s, divname) {
				var now = new Date();
				var endDate = new Date(year, month - 1, day + 1, h, m, s + 2);
				var leftTime = endDate.getTime() - now.getTime();
				if (leftTime > 0) {
					var leftsecond = parseInt(leftTime / 1000);
					//var day1=parseInt(leftsecond/(24*60*60*6)); 
					var day1 = Math.floor(leftsecond / (60 * 60 * 24));
					var hour = Math
							.floor((leftsecond - day1 * 24 * 60 * 60) / 3600);
					var minute = Math
							.floor((leftsecond - day1 * 24 * 60 * 60 - hour * 3600) / 60);
					var second = Math.floor(leftsecond - day1 * 24 * 60 * 60
							- hour * 3600 - minute * 60);
					var cc = document.getElementsByName(divname);
					//cc.innerHTML = "脚本之家提示距离"+year+"年"+month+"月"+day+"日"+h+"时"+m+"分"+s+"秒&nbsp;还有："+day1+"天"+hour+"小时"+minute+"分"+second+"秒"; 
					cc.innerHTML = "请在<font color='red'>" + day1
							+ "</font>天<font color='red'>" + hour
							+ "</font>小时<font color='red'>" + minute
							+ "</font>分<font color='red'>" + second
							+ "</font>秒内完成支付";

					if (day1 == 0 && hour == 0 && minute == 0 && second == 0) {
						window.clearInterval(a);
						location.reload();
						//location="${pageContext.request.contextPath }/shopCart/toOrderInfo.do";
					}
				}
			}

			var t = $("[name='time']").val();
			var time1 = t.substring(0, 11).split("-");
			var time2 = t.substring(11).split(":");

			var a = window.setInterval(function() {
				ShowCountDown(time1[0], time1[1], eval(time1[2]), time2[0],
						eval(time2[1]), eval(time2[2]), 'time');
			}, interval);
		</script>
		<table class="table table-hover">
			<br>
			<br>
			<p style="text-align: center; font-size: 30px">我的订单</p>
			<br>
			<c:forEach items="${orders}" var="orders">
				<tr>
					<td colspan="10">订单日期:${orders.addtime }&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;订单号:${orders.id }</td>
				</tr>
				<thead>
					<tr>
						<th>商品图片</th>
						<th>商品名称</th>
						<th>商品数量</th>
						<th>商品小计</th>
						<th>订单状态</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${orders.itemlist }" var="item">
						<c:if test="${orders.id == item.orderid }">
							<tr>
								<td><img style="width: 80px; height: 80px"
									src="/filepath/icon/${item.appid }_icon.jpg"></td>
								<td>${item.appname }</td>
								<td>${item.appnum }</td>
								<td>${item.price }</td>
								<td><c:if test="${orders.status == 0}">待支付</c:if> <c:if
										test="${orders.status == 1}">已支付</c:if> <c:if
										test="${orders.status == 2}">支付失败</c:if> <c:if
										test="${orders.status == 3}">订单关闭</c:if></td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
				<tr style="height: 25px;">
					<td colspan="10"><div name="time"
							style="font-size: 18px; font-weight: 900;">
							<input name="time" type="hidden" value="${orders.addtime }">
						</div></td>
				</tr>
				<tr>
					<td colspan="10"><c:if test="${orders.status == 0}">
							<button type="button" onclick="tenpay('${orders.id}')"
								class="btn btn-danger" style="float: right;">支付</button>
						</c:if> <c:if test="${orders.status == 1}"></c:if> <c:if
							test="${orders.status == 2}">
							<button type="button" onclick="tenpay('${orders.id}')"
								class="btn btn-danger" style="float: right;">再次支付</button>
						</c:if> <c:if test="${orders.status == 3}"></c:if></td>
				</tr>
			</c:forEach>
		</table>
		<script type="text/javascript">
			function tenpay(id) {
				location = "${pageContext.request.contextPath }/pay/toBuy.do?id="
						+ id;
			}
		</script>
	</div>
</body>
</html>