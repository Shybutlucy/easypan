package com.swx.easypan.entity.enums;

public enum UserStatusEnum {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用");

    private Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer status() {
        return this.status;
    }
    public String desc() {
        return this.desc;
    }
}
