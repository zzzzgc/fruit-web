package com.fruit.web.emum;

public enum BusinessAuthStatus {
    /**
     * 认证审核通过
     */
    OK(0),
    /**
     * 认证审核未通过
     */
    OFF(1),
    /**
     * 认证审核中
     */
    WAIT(2)
    ;

    Integer status;

    BusinessAuthStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
