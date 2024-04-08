package com.swx.easypan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.swx.common.pojo.BizException;
import com.swx.easypan.redis.RedisComponent;
import com.swx.easypan.entity.config.AppConfig;
import com.swx.easypan.entity.constants.Constants;
import com.swx.easypan.entity.dto.SysSettingsDTO;
import com.swx.easypan.pojo.EmailCode;
import com.swx.easypan.mapper.EmailCodeMapper;
import com.swx.easypan.service.EmailCodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swx.easypan.utils.StringTools;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.time.ZoneId;
import java.util.Date;

/**
 * <p>
 * 邮箱验证码服务实现类
 * </p>
 *
 * @author sw-code
 * @since 2023-05-17
 */
@Service
public class EmailCodeServiceImpl extends ServiceImpl<EmailCodeMapper, EmailCode> implements EmailCodeService {

    private final JavaMailSender javaMailSender;

    private final RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    public EmailCodeServiceImpl(JavaMailSender javaMailSender, RedisComponent redisComponent) {
        this.javaMailSender = javaMailSender;
        this.redisComponent = redisComponent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailCode(String email, Integer type) {
        String code = StringTools.getRandomNumber(Constants.LENGTH_5);
        // 发送验证码
        sendMailCode(email, code);
        // 将之前的验证码置为无效
        baseMapper.disableEmailCode(email);

        EmailCode emailCode = new EmailCode();
        emailCode.setCode(code);
        emailCode.setEmail(email);
        emailCode.setStatus(Constants.ZERO);
        this.save(emailCode);
    }

    @Override
    public void checkCode(String email, String code) {
        LambdaQueryWrapper<EmailCode> wrapper = new LambdaQueryWrapper<EmailCode>().eq(EmailCode::getEmail, email).eq(EmailCode::getCode, code);
        EmailCode one = this.getOne(wrapper);
        if (null == one) {
            throw new BizException("邮箱验证码错误");
        }
        ZoneId zoneId = ZoneId.systemDefault();
        long expire = System.currentTimeMillis() - one.getCreateTime().atZone(zoneId).toInstant().toEpochMilli();
        if (one.getStatus() == 1 || expire > Constants.LENGTH_15 * 60 * 1000) {
            throw new BizException("邮箱验证码已失效");
        }
        baseMapper.disableEmailCode(email);
    }

    private void sendMailCode(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(appConfig.getSendUsername());
            helper.setTo(toEmail);

            SysSettingsDTO sysSettingsDto = redisComponent.getSysSettingDto();

            helper.setSubject(sysSettingsDto.getRegisterMailTitle());
            helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));
            helper.setSentDate(new Date());
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new BizException("邮件发送失败");
        }
    }
}
