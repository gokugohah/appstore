<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户注册</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrap.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.request.contextPath }/bootstrap/bootstrapValidator.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/jquery-2.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/bootstrapValidator.js"></script>
<script type="text/javascript">
$(function () {
    $('form').bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            nickname: {
                message: '昵称验证失败',
                validators: {
                    notEmpty: {
                        message: '昵称不能为空'
                    },
                    stringLength: {
                        min: 6,
                        max: 18,
                        message: '昵称长度必须在6到18位之间'
                    },
                    regexp: {
                        regexp: /^[a-zA-Z0-9_]+$/,
                        message: '昵称只能包含大写、小写、数字和下划线'
                    }
                }
            },
            password : {
				validators : {
					notEmpty : {
						message : '密码不能为空'
					},
                    stringLength: {
                        min: 6,
                        max: 8,
                        message: '密码长度必须在6到8位之间'
                    }
				}
			},
			phone : {
				validators : {
					notEmpty : {
						message : '手机号不能为空'
					}
				}
			},
            mail: {
                validators: {
                    notEmpty: {
                        message: '邮箱不能为空'
                    },
                    emailAddress: {
                        message: '邮箱地址格式有误'
                    }
                }
            },
            address : {
				validators : {
					notEmpty : {
						message : '地址不能为空'
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
	<h1>用户信息注册页面</h1>
	<form action="${pageContext.request.contextPath }/user/reg.do" method="post">
		<div class="form-group">
			<span class="input-group-addon">昵称</span>
			<input type="text" name="nickname" class="form-control" placeholder="请输入昵称" value="${param.nickname}">
		</div><br>
		<div class="form-group">
			<span class="input-group-addon">密码</span>
			<input type="text" name="password" class="form-control" placeholder="请输入6-8位的密码">
		</div><br>
		<div class="form-group">
			<span class="input-group-addon">手机</span>
			<input type="text" name="phone" class="form-control" placeholder="请输入手机号">
		</div><br>
		<div class="form-group">
			<span class="input-group-addon">邮箱</span>
			<input type="text" name="mail" class="form-control" placeholder="请输入邮箱">
		</div><br>
		<div class="form-group">
			<span class="input-group-addon">地址</span>
			<input type="text" name="address" class="form-control" placeholder="请输入地址">
		</div><br><input type="hidden" name="social_uid" value="${param.social_uid}">
		<input type="hidden" name="id" value="${param.id}">
		<button type="submit" class="btn btn-primary btn-lg">立即注册</button>
		<br>
	</form>
</div>
</center>
</body>
</html>