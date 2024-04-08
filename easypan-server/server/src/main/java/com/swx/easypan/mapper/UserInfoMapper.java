package com.swx.easypan.mapper;

import com.swx.easypan.pojo.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author sw-code
 * @since 2023-05-16
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    Integer updateUserSpace(@Param("userId") String userId, @Param("useSpace") Long useSpace, @Param("totalSpace") Long totalSpace);
}
