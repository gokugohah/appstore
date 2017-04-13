//2017年1月12日上午10:21:47
//Administrator
package com.baidu.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.FileWriterWithEncoding;

import com.baidu.app.entity.App;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerCreateHtml {
	public void createHtml(Map<String,Object> map,String ftlname,String filename){
		try {
			Configuration cfg = new Configuration();
			cfg.setClassForTemplateLoading(this.getClass(), "");
			
			Template template =cfg.getTemplate(ftlname);
			
			//String rootPath = System.getProperty("user.dir");
//			String rootPath = getClass().getResource("/").getPath();
//			rootPath = rootPath.replace("/target/classes/", "/src/main/webapp/");
//			rootPath = rootPath.replace("/", "\\");
			//System.out.println(rootPath);
			
			
			
			
			FileWriterWithEncoding fileWriterWithEncoding =new FileWriterWithEncoding(new File(filename), "UTF-8");
			//FileWriterWithEncoding fileWriterWithEncoding =new FileWriterWithEncoding(new File(rootPath+"apphtml\\"+((App)(map.get("app"))).getAppname()+".html"), "UTF-8");
			
			template.process(map, fileWriterWithEncoding);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<App> appList = new ArrayList<App>();
		
			App app = new App();
			app.setAppname("360锁屏");
			app.setDownloadNums("123123123");
			app.setId("a0761a99e358404c851e96fe43aeac15");
			appList.add(app);
			app = new App();
			app.setAppname("微信");
			app.setDownloadNums("123123123");
			app.setId("9e068d8d1fdf415789f490aefb76eb8c");
			appList.add(app);
			app = new App();
			app.setAppname("快手");
			app.setDownloadNums("123123123");
			app.setId("32626d1d4af847b09663ba7d35169122");
			appList.add(app);
			app = new App();
			app.setAppname("购物大厅");
			app.setDownloadNums("123123123");
			app.setId("9b3ac6a4a6674e2292d92878b7b7c074");
			appList.add(app);
		
			app = new App();
			app.setAppname("映客直播");
			app.setDownloadNums("123123123");
			app.setId("fb75feb3ad9945d9b59f614f6435894e");
			appList.add(app);
			app = new App();
			app.setAppname("米聊");
			app.setDownloadNums("123123123");
			app.setId("53eed1797a0c44dfb78564256727df79");
			appList.add(app);
			app = new App();
			app.setAppname("酷狗直播");
			app.setDownloadNums("123123123");
			app.setId("6788e86f2cd442728174d45f948e2767");
			appList.add(app);
			app = new App();
			app.setAppname("灵犀语音助手");
			app.setDownloadNums("123123123");
			app.setId("b3c050f9dc7647dbb3ef4f034fd0f6aa");
			appList.add(app);
		
			app = new App();
			app.setAppname("QQ音乐");
			app.setDownloadNums("123123123");
			app.setId("64e325604bf14e2581c3eaeb06adee80");
			appList.add(app);
			app = new App();
			app.setAppname("QQ空间");
			app.setDownloadNums("123123123");
			app.setId("a41c30be8d8a4f7a826178f20df839aa");
			appList.add(app);
			app = new App();
			app.setAppname("多说英语");
			app.setDownloadNums("123123123");
			app.setId("4e5d59b25d274c8ebad5a77eca651f0e");
			appList.add(app);
			app = new App();
			app.setAppname("书旗小说");
			app.setDownloadNums("123123123");
			app.setId("2d0812df872147abb795056f53301e64");
			appList.add(app);
		
		
		
		map.put("appList", appList);
		//new FreemarkerCreateHtml().createHtml(new HashMap<String, Object>(),"appPage.ftl","");
		new FreemarkerCreateHtml().createHtml(map,"appList.ftl","D:/upload/html/aa.html");
	}
}
