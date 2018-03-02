package com.fruit.web.emum;

public enum ControllerStatusCode {
    /**
     * 需要登录
     */
    AUTH(401),
    /**
     * 错误
     */
    ERROR(420)
    ;

    Integer status;

    ControllerStatusCode(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
