package com.baidu.app.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.swetake.util.Qrcode;


public class QrcodenoImg {
		
	
	/** 
     * 生成二维码(QRCode)图片 
     * @param content 二维码图片的内容
     * @param imgPath 生成二维码图片完整的路径
     * @param ccbpath  二维码图片中间的logo路径
     */  
    public static int createQRCode(String content, String imgPath) {  
        try {  
            Qrcode qrcodeHandler = new Qrcode();  
            qrcodeHandler.setQrcodeErrorCorrect('M');  
            qrcodeHandler.setQrcodeEncodeMode('B');  
            qrcodeHandler.setQrcodeVersion(7);  
  
            // System.out.println(content);  
           //byte[] contentBytes = content.getBytes("gb2312");  
           byte[] contentBytes = content.getBytes("utf-8");  
            BufferedImage bufImg = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);  
            Graphics2D gs = bufImg.createGraphics();  
  
            gs.setBackground(Color.WHITE);  
            gs.clearRect(0, 0, 200, 200);  
  
          // 设定图像颜色 > BLACK  
            gs.setColor(Color.black);  
            // 设置偏移量 不设置可能导致解析出错  
            int pixoff = 8;  
            // 输出内容 > 二维码  
            if (contentBytes.length > 0 && contentBytes.length < 120) {  
                boolean[][]  codeOut =  qrcodeHandler.calQrcode(contentBytes);  
                for (int i = 0; i < codeOut.length; i++) {  
                    for (int j = 0; j < codeOut.length; j++) {  
                        if (codeOut[j][i]) {  
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);  
                        }  
                    }  
                }  
            } else {  
                System.err.println("QRCode content bytes length = "  
                        + contentBytes.length + " not in [ 0,120 ]. ");  
                return -1;
            }  
           // Image img = ImageIO.read(new File(ccbPath));
           
            
            
            /*  //实例化一个Image对象。
            gs.drawImage(img, 55, 55, null);
            gs.dispose();  
            bufImg.flush();  
  */
            //实例化一个Image对象。
          //  gs.drawImage(img, 44, 55, 49, 30, null);
          //  gs.dispose();  
          //  bufImg.flush();
            // 生成二维码QRCode图片  
            File imgFile = new File(imgPath);  
            ImageIO.write(bufImg, "png", imgFile);  
        } catch (Exception e) 
        {  
            e.printStackTrace();  
            return -100;
        }    
        return 0;
}
    
	/*public static void main(String[] args) {

		String imgPath = "D:/aa.png";
		// String content="http://mm.10086.cn/";
		String content = "http://192.168.3.66/appstore/app/appDownload.do?downloadFile=/filepath/apk/01450727be264795b7d62621d12b49f5.apk";
		// String ccbPath="D:/1/J0302953.JPG";
		QrcodenoImg test1 = new QrcodenoImg();
		test1.createQRCode(content, imgPath);

	}*/   
    
}
