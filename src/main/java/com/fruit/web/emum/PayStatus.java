package com.fruit.web.emum;

public enum PayStatus {
    /**
     * 未支付
     */
    PAY_NO(0),
    /**
     * 已支付
     */
    PAY_OK(5),
    /**
     * 已退款
     */
    REFUNDED(10),
    ;

    Integer status;

    PayStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
