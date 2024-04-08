package com.swx.easypan.entity.enums;

public enum FileDelFlagEnums {
    USING(0, "正常"),
    RECYCLE(1, "回收站"),
    DEL(2, "删除");
    private final Integer flag;
    private final String desc;

    FileDelFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}
