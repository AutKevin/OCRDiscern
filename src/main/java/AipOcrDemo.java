import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.*;
import java.util.List;

public class AipOcrDemo {
    //设置APPID/AK/SK
    public static final String APP_ID = "15514738";
    public static final String API_KEY = "huA2DXyMM4tcXIBQy7qiIzCn";
    public static final String SECRET_KEY = "********";

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true){
            ocrImg();
            System.in.read();
        }
    }

    public static void ocrImg() throws IOException, InterruptedException{
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "classpath:log4j.properties");
        Process screenshot = Runtime.getRuntime().exec("adb shell /system/bin/screencap -p /sdcard/screenshot.png" );
        //等待程序启动
        //Thread.sleep(1000);
        screenshot.waitFor();
        Process pullImg = Runtime.getRuntime().exec("adb pull /sdcard/screenshot.png D:/logs/screenshot.png" );
        pullImg.waitFor();
        // 调用接口
        String path = "D:\\logs\\screenshot.png";
        cutImage(path,path,80,500,535,140);
        JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        /**
         * 获取识别结果
         */
        Long log_id = (Long) res.get("log_id");
        int words_result_num = (Integer) res.get("words_result_num");
        JSONArray words_result = (JSONArray) res.get("words_result");  //识别返回的字符串为JSONArray
        Iterator iterator = words_result.iterator();   //遍历jsonArray
        List<String> words = new ArrayList();  //存放结果
        while (iterator.hasNext()){
            JSONObject object = (JSONObject) iterator.next();
            String wd = (String)object.get("words");
            words.add(wd);
        }

        System.out.println("log_id: "+log_id);
        System.out.println("words_result_num: "+words_result_num);
        System.out.println("words: "+words.toString());
        if (words_result_num == 3 && words != null){
            for (String w:words){
                System.out.println(w.split(":")[1]);
            }
        }
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
}
