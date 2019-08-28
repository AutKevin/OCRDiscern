import com.autumn.tool.CommonTool;
import com.autumn.tool.PropsUtil;
import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * xxqg tzdt
 */
public class XXQG {
    //设置APPID/AK/SK
    public static final String APP_ID = PropsUtil.getString(PropsUtil.loadProps("baidu.properties"),"APP_ID" );
    public static final String API_KEY = PropsUtil.getString(PropsUtil.loadProps("baidu.properties"),"API_KEY" );
    public static final String SECRET_KEY = PropsUtil.getString(PropsUtil.loadProps("baidu.properties"),"SECRET_KEY" );

    public static void main(String[] args) throws IOException{
        while (true){
            ocrImg();
            System.in.read();
        }
    }

    public static void ocrImg(){
        try {
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
            Process screenshot = Runtime.getRuntime().exec("adb shell /system/bin/screencap -p /sdcard/screenshot.png");
            //等待程序启动
            //Thread.sleep(1000);
            screenshot.waitFor();
            Process pullImg = Runtime.getRuntime().exec("adb pull /sdcard/screenshot.png D:/logs/screenshot.png");
            pullImg.waitFor();
            // 调用接口
            String path = "D:\\logs\\screenshot.png";
            CommonTool.cutImage(path, path, 45, 300, 634, 175);
            JSONObject res = client.basicGeneral(path, new HashMap<String, String>());

            /**
             * 获取识别结果
             */
            Long log_id = (Long) res.get("log_id");
            int words_result_num = (Integer) res.get("words_result_num");
            JSONArray words_result = (JSONArray) res.get("words_result");  //识别返回的字符串为JSONArray
            Iterator iterator = words_result.iterator();   //遍历jsonArray
            List<String> words = new ArrayList();  //存放结果
            while (iterator.hasNext()) {
                JSONObject object = (JSONObject) iterator.next();
                String wd = (String) object.get("words");
                words.add(wd);
            }

            System.out.println("log_id: " + log_id);
            System.out.println("words_result_num: " + words_result_num);
            System.out.println("words: " + words.toString());
            CommonTool.baidu(words.toString().replaceAll(" ",""));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

}
