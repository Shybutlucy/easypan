package com.swx.easypan.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author sw-code
 * @since 2023-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_share")
public class FileShare implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享ID
     */
    private String id;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 有效期类型 0:1天 1:7天 2:30天 3:永久有效
     */
    private Integer validType;

    /**
     * 提取码
     */
    private String code;

    /**
     * 浏览次数
     */
    private Integer browseCount;

    /**
     * 保存次数
     */
    private Integer saveCount;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 逻辑删除
     */
    private Integer deleted;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
