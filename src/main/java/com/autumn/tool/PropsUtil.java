package com.autumn.tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性文件工具类
 */
public class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);
    /**
     * 加载属性文件
     * @param fileName fileName一定要在class下面及java根目录或者resource跟目录下
     * @return
     */
    public static Properties loadProps(String fileName) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            //将资源文件加载为流
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            props.load(is);
            if(is==null){
                throw new FileNotFoundException(fileName+"file is not Found");
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("load properties file filure",e);
        } catch (IOException e) {
            LOGGER.error("load properties file filure",e);
        } finally {
            if(is !=null){
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("close input stream failure",e);
                }
            }
        }
        return props;
    }

    /**
     * 获取字符型属性（默认值为空字符串）
     * @param props
     * @param key
     * @return
     */
    public static String getString(Properties props,String key){
        return getString(props,key,"");
    }

    /**
     * 获取字符型属性（可制定默认值）
     * @param props
     * @param key
     * @param defaultValue 当文件中无此key对应的则返回defaultValue
     * @return
     */
    public static String getString(Properties props,String key,String defaultValue){
        String value = defaultValue;
        if (props.containsKey(key)){
            value = props.getProperty(key);
        }
        return value;
    }
}
