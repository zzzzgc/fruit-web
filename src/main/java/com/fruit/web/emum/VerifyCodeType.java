package com.fruit.web.emum;

public enum VerifyCodeType {
    //图片验证码
    IMGAGES(1),
    //手机短信验证码
    PHONE_SMS(2);

    private int type;

    VerifyCodeType(int type) {
        this.type = type;
    }

    public static VerifyCodeType getVerifyCodeType(Integer type) throws RuntimeException {
        //避免验证码有概率出现的明明是正确的却验证失败的情况
        synchronized (VerifyCodeType.class) {
            for (VerifyCodeType verifyCodeType : VerifyCodeType.values()) {
                if (verifyCodeType.type == type) {
                    return verifyCodeType;
                }
            }
            throw new RuntimeException("VerifyCodeType枚举类型有误");
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
