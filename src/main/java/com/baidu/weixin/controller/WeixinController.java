//2017年2月14日下午2:19:34
//Administrator
package com.baidu.weixin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.baidu.util.HttpUtil;
import com.baidu.util.weixin.Config;
import com.baidu.util.weixin.SignUtil;
import com.baidu.weixin.service.CoreService;
/** 
 * 核心请求处理类 
 */
@Controller
@RequestMapping("weixin")
public class WeixinController {

	/**
	 * 2017年2月14日下午2:58:59
	 * Administrator
	 * Function:确认请求来自于微信服务器
	 */
	@RequestMapping(value="core",method=RequestMethod.GET)
	public void core(HttpServletRequest request,HttpServletResponse response) throws IOException{
		/** 
	     * 确认请求来自微信服务器 
	     */
		// 微信加密签名  
        String signature = request.getParameter("signature");  
        // 时间戳  
        String timestamp = request.getParameter("timestamp");  
        // 随机数  
        String nonce = request.getParameter("nonce");  
        // 随机字符串  
        String echostr = request.getParameter("echostr");  
  
        PrintWriter out = response.getWriter();  
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
        	System.out.println(echostr);
            out.print(echostr);  
        }
        out.close();  
        out = null;
	}
	/** 
     * 处理微信服务器发来的消息 
     */
	@RequestMapping(value="core",method=RequestMethod.POST)
	public void core2(HttpServletRequest request,HttpServletResponse response) throws IOException{
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");  
  
        // 调用核心业务类接收消息、处理消息  
        String respMessage = CoreService.processRequest(request);  
          
        // 响应消息  
        PrintWriter out = response.getWriter();  
        out.print(respMessage);  
        out.close();
	}
	/**
	 * 2017年2月17日下午1:27:11
	 * Administrator
	 * Function:授权后，通过code获取网页授权access_token
	 */
	@RequestMapping("toBind")
	public String toBind(String code, HttpServletRequest request){
		String requestUrl = " https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Config.appID+"&secret="+Config.appsecret+"&code="+code+"&grant_type=authorization_code";
		
		String result = HttpUtil.post(requestUrl, null);
		//{"access_token":"PCWkHHiyLgf8mbxRTPi3Ss20HukFKq_KGhYEb4ynMSC6WS5kLGVCvUY_2jVsX2e-uwNZmCelsA3cskQ0eHixY2aGBhxwClIvRHK_jHOn3c4","expires_in":7200,"refresh_token":"31AetaE-UF3Ohed5Rzr1rAgAEwBUBcB75q6EIAtxPWaXDnyaCV27xmPKRiM_irS4489HmIwjYFvCmKGzhTH4JDoGYzyAyhHp-u4w24IOsMI","openid":"odCojxEJmRz68_SFieaVTeXNN2PY","scope":"snsapi_userinfo"}
		System.out.println(result);
		//当scope是snsapi_userinfo时，可以获取用户信息
		String access_token = JSONObject.fromObject(result).getString("access_token");
		//String refresh_token = JSONObject.fromObject(result).getString("refresh_token");
		String openid = JSONObject.fromObject(result).getString("openid");
		
		String message = HttpUtil.post("https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN", null);
		//{"openid":"odCojxEJmRz68_SFieaVTeXNN2PY","nickname":"修罗","sex":1,"language":"zh_CN","city":"信阳","province":"河南","country":"中国","headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/xibV64FibPHfRxZCcFiaxw8HPfLTITn0HTPW9HFJBZn2ica9EFCrhhZTCevFlJPqzAb0ZUuomvy1z7E9ZoctbHA7sBu0AEMTyicjG\/0","privilege":[]}
		System.out.println(message);
		String nickname = JSONObject.fromObject(message).getString("nickname");
		String headimgurl = JSONObject.fromObject(message).getString("headimgurl");
		Map<String, String> map = new HashMap<String, String>();
		map.put("nickname", nickname);
		map.put("openid", openid);
		map.put("headimgurl", headimgurl.replace("\\/", "/"));
		
		request.setAttribute("user", map);
		return "weixin/weixin_bind";
	}
	
}
