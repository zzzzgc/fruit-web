package com.fruit.web.util;


import java.security.MessageDigest;

/**
 * 加密工具箱
 */
public class EncryptUtils {

    private static final String KEY_MD5 = "MD5";

    private static final String KEY_SHA = "SHA";

    private static final String KEY_HMAC = "HMAC";

    /**
     * 32位md5加密
     *
     * @param data
     * @return
     */
    public static String md5Hex(byte[] data) throws Exception {
        String result = "";
        MessageDigest md = MessageDigest.getInstance(KEY_MD5);
        md.update(data);
        byte b[] = md.digest();
        System.out.println(b);
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0) {
                i += 256;
            }

            if (i < 16) {
                buf.append("0");
            }

            buf.append(Integer.toHexString(i));
        }
        result = buf.toString();
        return result;
    }

    /**
     * 16位md5加密
     *
     * @param data
     * @return
     */
    public static String md5(byte[] data) throws Exception {
        return md5Hex(data).substring(8, 24);
    }


    public static byte[] SHA(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance(KEY_SHA);
        md5.update(data);
        return md5.digest();
    }


    public static byte[] HMAC(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance(KEY_HMAC);
        md5.update(data);
        return md5.digest();
    }

    /**
     * MD5可用性测试用例
     */
    private static void Md5Test()  throws Exception{
        String MD5 = md5Hex("123456".getBytes());
        if (MD5.equals("e10adc3949ba59abbe56e057f20f883e")){
            System.out.println("测试MD5加密正常");
        }
    }

    public static void main(String[] args) throws Exception {
        Md5Test();
    }

}
