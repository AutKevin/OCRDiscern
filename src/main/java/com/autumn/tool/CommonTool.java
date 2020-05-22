package com.autumn.tool;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019/8/28.
 */
public class CommonTool {

    /*本地chrome 安装地址*/
    static String chromePath = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";

    /**
     * 打开chrome浏览器
     * @param httpurl http地址
     * @throws IOException
     */
    public static void chrome(String httpurl) throws IOException {
        Runtime.getRuntime().exec(chromePath+" "+httpurl);
    }

    //关键字转为百度url
    public static String baiduUrl(String wd) {
        return "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd="+wd+"&rsv_pq=d82ed2fe000170e0&rsv_t=185cMvh60YJ0ntaMJR8kRDD6cHo4WjUX4pZ7WW9ikzgTb7VX4jfkzzJkgck&rqlang=cn&rsv_enter=1&rsv_dl=tb&rsv_sug3=5&rsv_sug1=1&rsv_sug7=100&rsv_sug2=0&inputT=641&rsv_sug4=1458";
    }

    /**
     * xxqg查询库
     * https://xuexi1905.cn
     * @param wd
     * @return
     */
    public static String xxqgUrl(String wd) {
        //return "https://doc.deeract.com/l2s/api/questions?keyword="+wd;
        return "https://xxqg.achanyao.com?keyword="+wd;
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
        }catch (IllegalArgumentException e){
            System.err.println("裁剪图片区域不正确");
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

    /**
     * 模拟http协议发送get请求
     **/
    public static String sendGetRequest(String url) {
        String result = "";
        InputStream in = null;

        HttpURLConnection connection = null;

        try {
            URL httpUrl = new URL(url);
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                in = connection.getInputStream();

                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                int n = 0;
                byte[] datas = new byte[2048];

                while ((n = in.read(datas)) != -1) {
                    bs.write(datas, 0, n);
                }

                bs.flush();
                result = new String(bs.toByteArray(), "utf-8");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection.disconnect();
            } catch (Exception ex) {
            }
        }

        return result;
    }

    /**
     * 统计str在contentStr中出现的次数
     * @param contentStr
     * @param str
     * @return
     */
    public static int strCountInContent(String contentStr,String str) {
        int count = 0;   //出现次数
        int start = 0;  //开始位置
        while (contentStr.indexOf(str, start) >= 0 && start < contentStr.length()) {
            count++;
            start = contentStr.indexOf(str, start) + str.length();
        }
        return count;
    }

    /**
     * 获取字符串content第一个中文部分
     * @param content
     * @return
     */
    public static String subFirstCN(String content){
        String regex = "[\u4e00-\u9fa5]+";
        Pattern  pattern= Pattern.compile(regex);
        Matcher ma = pattern.matcher(content);
        /*if(ma.find()){
            System.out.println(ma.group(0));
        }*/
        ma.find();
        String result = ma.group(0);
        return result;
    }

    public static void main(String[] args) throws Exception {
        //baidu("java");
        //System.out.println(sendGetRequest("https://www.cnblogs.com/aeolian/p/7746158.html"));

    }
}
