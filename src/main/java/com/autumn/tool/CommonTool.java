package com.autumn.tool;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by Administrator on 2019/8/28.
 */
public class CommonTool {
    static String chromePath = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";

    /**
     * 用浏览器百度查询信息
     * @param wd
     * @throws IOException
     */
    public static void baidu(String wd) throws IOException {
        Runtime.getRuntime().exec(chromePath+" www.baidu.com/s?wd="+wd);
    }

    /**
     * 图片裁剪通用接口
     *
     * @param src  源图片地址,图片格式PNG
     * @param dest 目的图片地址
     * @param x    图片起始点x坐标
     * @param y    图片起始点y坐标
     * @param w    图片宽度
     * @param h    图片高度
     * @throws IOException 异常处理
     */
    public static void cutImage(String src, String dest, int x, int y, int w, int h)  {
        try{
            //获取png图片的ImageReader的Iterator
            Iterator iterator = ImageIO.getImageReadersByFormatName("png");
            //根据Iterator获取ImageReader
            ImageReader reader = (ImageReader) iterator.next();
            //获取源图片输入流
            InputStream in = new FileInputStream(src);
            //根据源图片输入流获得ImageInputStream流
            ImageInputStream iis = ImageIO.createImageInputStream(in);
            //将ImageInputStream流加载到ImageReader中
            reader.setInput(iis, true);
            //图片读取参数
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, w, h);
            //参数对象设置形状为一定大小的长方形
            param.setSourceRegion(rect);
            //ImageReader根据参数对象获得BufferedImage
            BufferedImage bi = reader.read(0, param);
            //将经过参数对象筛选的图片流写入目标文件中
            ImageIO.write(bi, "png", new File(dest));
        }catch (IOException e){
            System.err.println("裁剪图片失败");
        }
    }

    /**
     * adb截图到sd卡
     * @param imgPath
     * @return
     */
    public static Process adbScreencap(String imgPath){
        if (imgPath==null||imgPath.isEmpty()){
            imgPath = "/sdcard/screenshot.png";
        }

        Process screenshot = null;
        try {
            screenshot = Runtime.getRuntime().exec("adb shell /system/bin/screencap -p "+imgPath);
            screenshot.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return screenshot;
    }


    /**
     * adb将sd卡中的截图pull到电脑
     * @param imgPath
     * @return
     */
    public static Process adbPullScreencap(String screencapPath,String imgPath) {
        if (screencapPath==null||screencapPath.isEmpty()){
            screencapPath = "/sdcard/screenshot.png";
        }

        if (imgPath==null||imgPath.isEmpty()){
            imgPath = "D:/logs/screenshot.png";
        }

        Process pullImg = null;
        try {
            pullImg = Runtime.getRuntime().exec("adb pull "+ screencapPath +" "+imgPath);
            pullImg.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return pullImg;
    }

    /**
     * 先截图再pull下来
     * @param imgPath
     * @return
     */
    public static void adbScreencapAndPull(String imgPath,String pcPath) {
        adbScreencap(imgPath);
        adbPullScreencap(imgPath,pcPath);
    }

    public static void main(String[] args) throws Exception {
        baidu("java");
    }
}
