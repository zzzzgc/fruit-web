package com.fruit.web.emum;

/**
 * 控制台返回的状态码(未来待扩展)
 */
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
