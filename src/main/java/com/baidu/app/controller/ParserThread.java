//2017年1月9日下午3:22:38
//Administrator
package com.baidu.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;

import com.baidu.app.entity.App;
import com.baidu.app.entity.AppPic;
import com.baidu.app.entity.AppType;
import com.baidu.app.service.AppService;
import com.baidu.util.HttpUtil;

public class ParserThread implements Runnable {

	private int startPage;
	private int endPage;
	private AppService appService;
	
	public ParserThread() {
		super();
	}
	
	public ParserThread(int startPage, int endPage, AppService appService) {
		super();
		this.startPage = startPage;
		this.endPage = endPage;
		this.appService = appService;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}
	
	public AppService getAppService() {
		return appService;
	}

	public void setAppService(AppService appService) {
		this.appService = appService;
	}

	//重写run方法
	public void run(){
		try {
		//加载每个线程里面的每一页
		//List<App> applist = new ArrayList<App>();
		for (int i = startPage; i <= endPage; i++) {
				//每页分4屏，当滚动条下拉到底部时加载下面的一屏，并在url里传参数screen
				//for (int n = 1; n < 5; n++) {
					
					//Parser parser = new Parser("http://mm.10086.cn/android/software/qbrj?p="+i+"&screen="+n);
					Parser parser = new Parser("http://mm.10086.cn/android/software/qbrj?p="+i);
					parser.setEncoding("UTF-8");
					
					TagNameFilter tagNameFilter = new TagNameFilter("a");
					HasAttributeFilter hasAttributeFilter = new HasAttributeFilter("class","autoword");
					AndFilter andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
					NodeList nodeList = parser.parse(andFilter);
					for(int j = 0;j<nodeList.size();j++){
						String appUrl = ((TagNode)nodeList.elementAt(j)).getAttribute("href");
						//循环获取每个app的内容，存入数据库
						appPage(appUrl);
						//applist.add(app);
					}

				//}
			//自动生成当前页的列表页面		
//			List<App> appList = appService.findappList(i*12,12);
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("appList", appList);
//			new FreemarkerCreateHtml().createHtml(map,"appList.ftl","D:/upload/html/apphtml/"+i+".html");
		}
				//批量添加app
				//appService.addApps(applist);
				//System.out.println("++++++++++++++++++++++添加"+applist.size()+"条数据++++++++++++++++++++++++++");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("=============================解析列表异常===================================");
			}
		
	}
	//
	@SuppressWarnings("static-access")
	public App appPage(String appUrl){
//		String myIP = "";
//		try {
//			myIP = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e1) {
//			e1.printStackTrace();
//		}
		App app = new App();
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			//Parser parser = createParser("http://mm.10086.cn"+appUrl);
			Parser parser = new Parser("http://mm.10086.cn"+appUrl);
			parser.setEncoding("UTF-8");
			String id = UUID.randomUUID().toString().replace("-", "");
			app.setId(id);
			//app名称
			TagNameFilter tagNameFilter = new TagNameFilter("title");
			String appname = parser.parse(tagNameFilter).elementAt(0).toPlainTextString();
			app.setAppname(appname.substring(0, appname.lastIndexOf("-")));
			System.out.println(appname);
			//重置一下
			parser.reset();
			//获取基本信息
			tagNameFilter = new TagNameFilter("div");
			HasAttributeFilter hasAttributeFilter = new HasAttributeFilter("class","mj_info font-f-yh");
			AndFilter andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
			//下载量
			NodeList list = parser.parse(andFilter).elementAt(0).getChildren().elementAt(0).getChildren();
			String downloadNums = list.elementAt(0).getChildren().elementAt(0).getChildren().elementAt(0).toPlainTextString();
			app.setDownloadNums(downloadNums);
			
			//价格
			String price = list.elementAt(1).toPlainTextString();
			price = price.substring(price.lastIndexOf("：")+1).trim().substring(1);
			if("费".equals(price)){
				price = "0.01";
			}
			app.setPrice(Double.parseDouble(price));
			//版本
			String version = list.elementAt(2).toPlainTextString();
			app.setVersion(version.substring(version.lastIndexOf("：")+1).trim());
			//文件大小
			String filesize = list.elementAt(3).toPlainTextString();
			app.setFilesize(filesize.substring(filesize.lastIndexOf("：")+1).trim());
			//开发者
			String developer = list.elementAt(4).toPlainTextString();
			app.setDeveloper(developer.substring(developer.lastIndexOf("：")+1).trim());
			//更新时间
			String updatetime = list.elementAt(6).toPlainTextString();
			app.setUpdatetime(updatetime.substring(updatetime.lastIndexOf("：")+1).trim());
			//系统支持
			String platform = list.elementAt(7).toPlainTextString();
			app.setPlatform(platform.substring(platform.lastIndexOf("：")+1).trim());
			//所属类别
			String apptype = list.elementAt(5).toPlainTextString();
			apptype = apptype.substring(apptype.lastIndexOf("：")+1).trim();
			map.put("apptype", apptype);
			//判断小类别，查询表中有没有该类别；有，获取类别id；没有，新增类别并返回id；（mongodb）
			String typeid = "";
			String pid = "";
			if(apptype.split(" ").length!=0){
				//有大类别
				//获取大类别的id
				pid = appService.findType(apptype.split(" ")[0]);
				typeid = pid;
				if(apptype.split(" ").length>1){
					//有小类别
					typeid = appService.findType(apptype.split(" ")[1]);
					if(typeid==null){
						AppType type = new AppType();
						type.setTypeName(apptype.split(" ")[1]);
						type.setPid(pid);
						type.setDescription(apptype.split(" ")[1]);
						appService.addMongoTypeid(type);
						System.out.println("--------------添加类别"+apptype.split(" ")[1]+"------------------");
						typeid = type.getId();
					}
				}
			}
			//用获取的id字符串替换掉之前的文字字符串
			app.setApptype(typeid);
			
			//重置一下
			parser.reset();
			//app描述
//			tagNameFilter = new TagNameFilter("p");
//			hasAttributeFilter = new HasAttributeFilter("class", "cff8400 fz14 lh24 mb5");
//			andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
//			String description1 = parser.parse(andFilter).elementAt(0).toHtml();
//			parser.reset();
			tagNameFilter = new TagNameFilter("div");
			hasAttributeFilter = new HasAttributeFilter("class", "mj_yyjs font-f-yh");
			andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
			String description2 = parser.parse(andFilter).elementAt(0).toPlainTextString().trim();
			app.setDescription(description2);
			
			//图标
			parser.reset();
			tagNameFilter = new TagNameFilter("img");
			hasAttributeFilter = new HasAttributeFilter("id", "appicon");
			andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
			String iconUrl = ((TagNode)parser.parse(andFilter).elementAt(0)).getAttribute("src");
			//String iconType = iconUrl.substring(iconUrl.lastIndexOf("."));
			HttpUtil.httpDownload(iconUrl, "D:/upload/icon/"+id+"_icon.jpg");
			app.setAppicon("/filepath/icon/"+id+"_icon.jpg");
			
			//apk下载
			parser.reset();
			tagNameFilter = new TagNameFilter("a");
			hasAttributeFilter = new HasAttributeFilter("class","mj_xzdbd");
			andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
			NodeList nodeList = parser.parse(andFilter);
			//判断是否是免费
			if(nodeList.size()!=0){
				//String apkUrl = ((TagNode)nodeList.elementAt(0)).getAttribute("href");
				//HttpUtil.httpDownload(apkUrl, "D:/upload/apk/"+id+".apk");
				String apkPath = "/filepath/apk/"+id+".apk";
				app.setApkurl("http://192.168.3.66/appstore/app/appDownload.do?downloadFile="+apkPath);
			}else{
				app.setApkurl("javascript:;");
			}
			
			//获取二维码图片
			parser.reset();
			QrcodenoImg qrcode =new QrcodenoImg();
			//TwoDimensionCode codemain = new TwoDimensionCode();
			//判断是否是免费软件
			if(nodeList.size()!=0){
				//tagNameFilter = new TagNameFilter("div");
				//hasAttributeFilter = new HasAttributeFilter("class","mj_ew");
				//andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
				//String ewmurl = ((TagNode)(parser.parse(andFilter).elementAt(0).getChildren().elementAt(0))).getAttribute("src");
				//下载原网页的二维码图片到本地
				//HttpUtil.httpDownload(ewmurl, "D:/upload/temp/"+id+".png");
				//解析下载下来的二维码
//				String codeurl = codemain.decoderQRCode("/filepath/temp/"+id+".png");
				String apkPath = "/filepath/apk/"+id+".apk";
				//下载apk到本地
//				HttpUtil.httpDownload(codeurl, apkPath);
				//生成本站的二维码下载图片
				
				String imgPath = "D:/upload/code/"+id+".png";
				String encoderContent = "http://192.168.3.66/appstore/app/appDownload.do?downloadFile="+apkPath;
				qrcode.createQRCode(encoderContent, imgPath);
				//codemain.encoderQRCode(encoderContent, imgPath, "png");
				app.setCodeurl("/filepath/code/"+id+".png");
			}else{
				//生成本站的二维码下载图片
				String imgPath = "D:/upload/code/"+id+".png";
				String encoderContent = "http://192.168.3.66/appstore";
				qrcode.createQRCode(encoderContent, imgPath);
				//codemain.encoderQRCode(encoderContent, imgPath, "png");
				app.setCodeurl("/filepath/code/"+id+".png");
			}
			
			//System.out.println(app);
			
			//获取展示图片
			parser.reset();
			List<AppPic> picList = new ArrayList<AppPic>();
			tagNameFilter = new TagNameFilter("div");
			hasAttributeFilter = new HasAttributeFilter("class","mj_img");
			andFilter = new AndFilter(tagNameFilter, hasAttributeFilter);
			nodeList = parser.parse(andFilter);
			for (int i = 0; i < nodeList.size(); i++) {
				AppPic appPic = new AppPic();
				String picUrl = ((TagNode)(nodeList.elementAt(i).getChildren().elementAt(0))).getAttribute("src");
				String picType = picUrl.substring(picUrl.lastIndexOf("."));
				HttpUtil.httpDownload(picUrl, "D:/upload/img/"+id+"_"+(i+1)+picType);
				appPic.setPicUrl("/filepath/img/"+id+"_"+(i+1)+picType);
				appPic.setApp(id);
				picList.add(appPic);
			}
			//添加app
			appService.addapp(app);
			//添加展示图片
			appService.addAppPic(picList);
			
			//自动生成对应app的html静态页面
			
			map.put("app", app);
			map.put("picList", picList);
			new FreemarkerCreateHtml().createHtml(map,"appPage.ftl","D:/upload/html/apphtml/"+id+".html");
			
			return app;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("================================解析页面异常==================================");
			return app;
		}
	}
	
}
