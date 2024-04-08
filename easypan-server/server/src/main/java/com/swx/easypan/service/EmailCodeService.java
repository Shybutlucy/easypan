package com.swx.easypan.service;

import com.swx.easypan.pojo.EmailCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sw-code
 * @since 2023-05-17
 */
public interface EmailCodeService extends IService<EmailCode> {

    void sendEmailCode(String email, Integer type);

    void checkCode(String email, String code);
}
