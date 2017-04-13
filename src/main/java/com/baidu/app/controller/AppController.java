//2017年1月9日下午1:44:04
//Administrator
package com.baidu.app.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baidu.app.entity.App;
import com.baidu.app.service.AppService;
import com.baidu.util.FileUtil;

@Controller
@RequestMapping("app")
public class AppController {
	@Autowired
	private AppService appService;

	@RequestMapping("toAppDownload")
	public String toAppDownload() {
		return "app/app_download";
	}

	@RequestMapping("toAppList")
	public String toAppList(HttpServletRequest request,int cpage) {
		int totalApp = this.appService.findappcount();
		int totalPage = totalApp/12 + (totalApp%12==0?0:1);
		if(cpage<=0){
			cpage = 1;
		}else if(cpage>totalPage){
			cpage = totalPage;
		}
		List<App> appList = this.appService.findappList((cpage-1)*12,12);
		request.setAttribute("applist", appList);
		request.setAttribute("cpage", cpage);
		request.setAttribute("totalPage", totalPage);
		return "app/app_list";
	}

	@RequestMapping("appList")
	public void appList() {
		try {
			Parser parser = new Parser("http://mm.10086.cn/android/software/qbrj?p=1");
			parser.setEncoding("UTF-8");
			// 获取尾页的超链接地址
			TagNameFilter tagNameFilter = new TagNameFilter("a");
			HasAttributeFilter hasAttributeFilter = new HasAttributeFilter("class", "last");
			AndFilter andFilter = new AndFilter(tagNameFilter,hasAttributeFilter);
			NodeList nodeList = parser.parse(andFilter);
			String attribute = ((TagNode) nodeList.elementAt(0)).getAttribute("href").split("p=")[1];
			// 获取总页数
			if(attribute.contains("&")){
				attribute = attribute.substring(0,attribute.indexOf("&"));
			}
			int totalPage = Integer.parseInt(attribute);
			// 设置多线程，每个线程加载10页的内容
			int threadPage = 10;
			// 获取线程数
			int threadNum = totalPage / threadPage + (totalPage % threadPage == 0 ? 0 : 1);
			for (int i = 0; i < threadNum; i++) {
				// 获取每个线程的起始页、结束页
				int startPage = i * threadPage + 1;
				int endPage = startPage + threadPage - 1;
				ParserThread thread = new ParserThread(startPage, endPage, appService);
				new Thread(thread).start();
				//thread.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("----------------------------静态页面生成完毕-------------------------------------");
	}
	@RequestMapping("appDownload")
	public void appDownload(HttpServletRequest request,HttpServletResponse response,String downloadFile){
		Properties pp = new Properties();
		try {
			InputStream inputStream = getClass().getResourceAsStream("/filepath.properties");
			pp.load(inputStream);
			String filepath = pp.getProperty("filepath");
			downloadFile = downloadFile.replace("/filepath", filepath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtil.downloadFile(request, response, downloadFile);
	}
	
}
