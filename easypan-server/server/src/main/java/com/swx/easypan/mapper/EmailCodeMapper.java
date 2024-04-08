package com.swx.easypan.mapper;

import com.swx.easypan.pojo.EmailCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  邮箱验证码Mapper 接口
 * </p>
 *
 * @author sw-code
 * @since 2023-05-17
 */
public interface EmailCodeMapper extends BaseMapper<EmailCode> {

    void disableEmailCode(@Param("email") String email);
}
