//2017年1月15日下午7:18:09
//Administrator
package com.baidu.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.app.entity.App;
import com.baidu.cart.entity.Cart;
import com.baidu.cart.entity.CartItem;
import com.baidu.cart.entity.OrderItem;
import com.baidu.cart.entity.Orders;
import com.baidu.cart.service.CartService;
import com.baidu.user.entity.User;

@Controller
@RequestMapping("cart")
public class CartController {
	@Autowired
	private CartService cartService;
	/**
	 * 2017年2月5日下午3:47:43
	 * Administrator
	 * Function:判断用户是否登录
	 */
	@RequestMapping("loginYN")
	@ResponseBody
	public int loginYN(HttpServletRequest request){
		//判断用户是否登录
		HttpSession session = request.getSession();
		User loginuser = (User) session.getAttribute("loginuser");
		if(loginuser!=null){
			return 100;
		}
		return 0;
	}
	/**
	 * 2017年2月5日下午3:46:44
	 * Administrator
	 * Function:在已登录的情况下，将新添加的商品放入session中，并同步到mongodb
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("sessionYN")
	@ResponseBody
	public String sessionYN(CartItem item,HttpServletRequest request){
		HttpSession session = request.getSession();
		User loginuser = (User) session.getAttribute("loginuser");
		List<CartItem> itemlist1 = new ArrayList<CartItem>();
		//判断session
		Object obj = session.getAttribute(loginuser.getId());
		if(obj==null){
			//当前用户购物车没有商品，把新添加的放到session
			itemlist1.add(item);
			session.setAttribute(loginuser.getId(), itemlist1);
		}else{
			//购物车有商品，更新数量
			itemlist1 = (List<CartItem>) obj;
			//判断购物车里是否有新添加的商品，有就更新数量
			boolean flag = false;
			for (CartItem cartItem : itemlist1) {
				if(item.getAppid().equals(cartItem.getAppid())){
					cartItem.setAppnum(cartItem.getAppnum() + item.getAppnum());
					flag = true;
				}
			}
			if(!flag){
				//购物车没有该商品
				itemlist1.add(item);
			}
			session.setAttribute(loginuser.getId(), itemlist1);
		}
		//更新购物车对象到mongo中
		List<CartItem> list = new ArrayList<CartItem>();
		list.addAll(itemlist1);
		//获取mongo中该用户的购物车对象
		Cart cart = this.cartService.findUserCart(loginuser.getId());
		//在原有的购物车对象基础上更新新添加的商品
		if(cart!=null){
			//判断mongo购物车对象中是否已经有新添加的商品，有就把两者的数量相加
			List<CartItem> itemlist2 = cart.getItemlist();
			list.addAll(itemlist2);
			for(CartItem item1 : itemlist1){
				for(CartItem item2 : itemlist2){
					if(item2.getAppid().equals(item1.getAppid())){
						list.remove(item1);
						list.remove(item2);
						item1.setAppnum(item2.getAppnum()+item1.getAppnum());
						list.add(item1);
					}
				}
			}
			this.cartService.addCart(new Cart(loginuser.getId(),list));
		}else{
			this.cartService.addCart(new Cart(loginuser.getId(),list));
		}
		//将session中新添加的商品同步到mongodb中后，清空session中对应的商品信息
		session.setAttribute(loginuser.getId(), null);
		return "1";
	}
	/**
	 * 2017年2月5日下午3:48:04
	 * Administrator
	 * Function:添加商品后跳转到选择界面
	 */
	@RequestMapping("toResult")
	public String itemlist(){
		return "cart/cart_result";
	}
	/**
	 * 2017年2月5日下午3:48:31
	 * Administrator
	 * Function:进入购物车列表界面
	 */
	@RequestMapping("toCart")
	public String toCart(HttpServletRequest request,HttpServletResponse response){
		User loginuser = (User) request.getSession().getAttribute("loginuser");
		//判断是否登录，登录则返回mongo和cookie中的商品集合，未登录则只返回cookie的商品集合
		List<CartItem> itemlist1 = new ArrayList<CartItem>();
		if(loginuser!=null){
			//获取mongo中的商品集合
			Cart cart = this.cartService.findUserCart(loginuser.getId());
			if(cart!=null){
				itemlist1 = cart.getItemlist();
			}
		}
		//获取cookie中的商品集合
		List<CartItem> itemlist2 = new ArrayList<CartItem>();
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
			for (Cookie c : cookies) {
				String appid = c.getName();
				App app = this.cartService.findApp(appid);
				if(app!=null){
					CartItem item = new CartItem();
					item.setAppid(appid);
					item.setAppname(app.getAppname());
					item.setPrice(app.getPrice());
					item.setAppnum(Integer.parseInt(c.getValue()));
					itemlist2.add(item);
				}
			}
		}
		List<CartItem> list = new ArrayList<CartItem>();
		list.addAll(itemlist1);
		list.addAll(itemlist2);
		//将两个集合中有相同商品的部分，数量相加给其中一个，并删除另外一个，最后将两个集合合并
		for (CartItem item2 : itemlist2) {
			for (CartItem item1 : itemlist1) {
				if(item2.getAppid().equals(item1.getAppid())){
					list.remove(item1);
					list.remove(item2);
					item2.setAppnum(item2.getAppnum()+item1.getAppnum());
					list.add(item2);
				}
			}
		}
		//如果用户已登录，则把cookie中的商品更新到mongo中，并清空cookie
		if(loginuser!=null){
			this.cartService.addCart(new Cart(loginuser.getId(),list));
			//清空cookie中的购物车信息
			for (int i = 0; i < cookies.length; i++) {
				String appid = cookies[i].getName();
				for (CartItem item : list) {
					if(appid.equals(item.getAppid())){
						cookies[i].setValue("");
						cookies[i].setMaxAge(0);
						cookies[i].setPath("/");
						response.addCookie(cookies[i]);
						System.out.println(cookies[i]+"已清理-----------------------------");
					}
				}
			}
		}
		request.setAttribute("itemlist", list);
		String json=JSONArray.fromObject(list).toString();
		//System.out.println(json);
		request.setAttribute("json", json);
		return "cart/cart_list";
	}
	/**
	 * 2017年2月5日下午3:48:46
	 * Administrator
	 * Function:生成订单，将mongodb中的购物车信息存入mysql中，并清空mongodb中对应的数据
	 */
	@RequestMapping("addOrder")
	@ResponseBody
	public Integer addOrder(String json,String address,String name,HttpServletRequest request){
		//在订单表生成该用户的订单信息
		Orders orders = new Orders();
		User loginuser = (User) request.getSession().getAttribute("loginuser");
		orders.setUserid(loginuser.getId());
		orders.setAddress(address);
		//生成订单信息，并获取orderid
		this.cartService.addOrder(orders);
		
		//将选中的商品添加到订单子对象中
		List<OrderItem> itemlist = new ArrayList<OrderItem>();
		JSONArray ja = JSONArray.fromObject(json);
		double totalprice = 0.0;
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			CartItem item = (CartItem) JSONObject.toBean(jo, CartItem.class);
			//如果在购物车页面删除某件商品时，会导致json串有null情况，需要排除
			if(item!=null){
				//获取总价格
				totalprice = totalprice + (item.getPrice())*(item.getAppnum());
				OrderItem orderItem = new OrderItem(item.getAppid(),item.getAppnum(),item.getPrice(),orders.getId());
				orderItem.setAppname(item.getAppname());
				itemlist.add(orderItem);
			}
		}
		//获取订单的总价格
		orders.setTotalprice(totalprice);
		//批量添加订单子对象到mysql中
		this.cartService.addItems(itemlist,orders);
		
		return orders.getId();
	}
	/**
	 * 2017年2月9日上午8:02:34
	 * Administrator
	 * Function:查询订单并跳转到确认订单页面
	 */
	@RequestMapping("toOrder")
	public String toOrder(Integer id,HttpServletRequest request){
		Orders orders = this.cartService.orderItemlist(id);
		request.setAttribute("orders", orders);
		return "cart/order_list";
	}
	/**
	 * 2017年2月6日上午9:11:11
	 * Administrator
	 * Function:将购物车页面的修改信息同步到mongo中
	 */
	@RequestMapping("updateNum")
	@ResponseBody
	public String updateNum(String json,HttpServletRequest request){
		User loginuser = (User) request.getSession().getAttribute("loginuser");
		JSONArray ja = JSONArray.fromObject(json);
		List<CartItem> itemlist = new ArrayList<CartItem>();
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			CartItem item = (CartItem) JSONObject.toBean(jo, CartItem.class);
			itemlist.add(item);
		}
		this.cartService.addCart(new Cart(loginuser.getId(), itemlist));
		return "1";
	}
	/**
	 * 2017年2月9日下午2:00:32
	 * Administrator
	 * Function:我的订单
	 */
	@RequestMapping("myOrder")
	public String myOrder(String userid, HttpServletRequest request){
		//获取订单的信息
		List<Orders> orders = this.cartService.myOrder(userid);
		request.setAttribute("orders", orders);
		return "cart/myorder";
	}
	
}
