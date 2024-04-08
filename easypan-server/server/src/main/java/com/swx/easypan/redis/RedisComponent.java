package com.swx.easypan.redis;

import com.swx.easypan.entity.constants.Constants;
import com.swx.easypan.entity.dto.DownloadFileDTO;
import com.swx.easypan.entity.dto.SysSettingsDTO;
import com.swx.easypan.entity.dto.UserSpaceDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("redisComponent")
public class RedisComponent {

    @Resource
    private RedisUtils<Object> redisUtils;

    public SysSettingsDTO getSysSettingDto() {
        SysSettingsDTO sysSettingsDto = (SysSettingsDTO) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (null == sysSettingsDto) {
            sysSettingsDto = new SysSettingsDTO();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    public void saveUserSpaceUse(String userId, UserSpaceDTO userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRE_ONE_DAY);
    }

    public Long getInitSpace() {
        return getSysSettingDto().getUserInitSpace() * Constants.MB;
    }

    public UserSpaceDTO getUserSpaceUse(String userId) {
        return (UserSpaceDTO) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
    }

    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        Long currentSize = getFileTempSize(userId, fileId);
        String key = Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId;
        redisUtils.setex(key, currentSize + fileSize, Constants.REDIS_KEY_EXPIRE_ONE_HOUR);
    }

    // 获取临时文件大小
    public Long getFileTempSize(String userId, String fileId) {
        Object sizeObj = redisUtils.get(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
        if (sizeObj == null) {
            return 0L;
        }
        if (sizeObj instanceof Integer) {
            return ((Integer) sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            return (Long) sizeObj;
        }
        return 0L;
    }

    // 保存下载文件信息
    public void saveDownloadCode(String code, DownloadFileDTO fileDTO) {
        redisUtils.setex(Constants.REDIS_KEY_DOWNLOAD + code, fileDTO, Constants.REDIS_KEY_EXPIRE_FIVE_MIN);
    }

    // 获取下载文件信息
    public DownloadFileDTO getDownloadCode(String code) {
        return (DownloadFileDTO) redisUtils.get(Constants.REDIS_KEY_DOWNLOAD + code);
    }
}
