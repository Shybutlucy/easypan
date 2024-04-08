package com.swx.easypan.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 文件信息表
 * </p>
 *
 * @author sw-code
 * @since 2023-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_info")
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件MD5值
     */
    private String fileMd5;

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
     * 文件路径
     */
    private String filePath;

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
     * 进入回收站时间
     */
    private LocalDateTime recoveryTime;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;

    /**
     * 0:正常 1:回收站 2:删除
     */
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
