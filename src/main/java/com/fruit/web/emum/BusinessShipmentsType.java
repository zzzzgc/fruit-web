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
    MARKET(0, "市场车"),
    /**
     * 物流
     */
    LOGISTICS(1, "物流"),
    /**
     * 自提
     */
    TAKE_THEIR(2, "自提");

    Integer status;

    String shipmentsTypeName;

    BusinessShipmentsType(Integer status, String shipmentsTypeName) {
        this.status = status;
        this.shipmentsTypeName = shipmentsTypeName;
    }

    /**
     * 根据状态获取物流类型名称
     *
     * @param status
     * @return
     */
    public static synchronized String getShipmentsTypeName(Integer status) {
        for (BusinessShipmentsType businessShipmentsType : BusinessShipmentsType.values()) {
            if (businessShipmentsType.getStatus().equals(status)) {
                return businessShipmentsType.getShipmentsTypeName();
            }
        }
        throw new RuntimeException("不存在该物流类型");
    }

    /**
     * 根据物流类型名称获取状态
     * @param shipmentsTypeName
     * @return
     */
    public static synchronized Integer getStatus(String shipmentsTypeName) {
        for (BusinessShipmentsType businessShipmentsType : BusinessShipmentsType.values()) {
            if (businessShipmentsType.getShipmentsTypeName().equals(shipmentsTypeName)) {
                return businessShipmentsType.getStatus();
            }
        }
        throw new RuntimeException("不存在该物流类型");
    }

    public Integer getStatus() {
        return status;
    }

    public String getShipmentsTypeName() {
        return shipmentsTypeName;
    }
}
