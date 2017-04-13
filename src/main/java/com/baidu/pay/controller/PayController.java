//2017年2月11日上午11:05:55
//Administrator
package com.baidu.pay.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baidu.cart.entity.OrderItem;
import com.baidu.cart.entity.Orders;
import com.baidu.cart.service.CartService;
import com.baidu.pay.controller.client.ClientResponseHandler;
import com.baidu.pay.controller.client.TenpayHttpClient;
import com.baidu.pay.controller.util.TenpayUtil;
import com.baidu.util.PayConfig;
import com.baidu.weixin.service.MQsender;

@Controller
@RequestMapping("pay")
public class PayController {
	@Autowired
	private MQsender mqSender;
	@Autowired
	private CartService cartService;
	
	/**
	 * 2017年2月5日下午3:50:33
	 * Administrator
	 * Function:跳转到支付页面
	 */
	@RequestMapping("toBuy")
	public String toBuy(int id, HttpServletRequest request){
		
		request.getSession().setAttribute("orderid", id);
		
		//获取订单的信息
		Orders orders = this.cartService.orderItemlist(id);
		List<OrderItem> itemlist = orders.getItemlist();
		String appnames = "";
		for (OrderItem item : itemlist) {
			appnames += "," + item.getAppname();
		}
		request.setAttribute("appnames", appnames.substring(1));
		request.setAttribute("totalprice", orders.getTotalprice());
		//生成一个10位序列号作为订单编号
		String currTime = TenpayUtil.getCurrTime();
		String strTime = currTime.substring(8, currTime.length());
		String strRandom = TenpayUtil.buildRandom(4) + "";
		String strReq = strTime + strRandom;
		request.setAttribute("strReq", strReq);
		
		return "pay/index";
	}
	/**
	 * 2017年2月11日上午9:09:50
	 * Administrator
	 * Function:支付请求后台，跳转到财付通支付页面
	 */
	@RequestMapping("payRequest")
	public String payRequest(HttpServletRequest request, HttpServletResponse response){
		int orderid = (int) request.getSession().getAttribute("orderid");
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		//获取提交的商品价格
		String order_price=request.getParameter("order_price");
		/* 商品价格（包含运费），以分为单位 */
		double total_fee = (Double.valueOf(order_price) * 100);
		int fee = (int)total_fee;
		  
		//获取提交的商品名称
		String product_name=request.getParameter("product_name");  

		//获取提交的备注信息
		String remarkexplain=request.getParameter("remarkexplain");  

		String desc = "商品：" + product_name+",备注："+remarkexplain;

		//获取提交的订单号
		String out_trade_no=request.getParameter("order_no");  

		//支付方式
		String trade_mode=request.getParameter("trade_mode"); 

		String currTime = TenpayUtil.getCurrTime();
		//创建支付请求对象
		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init();
		//设置密钥
		reqHandler.setKey(PayConfig.key);
		//设置支付网关
		reqHandler.setGateUrl("https://gw.tenpay.com/gateway/pay.htm");

		//-----------------------------
		//设置支付参数
		//-----------------------------
		reqHandler.setParameter("partner", PayConfig.partner);		        //商户号
		reqHandler.setParameter("out_trade_no", out_trade_no);		//商家订单号
		reqHandler.setParameter("total_fee", String.valueOf(fee));			        //商品金额,以分为单位
		reqHandler.setParameter("return_url", PayConfig.return_url+orderid);		    //交易完成后跳转的URL
		reqHandler.setParameter("notify_url", PayConfig.notify_url);		    //接收财付通通知的URL
		reqHandler.setParameter("body", desc);	                    //商品描述
		reqHandler.setParameter("bank_type", "DEFAULT");		    //银行类型(中介担保时此参数无效)
		reqHandler.setParameter("spbill_create_ip",request.getRemoteAddr());   //用户的公网ip，不是商户服务器IP
		reqHandler.setParameter("fee_type", "1");                    //币种，1人民币
		reqHandler.setParameter("subject", desc);              //商品名称(中介交易时必填)

		//系统可选参数
		reqHandler.setParameter("sign_type", "MD5");                //签名类型,默认：MD5
		reqHandler.setParameter("service_version", "1.0");			//版本号，默认为1.0
		reqHandler.setParameter("input_charset", "UTF-8");            //字符编码
		reqHandler.setParameter("sign_key_index", "1");             //密钥序号

		//业务可选参数
		reqHandler.setParameter("attach", "");                      //附加数据，原样返回
		reqHandler.setParameter("product_fee", "");                 //商品费用，必须保证transport_fee + product_fee=total_fee
		reqHandler.setParameter("transport_fee", "0");               //物流费用，必须保证transport_fee + product_fee=total_fee
		reqHandler.setParameter("time_start", currTime);            //订单生成时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("time_expire", "");                 //订单失效时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("buyer_id", "");                    //买方财付通账号
		reqHandler.setParameter("goods_tag", "");                   //商品标记
		reqHandler.setParameter("trade_mode", trade_mode);                 //交易模式，1即时到账(默认)，2中介担保，3后台选择（买家进支付中心列表选择）
		reqHandler.setParameter("transport_desc", "");              //物流说明
		reqHandler.setParameter("trans_type", "1");                  //交易类型，1实物交易，2虚拟交易
		reqHandler.setParameter("agentid", "");                     //平台ID
		reqHandler.setParameter("agent_type", "");                  //代理模式，0无代理(默认)，1表示卡易售模式，2表示网店模式
		reqHandler.setParameter("seller_id", "");                   //卖家商户号，为空则等同于partner

		//请求的url
		String requestUrl = "";
		try {
			requestUrl = reqHandler.getRequestURL();
			System.out.println("requestUrl:  " + requestUrl + "<br><br>");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//获取debug信息,建议把请求和debug信息写入日志，方便定位问题
		String debuginfo = reqHandler.getDebugInfo();
		//System.out.println("requestUrl:  " + requestUrl);
		//System.out.println("sign_String:  " + debuginfo);

		System.out.println("sign_String:  " + debuginfo + "<br><br>");
		
		return "redirect:"+requestUrl;
	}
	/**
	 * 2017年2月11日上午11:03:51
	 * Administrator
	 * Function:接收财付通通知
	 * @throws Exception 
	 */
	@RequestMapping("payNotifyUrl")
	public void payNotifyUrl(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//创建支付应答对象
		ResponseHandler resHandler = new ResponseHandler(request, response);
		resHandler.setKey(PayConfig.key);

		System.out.println("后台回调返回参数:"+resHandler.getAllParameters());

		//判断签名
		if(resHandler.isTenpaySign()) {
			
			//通知id
			String notify_id = resHandler.getParameter("notify_id");
			
			//创建请求对象
			RequestHandler queryReq = new RequestHandler(null, null);
			//通信对象
			TenpayHttpClient httpClient = new TenpayHttpClient();
			//应答对象
			ClientResponseHandler queryRes = new ClientResponseHandler();
			
			//通过通知ID查询，确保通知来至财付通
			queryReq.init();
			queryReq.setKey(PayConfig.key);
			queryReq.setGateUrl("https://gw.tenpay.com/gateway/simpleverifynotifyid.xml");
			queryReq.setParameter("partner", PayConfig.partner);
			queryReq.setParameter("notify_id", notify_id);
			
			//通信对象
			httpClient.setTimeOut(5);
			//设置请求内容
			httpClient.setReqContent(queryReq.getRequestURL());
			System.out.println("验证ID请求字符串:" + queryReq.getRequestURL());
			
			//后台调用
			if(httpClient.call()) {
				//设置结果参数
				queryRes.setContent(httpClient.getResContent());
				System.out.println("验证ID返回字符串:" + httpClient.getResContent());
				queryRes.setKey(PayConfig.key);
					
					
				//获取id验证返回状态码，0表示此通知id是财付通发起
				String retcode = queryRes.getParameter("retcode");
				
				//商户订单号
				//String out_trade_no = resHandler.getParameter("out_trade_no");
				//财付通订单号
				//String transaction_id = resHandler.getParameter("transaction_id");
				//金额,以分为单位
				//String total_fee = resHandler.getParameter("total_fee");
				//如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
				//String discount = resHandler.getParameter("discount");
				//支付结果
				String trade_state = resHandler.getParameter("trade_state");
				//交易模式，1即时到账，2中介担保
				String trade_mode = resHandler.getParameter("trade_mode");
					
				//判断签名及结果
				if(queryRes.isTenpaySign()&& "0".equals(retcode)){ 
					System.out.println("id验证成功");
					
					if("1".equals(trade_mode)){       //即时到账 
						if( "0".equals(trade_state)){
					        //------------------------------
							//即时到账处理业务开始
							//------------------------------
							
							//处理数据库逻辑
							//注意交易单不要重复处理
							//注意判断返回金额
							
							//------------------------------
							//即时到账处理业务完毕
							//------------------------------
							
							System.out.println("即时到账支付成功");
							//给财付通系统发送成功信息，财付通系统收到此结果后不再进行后续通知
							resHandler.sendToCFT("success");
							
						}else{
							System.out.println("即时到账支付失败");
							resHandler.sendToCFT("fail");
						}
					}else if("2".equals(trade_mode)){    //中介担保
						//------------------------------
						//中介担保处理业务开始
						//------------------------------
						
						//处理数据库逻辑
						//注意交易单不要重复处理
						//注意判断返回金额
						
						int iStatus = TenpayUtil.toInt(trade_state);
						switch(iStatus) {
							case 0:		//付款成功
							
								break;
							case 1:		//交易创建
							
								break;
							case 2:		//收获地址填写完毕
							
								break;
							case 4:		//卖家发货成功
							
								break;
							case 5:		//买家收货确认，交易成功
							
								break;
							case 6:		//交易关闭，未完成超时关闭
							
								break;
							case 7:		//修改交易价格成功
							
								break;
							case 8:		//买家发起退款
							
								break;
							case 9:		//退款成功
							
								break;
							case 10:	//退款关闭
							
								break;
							default:
						}
						
						//------------------------------
						//中介担保处理业务完毕
						//------------------------------
						
						System.out.println("trade_state = " + trade_state);
						//给财付通系统发送成功信息，财付通系统收到此结果后不再进行后续通知
						resHandler.sendToCFT("success");
					}
				}else{
					//错误时，返回结果未签名，记录retcode、retmsg看失败详情。
					System.out.println("查询验证签名失败或id验证失败"+",retcode:" + queryRes.getParameter("retcode"));
				}
			} else {
				System.out.println("后台调用通信失败");
				System.out.println(httpClient.getResponseCode());
				System.out.println(httpClient.getErrInfo());
				//有可能因为网络原因，请求已经处理，但未收到应答。
			}
		}else{
			System.out.println("通知签名验证失败");
		}
	}
	/**
	 * 2017年2月11日上午11:02:03
	 * Administrator
	 * Function:交易完成后跳转
	 */
	@RequestMapping("payReturnUrl")
	public String payReturnUrl(Orders order, HttpServletRequest request, HttpServletResponse response){
		//---------------------------------------------------------
		//财付通支付应答（处理回调）示例，商户按照此文档进行开发即可
		//---------------------------------------------------------
		//创建支付应答对象
		ResponseHandler resHandler = new ResponseHandler(request, response);
		resHandler.setKey(PayConfig.key);
		
		System.out.println("前台回调返回参数:"+resHandler.getAllParameters());
		
		//判断签名
		if(resHandler.isTenpaySign()) {
		
		    //通知id
			//String notify_id = resHandler.getParameter("notify_id");
			//商户订单号
			//String out_trade_no = resHandler.getParameter("out_trade_no");
			//财付通订单号
			//String transaction_id = resHandler.getParameter("transaction_id");
			//金额,以分为单位
			//String total_fee = resHandler.getParameter("total_fee");
			//如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
			//String discount = resHandler.getParameter("discount");
			//支付结果
			String trade_state = resHandler.getParameter("trade_state");
			//交易模式，1即时到账，2中介担保
			String trade_mode = resHandler.getParameter("trade_mode");
			
			if("1".equals(trade_mode)){       //即时到账 
				if( "0".equals(trade_state)){
			        //------------------------------
					//即时到账处理业务开始
					//------------------------------
					
		
					//注意交易单不要重复处理
					//注意判断返回金额
					
					//------------------------------
					//即时到账处理业务完毕
					//------------------------------
					System.out.println("即时到帐付款成功");
				}else{
					order.setStatus(2);
					this.cartService.updateStatus(order);
					System.out.println("即时到帐付款失败");
				}
			}else if("2".equals(trade_mode)){    //中介担保
				if( "0".equals(trade_state)){
					//------------------------------
					//中介担保处理业务开始
					//------------------------------
					
		
					//注意交易单不要重复处理
					//注意判断返回金额
				
					//------------------------------
					//中介担保处理业务完毕
					//------------------------------
					
					System.out.println("中介担保付款成功");
				}else{
					System.out.println("trade_state=" + trade_state);
				}
			}
		} else {
			System.out.println("认证签名失败");
		}
		
		//将订单状态改变
		order.setStatus(1);
		this.cartService.updateStatus(order);
		//获取debug信息,建议把debug信息写入日志，方便定位问题
		String debuginfo = resHandler.getDebugInfo();
		System.out.println("debuginfo:" + debuginfo);
		//out.print("sign_String:  " + debuginfo + "<br><br>");
		
		//将订单信息发送到MQ消息服务器
		order = this.cartService.orderItemlist(order.getId());
		List<OrderItem> itemlist = order.getItemlist();
		String appnames = "";
		int totalnum = 0;
		for (OrderItem item : itemlist) {
			appnames += "," + item.getAppname();
			totalnum += item.getAppnum();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("orderID", order.getId()+"");
		map.put("appnames", appnames.substring(1));
		map.put("totalnum", totalnum+"");
		map.put("totalprice", order.getTotalprice()+"");
		map.put("ordertime", order.getAddtime());
		map.put("openid", order.getOpenid());
		map.put("userid", order.getUserid());
		
		JSONObject jo = JSONObject.fromObject(map);
		this.mqSender.sendMessage("MQtopic", jo.toString());
		System.out.println("消息已发送----------------------------------------");
		
		
		return "redirect:/index.jsp";
	}
	
}
