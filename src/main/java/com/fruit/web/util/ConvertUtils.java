package com.fruit.web.util;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    /**
     * 未知数据类型转BigDecimal
     * @param value
     * @return
     */
    public static BigDecimal toBigDecimal(Object value ) {
        BigDecimal ret = null;
        if( value != null ) {
            if( value instanceof BigDecimal ) {
                ret = (BigDecimal) value;
            } else if( value instanceof String ) {
                ret = new BigDecimal( (String) value );
            } else if( value instanceof BigInteger) {
                ret = new BigDecimal( (BigInteger) value );
            } else if( value instanceof Number ) {
                ret = new BigDecimal( ((Number)value).doubleValue() );
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }


    public static void main(String[] args) throws Exception {



    }


}
