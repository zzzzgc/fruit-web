package com.fruit.web.util;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * 转换工具箱
 */
public class ConvertUtils {

    /**
     * 集合转键值对的字符串(key=value&key1=value1&.....)
     *
     * @return
     */
    public static String map2KeyValueString(Map<String, String> map) {
        Set<String> keys = map.keySet();
        List<String> keyArr = new ArrayList<>(keys);
        Collections.sort(keyArr);
        StringBuffer sb = new StringBuffer();
        for (String key : keyArr) {
            sb.append("&" + key + "=" + map.get(key));
        }
        return sb.substring(1);
    }

    /**
     * 简单的map转xml格式字符串
     *
     * @param map
     * @return
     */
    public static String map2SimpleXmlStr(Map<String, String> map) {
        StringBuffer sb = new StringBuffer();
        Set<String> keys = map.keySet();
        sb.append("<xml>");
        for (String key : keys) {
            sb.append("<" + key + ">" + map.get(key) + "</" + key + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {



    }


}
