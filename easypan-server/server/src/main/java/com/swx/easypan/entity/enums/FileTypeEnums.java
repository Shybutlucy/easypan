package com.swx.easypan.entity.enums;

import org.apache.commons.lang3.ArrayUtils;

public enum FileTypeEnums {
    VIDEO(FileCategoryEnums.VIDEO, 1, new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov", ".flv"}, "视频"),
    MUSIC(FileCategoryEnums.MUSIC, 2, new String[]{".mp3", ".wav", ".wma", ".mp2", ".flac", ".midi", ".ra", ".ape", ".aac", ".cda"}, "音频"),
    IMAGE(FileCategoryEnums.IMAGE, 3, new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".dds", ".psd", ".pdt", ".webp", ".xmp", ".svg", ".tiff"}, "图片"),
    PDF(FileCategoryEnums.DOC, 4, new String[]{".pdf"}, "pdf"),
    WORD(FileCategoryEnums.DOC, 5, new String[]{".doc", ".docx"}, "word"),
    EXCEL(FileCategoryEnums.DOC, 6, new String[]{".xlsx", ".excel"}, "excel"),
    TXT(FileCategoryEnums.DOC, 7, new String[]{".txt"}, "文本"),
    CODE(FileCategoryEnums.DOC, 8, new String[]{".h", ".c", ".hpp", ".hxx", ".cpp", ".cc", ".c++", ".cxx", ".m", ".o", ".s", ".dll", ".cs",
            ".java", ".class", ".js", ".ts", ".css", ".scss", "vue", ".jsx", ".sql", ".md", ".json", ".html", ".xml", ".vue"}, "代码"),
    ZIP(FileCategoryEnums.OTHER, 9, new String[]{".rar", ".zip", ".7z", ".cab", ".arj", ".lzh", ".tar", ".gz", ".ace", ".uue", "bz", ".jar", ".iso"}, "视频"),
    OTHER(FileCategoryEnums.OTHER, 10, new String[]{}, "其他");

    private FileCategoryEnums category;
    private Integer type;
    private String[] suffixs;
    private String desc;

    FileTypeEnums(FileCategoryEnums category, Integer type, String[] suffix, String desc) {
        this.category = category;
        this.type = type;
        this.suffixs = suffix;
        this.desc = desc;
    }

    public static FileTypeEnums getBySuffix(String suffix) {
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (ArrayUtils.contains(item.getSuffixs(), suffix)) {
                return item;
            }
        }
        return FileTypeEnums.OTHER;
    }

    public static FileTypeEnums getByType(Integer type) {
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public FileCategoryEnums getCategory() {
        return category;
    }

    public Integer getType() {
        return type;
    }

    public String[] getSuffixs() {
        return suffixs;
    }

    public String getDesc() {
        return desc;
    }
}
