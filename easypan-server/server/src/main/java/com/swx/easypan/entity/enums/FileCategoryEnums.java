package com.swx.easypan.entity.enums;

public enum FileCategoryEnums {

    VIDEO(1, "video", "视频"),
    MUSIC(2, "music", "音乐"),
    IMAGE(3, "image", "图片"),
    DOC(4, "doc", "文档"),
    OTHER(5, "other", "其他");

    private final Integer category;
    private final String code;

    FileCategoryEnums(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
    }

    public static FileCategoryEnums getByCode(String code) {
        for (FileCategoryEnums item : FileCategoryEnums.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public Integer getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }
}
