package com.swx.easypan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.swx.common.pojo.BizException;
import com.swx.common.utils.MD5;
import com.swx.easypan.redis.RedisComponent;
import com.swx.easypan.entity.enums.UserStatusEnum;
import com.swx.easypan.entity.config.AppConfig;
import com.swx.easypan.entity.constants.Constants;
import com.swx.easypan.entity.vo.SessionWebUserVO;
import com.swx.easypan.entity.dto.SysSettingsDTO;
import com.swx.easypan.entity.dto.UserSpaceDTO;
import com.swx.easypan.entity.dto.RegisterDTO;
import com.swx.easypan.pojo.UserInfo;
import com.swx.easypan.mapper.UserInfoMapper;
import com.swx.easypan.service.EmailCodeService;
import com.swx.easypan.service.FileInfoService;
import com.swx.easypan.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swx.easypan.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author sw-code
 * @since 2023-05-16
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private final EmailCodeService emailCodeService;
    private final RedisComponent redisComponent;
    private final FileInfoService fileInfoService;

    @Resource
    private AppConfig appConfig;


    public UserInfoServiceImpl(EmailCodeService emailCodeService, RedisComponent redisComponent, FileInfoService fileInfoService) {
        this.emailCodeService = emailCodeService;
        this.redisComponent = redisComponent;
        this.fileInfoService = fileInfoService;
    }

    /**
     * 注册
     *
     * @param registerDto 注册信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDto) {
        UserInfo emailUser = this.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getEmail, registerDto.getEmail()));
        if (null != emailUser) {
            throw new BizException("邮箱账号已经存在");
        }
        UserInfo nickNameUser = this.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getNickname, registerDto.getNickname()));
        if (null != nickNameUser) {
            throw new BizException("邮箱昵称已经存在");
        }
        // 校验邮箱验证码
        emailCodeService.checkCode(registerDto.getEmail(), registerDto.getEmailCode());

        String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setNickname(registerDto.getNickname());
        userInfo.setEmail(registerDto.getEmail());
        userInfo.setPassword(MD5.encrypt(registerDto.getPassword()));
        userInfo.setUseSpace(0L);
        SysSettingsDTO sysSettingDto = redisComponent.getSysSettingDto();
        userInfo.setTotalSpace(sysSettingDto.getUserInitSpace() * Constants.MB);
        this.save(userInfo);
    }

    @Override
    public SessionWebUserVO login(String email, String password) {
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getEmail, email));
        if (null == userInfo || !userInfo.getPassword().equals(MD5.encrypt(password))) {
            throw new BizException("账号或者密码错误");
        }

        if (UserStatusEnum.DISABLE.status().equals(userInfo.getStatus())) {
            throw new BizException("账号已禁用");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(LocalDateTime.now());
        updateById(updateInfo);

        SessionWebUserVO sessionWebUserVO = new SessionWebUserVO();
        sessionWebUserVO.setNickname(userInfo.getNickname());
        sessionWebUserVO.setId(userInfo.getId());
        sessionWebUserVO.setAvatar(userInfo.getQqAvatar());
        // 判断登陆用户的email是否是管理员email
        sessionWebUserVO.setIsAdmin(
                ArrayUtils.contains(appConfig.getEmails().split(","), email));

        // 用户空间
        UserSpaceDTO userSpace = new UserSpaceDTO();
        // 查询当前用户已经上传文件大小总和
        Long useSpace = fileInfoService.getUseSpace(userInfo.getId());
        userSpace.setUseSpace(useSpace);
        userSpace.setTotalSpace(userInfo.getTotalSpace());
        redisComponent.saveUserSpaceUse(userInfo.getId(), userSpace);
        return sessionWebUserVO;
    }

    /**
     * 重置密码
     *
     * @param email     邮箱
     * @param password  密码
     * @param emailCode 邮箱验证码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String emailCode) {
        UserInfo emailUser = this.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getEmail, email));
        if (null == emailUser) {
            throw new BizException("邮箱账号不存在");
        }
        emailCodeService.checkCode(email, emailCode);
        emailUser.setPassword(MD5.encrypt(password));
        updateById(emailUser);
    }

    @Override
    public Boolean updateUserSpace(String userId, Long useSpace, Long totalSpace) {
        Integer update = baseMapper.updateUserSpace(userId, useSpace, totalSpace);
        return update != null && update >= 1;
    }
}
