package com.baidu.user.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.app.controller.QrcodenoImg;
import com.baidu.user.entity.User;
import com.baidu.user.service.UserService;
import com.baidu.util.HttpUtil;
import com.baidu.util.Md5Util;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private UserService userService;

	@RequestMapping("toLogin")
	public String toLogin(String aa, HttpServletRequest request) {
		if(aa!=null){
			request.getSession().setAttribute("aa", aa);
		}
		return "user/user_login";
	}
	
	@RequestMapping("toReg")
	public String toReg() {
		return "user/user_reg";
	}
	
	@RequestMapping("toExit")
	public String toExit(HttpServletRequest request){
		request.getSession().invalidate();
		return "redirect:/index.jsp";
	}

	@RequestMapping("login")
	public String login(String loginname, String password, HttpServletRequest request) {
		User user = this.userService.login(loginname, password);
		if("true".equals(request.getSession().getAttribute("aa"))){
			request.getSession().setAttribute("loginuser", user);
			return "redirect:/cart/toCart.do";
		}
		if (user!=null) {
			request.getSession().setAttribute("loginuser", user);
			return "redirect:/index.jsp";
		} else {
			return "redirect:toLogin.do";
		}
	}

	@RequestMapping("reg")
	public String reg(User user,HttpServletRequest request) {
		if(user.getId()==""){
			user.setId(UUID.randomUUID().toString().replace("-", ""));
		}
		this.userService.reg(user);
		//第三方登录跳转过来的，注册完直接进入主页
		if(user.getSocial_uid()!=null){
			Map<String, String> map = new HashMap<String, String>();
			//绑定第三方平台用户
			String uid_sign = Md5Util.getMD5(user.getId()+"TOo2xAASovK4msjc1unmu7PqyxBc1gvV");
			map.put("access_token", request.getSession().getAttribute("access_token").toString());
			map.put("uid", user.getId());
			map.put("uid_sign", uid_sign);
			map.put("uname", user.getNickname());
			String bindresult = HttpUtil.post("https://openapi.baidu.com/social/api/2.0/user/bind", map);
			System.out.println(bindresult);
			request.getSession().setAttribute("loginuser", user);
			return "redirect:/index.jsp";
		}
		return "redirect:toLogin.do";
	}
	
	@RequestMapping("userLogin")
	public String userLogin(HttpServletRequest request) {
		System.out.println("获取第三方登录信息");
		// 获取百度社会化服务传递的code
		String code = request.getParameter("code");
		// 获取token的接口
		String url = "https://openapi.baidu.com/social/oauth/2.0/token";
		// 回调地址
		String redirect_uri = "http://127.0.0.1/appstore/user/userLogin.do";
		//将数据放入map集合
		Map<String, String> map = new HashMap<String, String>();
		map.put("grant_type", "authorization_code");
		map.put("client_id", "m6fO55eQNQaU4MmSR8xOSqj7");
		map.put("client_secret", "TOo2xAASovK4msjc1unmu7PqyxBc1gvV");
		map.put("redirect_uri", redirect_uri);
		map.put("code", code);
		//获取令牌token相关信息
		String returnJson = HttpUtil.post(url, map);
		System.out.println(returnJson);
		//解析令牌
		String access_token = JSONObject.fromObject(returnJson).get("access_token").toString();
		//{"expires_in":2592000,"access_token":"51.b23dab07d2e974bd93fcfa3e9057f456.2592000.1486289772.1644510450-8334948","session_secret":"348888ecc54ef49550ca728e3a7667fe","session_key":"8aKDX\/tft1hb4UYfb94EydLUXLoS2t4WEXJkWN4vD950gEXiBAvVHTA\/8X42PSuTJuHvI\/0SA3znw1iDkpyaHHruhYJQWH7xmg==","name":"gokugohah","media_uid":"3373642789","social_uid":1644510450,"media_type":"baidu"}
		request.getSession().setAttribute("access_token", access_token);
		map.clear();
		map.put("access_token", access_token);
		//根据令牌获取合作网站的账号信息
		String userJson = HttpUtil.post("https://openapi.baidu.com/social/api/2.0/user/info", map);
		System.out.println(userJson);
		//{"username":"gokugohah","sex":"1","birthday":"1985","tinyurl":"http:\/\/himg.bdimg.com\/sys\/portraitn\/item\/fac9676f6b75676f686168fa11.jpg","headurl":"http:\/\/himg.bdimg.com\/sys\/portraitn\/item\/fac9676f6b75676f686168fa11.jpg","mainurl":"","hometown_location":[],"work_history":[],"university_history":[],"hs_history":[],"province":"","city":"","is_verified":"0","media_uid":"3373642789","media_type":"baidu","social_uid":1644510450}
		//获取第三方网站用户名
		String nickname = JSONObject.fromObject(userJson).get("username").toString();
		//获取社会化平台的统一uid
		String social_uid = JSONObject.fromObject(userJson).get("social_uid").toString();
		//头像
		String tinyurl = JSONObject.fromObject(userJson).get("tinyurl").toString();
		String birth = JSONObject.fromObject(userJson).get("birthday").toString();
		request.getSession().setAttribute("tinyurl", tinyurl);
		map.clear();
		map.put("access_token", access_token);
		map.put("social_uid", social_uid);
		String uid = this.userService.findUidBySocial_uid(social_uid);
		map.put("uid", uid);
		//查看第三方平台用户的绑定状态
		String statusJson = HttpUtil.post("https://openapi.baidu.com/social/api/2.0/user/bind_status", map);
		System.out.println(statusJson);
		//查看绑定信息的json数组里面是否含有error_msg，如果没有绑定，则含有error_msg；
		//{"error_code":152,"error_msg":"The uid has not been binded yet"}
		boolean flag = statusJson.contains("error_msg");
		//新建一个用户
		User user = new User();
		//uid = Calendar.getInstance().getTime().getTime()+"";
		user.setId(birth+social_uid);
		user.setNickname(nickname);
		user.setSocial_uid(Integer.parseInt(social_uid));
		request.getSession().setAttribute("loginuser", user);
		if(flag){
			//未绑定，进入选择页面
			return "user/user_bind";
		}else{
			//已绑定，直接进入主页面
			user = this.userService.findUser(user.getId());
			System.out.println(user);
			request.getSession().setAttribute("loginuser", user);
			if("true".equals(request.getSession().getAttribute("aa"))){
				return "redirect:/cart/toCart.do";
			}
			return "redirect:/index.jsp";
		}
	}
	/**
	 * 2017年2月23日上午9:09:42
	 * Administrator
	 * Function:跳转到微信扫码登录页面
	 */
	@RequestMapping("toWeixinLogin")
	public String toWeixinLogin(HttpServletRequest request){
		if(request.getSession().getAttribute("uuid")!=null){
			String olduuid = (String) request.getSession().getAttribute("uuid");
			boolean delete = new File("d:\\upload\\"+olduuid+".jpg").delete();
			System.out.println(delete?"过期二维码图片已删除！":"过期二维码图片删除失败！");
		}
		//生成一个随机的uuid
		String uuid = UUID.randomUUID().toString();
		request.getSession().setAttribute("uuid", uuid);
		//生成二维码
		QrcodenoImg.createQRCode("http://gokugohah.3w.dkys.org/weixinTest/toLogin?uuid="+uuid, "d:\\upload\\"+uuid+".jpg");
		return "user/weixinLogin";
	}
	/**
	 * 2017年2月23日下午4:50:52
	 * Administrator
	 * Function:ajax轮询查找是否已经有对应uuid的用户
	 */
	@RequestMapping("WeixinLogin")
	@ResponseBody
	public boolean WeixinLogin(HttpServletRequest request, String uuid){
		//查询用户表里面是否有uuid
		User user = this.userService.findUserByUuid(uuid);
		boolean flag = false;
		if(user!=null){
			flag = true;
			request.getSession().setAttribute("loginuser", user);
			//用户已登录，删除虚拟路径下的二维码图片
			new File("d:\\upload\\"+uuid+".jpg").delete();
		}
		return flag;
	}
}
