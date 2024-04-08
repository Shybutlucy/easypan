package com.swx.easypan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swx.easypan.entity.dto.*;
import com.swx.easypan.entity.query.FileInfoQuery;
import com.swx.easypan.entity.vo.FileInfoVO;
import com.swx.easypan.pojo.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 文件信息表 服务类
 * </p>
 *
 * @author sw-code
 * @since 2023-05-19
 */
public interface FileInfoService extends IService<FileInfo> {

    IPage<FileInfoVO> pageInfo(Page<FileInfo> pageParam, FileInfoQuery query);

    /**
     * 查询用户使用的空间
     *
     * @param userId 用户ID
     */
    Long getUseSpace(String userId);

    /**
     * 保存文件信息
     *
     * @param userId  文件用户ID
     * @param fileId  文件ID
     * @param fileDTO 文件信息
     */
    boolean saveFileInfo(String userId, String fileId, FileUploadDTO fileDTO);

    /**
     * 从文件保存文件，即保存秒传文件
     *
     * @param userId  当前用户
     * @param fileId  当前文件ID
     * @param fileDTO 当前文件信息
     * @param dbFile  已存在文件信息
     */
    boolean saveFileInfoFromFile(String userId, String fileId, FileUploadDTO fileDTO, FileInfo dbFile);

    /**
     * 获取文件路径
     *
     * @param id     文件ID
     * @param userId 用户ID
     */
    String getFilePath(String id, String userId);

    /**
     * 新建目录
     *
     * @param folderDTO 目录信息
     * @param userId    用户ID
     */
    FileInfoVO newFolder(NewFolderDTO folderDTO, String userId);

    /**
     * 根据ids获取目录信息
     *
     * @param ids ids
     */
    List<FileInfoVO> listFolderByIds(String[] ids);

    /**
     * 文件重命名
     *
     * @param userId        用户ID
     * @param renameFileDTO 新文件信息
     */
    FileInfoVO rename(String userId, RenameFileDTO renameFileDTO);

    /**
     * 移动文件
     *
     * @param userId      用户ID
     * @param moveFileDTO 移动文件信息
     */
    void changeFileFolder(String userId, MoveFileDTO moveFileDTO);

    /**
     * 创建下载链接
     *
     * @param userId 用户ID
     * @param id     文件ID
     */
    String createDownloadUrl(String userId, String id);

    /**
     * 将文件移入回收站
     *
     * @param userId 用户ID
     * @param ids    文件IDS，逗号分隔
     */
    void removeFile2RecycleBatch(String userId, String ids);

    /**
     * 批量恢复文件
     *
     * @param userId 用户ID
     * @param ids    需要恢复的文件ID，逗号分隔
     */
    void recoverFileBatch(String userId, String ids);

    /**
     * 批量删除文件
     *
     * @param userId         用户ID
     * @param delFilePidList 根据pid删除
     * @param idList         根据id删除
     * @param oldDelFlag     旧状态
     */
    Boolean delFileBatch(String userId, List<String> delFilePidList, List<String> idList, Integer oldDelFlag);
}
