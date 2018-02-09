package com.fruit.web.util;


import com.fruit.web.bean.pay.wechar.WeChatPayConfig;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 转换工具箱
 *
 * @author zgc
 */
public final class ConvertUtils {

    /**
     * javaBean对象转键值对url
     * 限制:最好所有的全局对象类型都是String,因为比如Date类型的数据就会有多种格式的要求(yyMMdd,yy-MM-dd等),所以建议都为String
     *
     * @param javaBean 需要转换的实体
     * @return key=value&key1=value1&k......
     */
    public static String javaBean2KeyValueString(Object javaBean) {
        return map2KeyValueString(javaBean2Map(javaBean));
    }

    /**
     * 集合转键值对的字符串(key=value&key1=value1&.....)
     *
     * @param map 集合
     * @return key=value&key1=value1&k......
     */
    public static String map2KeyValueString(Map<String, String> map) {
        Set<String> keys = map.keySet();
        List<String> keyArr = new ArrayList<>(keys);
        Collections.sort(keyArr);
        StringBuilder sb = new StringBuilder();
        for (String key : keyArr) {
            sb.append("&" + key + "=" + map.get(key));
        }
        return sb.substring(1);
    }

    /**
     * 简单的map转xml格式字符串
     *
     * @param map  集合
     * @param root 根节点名
     * @return xml格式字符串
     */
    public static String map2SimpleXmlStr(Map<String, String> map, String root) {
        StringBuilder sb = new StringBuilder();
        // map转list
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        sb.append("<" + root + ">");
        for (Map.Entry<String, String> entry : list) {
            sb.append("<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">");
        }
        sb.append("</" + root + ">");
        return sb.toString();
    }

    /**
     * 简单的map转xml格式字符串
     *
     * @param map 集合
     * @return xml格式字符串
     */
    public static String map2SimpleXmlStr(Map<String, String> map) {
        return map2SimpleXmlStr(map, "xml");
    }

    public static Map<String,String> simpleXmlStr2map(String xmlString) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(xmlString);
            Element root = doc.getRootElement();
            List<Element> elements = root.elements();
            HashMap<String, String> map = new HashMap<>(elements.size());
            for (Element element : elements) {
                map.put(element.getNodeTypeName(), element.getText());
            }
            return map;
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 未知数据类型转BigDecimal
     *
     * @param value
     * @return
     */
    public static BigDecimal toBigDecimal(Object value) throws ClassCastException {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }

    public static Map<String, String> javaBean2Map(Object object) {
        try {
            HashMap<String, String> map = new HashMap<>(30);
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object obj = field.get(object);
                if (obj != null) {
                    map.put(field.getName(), obj.toString());
                }
            }
            return map;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }


    public static void main(String[] args) throws Exception {


    }


}
