package com.fruit.web.emum;

/**
 * 物流类型
 *
 * @author ZGC
 * @date 2018-03-02 16:05
 **/
public enum BusinessShipmentsType {
    /**
     * 市场车
     */
    MARKET(1),
    /**
     * 物流
     */
    LOGISTICS(2),

    /**
     * 自提
     */
    TAKE_THEIR(3)
    ;

    Integer status;

    BusinessShipmentsType(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
