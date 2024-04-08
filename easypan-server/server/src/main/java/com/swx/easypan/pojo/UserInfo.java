package com.swx.easypan.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author sw-code
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * QQ openID
     */
    private String qqOpenId;

    /**
     * QQ头像
     */
    private String qqAvatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 最后一次登陆时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 使用空间 单位 byte
     */
    private Long useSpace;

    /**
     * 总空间
     */
    private Long totalSpace;

    /**
     * 用户状态，0:禁用 1:启用
     */
    private Integer status;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除 0:删除 1:正常
     */
    @TableLogic
    private Integer deleted;

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
