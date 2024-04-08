package com.swx.easypan.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.swx.common.annotation.ResponseResult;
import com.swx.common.pojo.BizException;
import com.swx.common.utils.FileUtil;
import com.swx.common.utils.MD5;
import com.swx.easypan.annotation.LoginValidator;
import com.swx.easypan.entity.config.AppConfig;
import com.swx.easypan.entity.dto.CreateImageCode;
import com.swx.easypan.entity.constants.Constants;
import com.swx.easypan.entity.dto.UserSpaceDTO;
import com.swx.easypan.entity.vo.SessionWebUserVO;
import com.swx.easypan.entity.dto.LoginDTO;
import com.swx.easypan.entity.dto.RegisterDTO;
import com.swx.easypan.pojo.UserInfo;
import com.swx.easypan.service.EmailCodeService;
import com.swx.easypan.service.UserInfoService;
import com.swx.easypan.service.common.UserFileService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author sw-code
 * @since 2023-05-16
 */
@RestController("userInfoController")
@ResponseResult
@Validated
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final EmailCodeService emailCodeService;
    private final UserFileService userFileService;

    @Resource
    AppConfig appConfig;

    public UserInfoController(UserInfoService userInfoService, EmailCodeService emailCodeService, UserFileService userFileService) {
        this.userInfoService = userInfoService;
        this.emailCodeService = emailCodeService;
        this.userFileService = userFileService;
    }

    @GetMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }

    @PostMapping("/sendEmailCode")
    public void sendEmailCode(HttpSession session,
                              @NotNull String email,
                              @NotNull String checkCode,
                              @NotNull Integer type) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
                throw new BizException("图片验证码错误");
            }
            // 0:注册 1：找回
            if (type == 0) {
                UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getEmail, email));
                if (null != userInfo) {
                    throw new BizException("邮箱已存在");
                }
            }
            emailCodeService.sendEmailCode(email, type);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

    @PostMapping("/register")
    public void register(HttpSession session, @Valid @RequestBody RegisterDTO registerDto) {
        try {
            if (!registerDto.getCheckCode().equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BizException("图片验证码错误");
            }
            userInfoService.register(registerDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @PostMapping("/login")
    public SessionWebUserVO login(HttpSession session,
                                  @Valid @RequestBody LoginDTO loginDto) {
        try {
            if (!loginDto.getCheckCode().equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BizException("图片验证码错误");
            }
            SessionWebUserVO sessionWebUserVo = userInfoService.login(loginDto.getEmail(), loginDto.getPassword());
            session.setAttribute(Constants.SESSION_KEY, sessionWebUserVo);
            return sessionWebUserVo;
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @PostMapping("/resetPwd")
    public void resetPwd(HttpSession session,
                         @Valid @RequestBody RegisterDTO registerDto) {
        try {
            if (!registerDto.getCheckCode().equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BizException("图片验证码错误");
            }
            userInfoService.resetPwd(registerDto.getEmail(), registerDto.getPassword(), registerDto.getEmailCode());
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @GetMapping("getAvatar/{userId}")
    public void getAvatar(HttpServletResponse response, @PathVariable @NotNull String userId) {
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
        File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        if (!file.exists()) {
            // 默认头像
            String defaultAvatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT;
            File defaultAvatar = new File(defaultAvatarPath);
            if (!defaultAvatar.exists()) {
                printNoDefaultImage(response);
                return;
            }
            avatarPath = defaultAvatarPath;
        }
        response.setContentType("image/jpg");
        FileUtil.readFile(response, avatarPath);
    }

    @GetMapping("getUserInfo")
    @LoginValidator
    public SessionWebUserVO getUserInfo(HttpSession session) {
        return (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
    }

    @GetMapping("getUseSpace")
    @LoginValidator
    public UserSpaceDTO getUseSpace(HttpSession session) {
        SessionWebUserVO userVo = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        return userFileService.getUseSpace(userVo.getId());
    }

    @PostMapping("/logout")
    @LoginValidator
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @PostMapping("/updateUserAvatar")
    @LoginValidator
    public void updateUserAvatar(HttpSession session, MultipartFile avatar) {
        SessionWebUserVO userVo = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();
        }
        File targetFile = new File(targetFileFolder.getPath() + "/" + userVo.getId() + Constants.AVATAR_SUFFIX);
        try {
            avatar.transferTo(targetFile);
        } catch (IOException e) {
            throw new BizException("头像更新失败");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userVo.getId());
        userInfo.setQqAvatar("");
        userInfoService.updateById(userInfo);
        userVo.setAvatar(null);
        session.setAttribute(Constants.SESSION_KEY, userVo);
    }

    @PostMapping("/updatePassword")
    @LoginValidator
    public void updatePassword(HttpSession session,
                               @Size(min = 8, max = 18, message = "密码长度8-18") String password) {
        SessionWebUserVO userVo = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userVo.getId());
        userInfo.setPassword(MD5.encrypt(password));
        userInfoService.updateById(userInfo);
    }

    @PostMapping("wxLogin")
    public void wechatLogin(HttpSession session, String callbackUrl) {

    }
    private void printNoDefaultImage(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.println("请在头像目录下放置默认头像default_avatar.jpg");
            writer.close();
        } catch (IOException e) {
            throw new BizException("输出默认头像失败");
        }
    }
}

