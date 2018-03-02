package com.fruit.web.emum;

public enum BusinessInfoType {
    /**
     * 实体店认证
     */
    PHYSICAL_STORE_AUTH(1),
    /**
     * 网店认证
     */
    ONLINE_STORE_AUTH(2)
    ;

    Integer status;

    BusinessInfoType(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
