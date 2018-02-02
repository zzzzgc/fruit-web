package com.fruit.web.controller;

import com.jfinal.ext2.kit.RandomKit;

/**
 * 支付回调接口
 * @Author: ZGC
 * @Date Created in 9:35 2018/1/8
 */
public class PayController {

    /**
     * 微信支付通用回调接口
     */
    public void wecharCallBack(){

    }

    public static void main(String[] args) {
        System.out.println(RandomKit.random(0, 16));
        System.out.println(RandomKit.randomMD5Str());
    }
}
