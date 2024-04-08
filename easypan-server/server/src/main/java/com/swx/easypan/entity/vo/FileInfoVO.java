package com.swx.easypan.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class FileInfoVO {
    /**
     * 文件ID
     */
    private String id;

    /**
     * 文件父ID
     */
    private String filePid;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件封面
     */
    private String fileCover;


    /**
     * 0文件 1目录
     */
    private Integer folderType;

    /**
     * 文件分类 1:视频 2:音频 3:图片 4:文档 5:其他
     */
    private Integer fileCategory;

    /**
     * 文件分类 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 9:压缩包 10:其他
     */
    private Integer fileType;

    /**
     * 0:转码中 1:转码失败 2:转码成功
     */
    private Integer status;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 回收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recoveryTime;
}
