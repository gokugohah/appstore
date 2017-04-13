<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>我的购物车</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/Cookies.js"></script>
<script type="text/javascript">
	var itemlist = eval('('+'${json}'+')');
	function confirmCart(){
		//判断用户是否登录，已登录则把mongo中的数据同步到mysql中，未登录则跳转到登录页面
		$.post(
			"${pageContext.request.contextPath }/cart/loginYN.do",
			{},
			function(msg){
				if(msg>0){
					//已登录
					var json = JSON.stringify(itemlist);
					//var json = $.parseJSON('{ "name": "小强", "age": 16 }');
					console.info(json);
					//location = "${pageContext.request.contextPath }/cart/addOrder.do?json="+json;
					var name = $("#name").val();
					var address = $("#address").val();
					$.post(
						"${pageContext.request.contextPath }/cart/addOrder.do",
						{json:json,address:address,name:name},
						function(succ){
							location = "${pageContext.request.contextPath }/cart/toOrder.do?id="+succ;
						}
					);
				}else{
					location = "${pageContext.request.contextPath }/user/toLogin.do?aa=true";
				}
			},
			"json"
		);
	}
	//减少数量
	function reduceNum(appid){
		var appNum = $("#appNum_"+appid).val();
		var result = parseInt(appNum)-1>=1 ? parseInt(appNum)-1 : 1;
		$("#appNum_"+appid).val(result);
		for(var i in itemlist){
			if(appid==itemlist[i].appid){
				itemlist[i].appnum = result;
				//alert(itemlist[i].appname);
				//alert(itemlist[i].appnum);
			}
		}
	}
	//增加数量
	function increaseNum(appid){
		var appNum = $("#appNum_"+appid).val();
		var result = parseInt(appNum)+1;
		$("#appNum_"+appid).val(result);
		for(var i in itemlist){
			if(appid==itemlist[i].appid){
				itemlist[i].appnum = result;
				//alert(itemlist[i].appname);
				//alert(itemlist[i].appnum);
			}
		}
	}
	//删除商品
	function delItem(appid){
		$("#"+appid).hide();
		for(var i in itemlist){
			if(appid==itemlist[i].appid){
				delete itemlist[i];
			}
		}
	}
	//开启编辑
	function editCart(appid){
		$(".edit_"+appid).show();
		$("#appNum_"+appid).attr("disabled",false);
		$("#bj_"+appid).hide();
		$("#js_"+appid).show();
	}
	//结束编辑
	function endCart(appid){
		var appNum = $("#appNum_"+appid).val();
		for(var i in itemlist){
			if(appid==itemlist[i].appid){
				itemlist[i].appnum = appNum;
				$("#price_"+appid).text(itemlist[i].price*appNum+"元");
			}
		}
		$(".edit_"+appid).hide();
		$("#appNum_"+appid).attr("disabled",true);
		$("#bj_"+appid).show();
		$("#js_"+appid).hide();
		//将编辑后的数据同步到mongo或cookie中
		$.post(
			"${pageContext.request.contextPath}/cart/loginYN.do",
			{},
			function(msg){
				if(msg>0){
					//已登录，同步到mongo中
					var json = JSON.stringify(itemlist);
					console.info(json);
					$.post(
						"${pageContext.request.contextPath}/cart/updateNum.do",
						{json:json},
						function(succ){}
					);
				}else{
					//未登录，同步到cookie中
					SetCookie(appid, appNum, 1, "/");
				}
			}
		);
	}
</script>
</head>
<body>
<div style="padding:0 300px">
<h1 style="text-align: center;font-size: 30px;">购物车页面</h1>
<table class="table table-hover">
<thead>
	<tr align="center">
		<td>全选</td>
		<td>预览</td>
		<td>商品名称</td>
		<td>单价</td>
		<td>数量</td>
		<td>总价</td>
		<td>操作</td>
	</tr>
</thead>
<tbody>
<c:forEach items="${itemlist}" var="item">
	<tr id="${item.appid}" align="center">
		<td><input type="checkbox"></td>
		<td><img style="height:100px;" src="/filepath/icon/${item.appid}_icon.jpg" /><input type="hidden" class="appid" value="${item.appid}"></td>
		<td>${item.appname}</td>
		<td>${item.price}</td>
		<td>
			<input class="edit_${item.appid}" style="display: none;" type="button" value=" - " onclick="reduceNum('${item.appid}')">&nbsp;&nbsp;
			<input style="width:40px;text-align: center;" disabled="disabled" id="appNum_${item.appid}" value="${item.appnum}">&nbsp;&nbsp;
			<input class="edit_${item.appid}" style="display: none;" type="button" value=" + " onclick="increaseNum('${item.appid}')"><br><br>
			<button id="bj_${item.appid}" type="button" class="btn btn-primary" onclick="javascript:editCart('${item.appid}')">编辑</button>
			<button id="js_${item.appid}" type="button" style="display: none;" class="btn btn-warning" onclick="javascript:endCart('${item.appid}')">结束</button>
		</td>
		<td><span id="price_${item.appid}">${item.price * item.appnum}元</span></td>
		<td><button type="button" class="btn btn-danger" onclick="javascript:delItem('${item.appid}')">删除</button></td>
	</tr>
</c:forEach>
	<tr>
		<td colspan="11" valign="middle" >
			<span>收货人：<input id="name" value="${loginuser.nickname}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			收货地址：<input id="address" value="${loginuser.address}" style="width: 400px;"><span>
		</td>
	</tr>
	<tr>
		<td colspan="11" align="right"><button type="button" class="btn btn-danger" onclick="javascript:confirmCart()">确认订单</button></td>
	</tr>
</table>
</div>
</body>
</html>