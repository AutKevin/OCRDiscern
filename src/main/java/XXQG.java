import com.autumn.tool.CommonTool;
import com.autumn.tool.PropsUtil;
import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static com.autumn.tool.CommonTool.baiduUrl;

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

            //截图并将图片拉取到本地
            String path = "D:\\logs\\screenshot.png";
            CommonTool.adbScreencapAndPull(null,path);

            //截图 题目
            String destpath = "D:\\logs\\screenshot_question.png";
            CommonTool.cutImage(path, destpath, 45, 300, 634, 175);

            //截图 答案
            String destpath_answer = "D:\\logs\\screenshot_answer.png";
            CommonTool.cutImage(path, destpath_answer, 40, 510, 634, 500);

            // 调用百度识别接口
            JSONObject res = client.basicGeneral(destpath, new HashMap<String, String>());

            /**
             * OCR识别题目
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

            /*System.out.println("log_id: " + log_id);
            System.out.println("words_result_num: " + words_result_num);
            System.out.println("words: " + words.toString());*/

            //OCR识别 答案
            JSONObject res_a = client.basicGeneral(destpath_answer, new HashMap<String, String>());
            Long log_id_a = (Long) res_a.get("log_id");
            int words_result_num_a = (Integer) res_a.get("words_result_num");
            JSONArray words_result_a = (JSONArray) res_a.get("words_result");  //识别返回的字符串为JSONArray
            Iterator iterator_a = words_result_a.iterator();   //遍历jsonArray
            List<String> words_a = new ArrayList();  //存放结果
            while (iterator_a.hasNext()) {
                JSONObject object = (JSONObject) iterator_a.next();
                String wd = (String) object.get("words");
                words_a.add(wd);
            }

            /*System.out.println("log_id_a: " + log_id_a);
            System.out.println("words_result_num_a: " + words_result_num_a);
            System.out.println("words_a: " + words_a.toString());*/

            /*分析结果*/
            //获取关键词
            String words_r =words.toString().substring(1,words.toString().length()-1);
            String firstCN = CommonTool.subFirstCN(words_r);  //获取开头中文

            if (firstCN.length()>10){
                 firstCN = firstCN.substring(0,10);
            }

            System.err.println("搜索关键词:"+firstCN);

            //利用chrome搜索学习强国答案
            CommonTool.chrome(CommonTool.xxqgUrl(firstCN));

            //根据关键词拼接答案库
            String answerLibUrl = CommonTool.xxqgUrl(firstCN);
            //String answerLibUrl ="https://www.hack520.com/666.html";  //页面答案

            //获取答案库返回内容
            String content = CommonTool.sendGetRequest(answerLibUrl);  //获取结果
            //System.err.println(content);
            /*String words_r =words.toString().substring(1,words.toString().length()-1).replaceAll(" ","");
            words_r="%2B学习强国%20"+words_r;
            String content = CommonTool.sendGetRequest(baiduUrl(words_r));*/  //获取百度内容

            /*分析各个答案在返回的答案库中占的比例*/
            Map resultMap = new HashMap();
            String maxStr = "";
            int maxValue = 0;  //最后可能的答案
            for (String s:words_a){
                int count = CommonTool.strCountInContent(content,s);
                resultMap.put(s,count);
                if (count > 0 && count > maxValue){
                    maxValue = count;
                    maxStr = s;
                }
                System.out.println(s+"出现次数为： "+count);
            }
            System.err.println("------------答案为: "+maxStr+" (" + maxValue+"次)");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

}
