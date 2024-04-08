package com.swx.easypan.service.common;

import com.swx.easypan.entity.dto.FileUploadDTO;
import com.swx.easypan.entity.dto.UserSpaceDTO;
import com.swx.easypan.entity.vo.UploadResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户文件服务
 */
public interface UserFileService {

    /**
     * 获取用户使用空间
     *
     * @param id 用户ID
     */
    UserSpaceDTO getUseSpace(String id);

    UploadResultVO uploadFile(String userId, MultipartFile file, FileUploadDTO fileDTO) throws IOException;

    /**
     * 批量删除文件
     *
     * @param userId  用户ID
     * @param ids     需要删除的文件ID，逗号分隔
     * @param isAdmin 是否是管理员
     */
    void delFileBatch(String userId, String ids, Boolean isAdmin);
}
