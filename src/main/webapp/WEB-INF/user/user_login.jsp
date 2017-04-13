<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户登录</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrapValidator.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrapValidator.js"></script>
<script type="text/javascript">
	$(function() {
		$('form').bootstrapValidator({
			message : 'This value is not valid',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			fields : {
				loginname : {
					message : '用户名验证失败',
					validators : {
						notEmpty : {
							message : '用户名不能为空'
						}
					}
				},
				password : {
					validators : {
						notEmpty : {
							message : '密码不能为空'
						}
					}
				}
			}
		});
	});
</script>
</head>
<body>
	<center>
		<div style="padding: 100px 100px 10px; width: 500px">
			<h1>用户登录</h1>
			<form action="${pageContext.request.contextPath }/user/login.do"
				method="post">
				<div class="form-group ">
					<label>昵称</label> <input type="text"
						name="loginname" class="form-control" placeholder="邮箱/手机号/昵称">
				</div>
				<br>
				<div class="form-group ">
					<label>密码</label> <input type="password"
						name="password" class="form-control" placeholder="请输入密码">
				</div>
				<br>
				<button type="submit" class="btn btn-primary btn-lg">立即登录</button>
				<button type="button"
					onclick="javascript:location='${pageContext.request.contextPath }/user/toReg.do'"
					class="btn btn-primary btn-lg">注册</button>
				<br>
			</form>

			<div class="space-6">
				<br>
			</div>

			<div class="social-or-login center">
				<span class="bigger-110">第三方登录</span><br>
			</div>

			<div>
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
		</div>
	</center>
</body>
</html>