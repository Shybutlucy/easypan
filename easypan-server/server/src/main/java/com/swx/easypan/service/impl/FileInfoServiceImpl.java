package com.swx.easypan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swx.common.pojo.BizException;
import com.swx.common.pojo.ResultCode;
import com.swx.easypan.entity.config.AppConfig;
import com.swx.easypan.entity.constants.Constants;
import com.swx.easypan.entity.dto.*;
import com.swx.easypan.entity.enums.*;
import com.swx.easypan.entity.query.FileInfoQuery;
import com.swx.easypan.entity.vo.FileInfoVO;
import com.swx.easypan.pojo.FileInfo;
import com.swx.easypan.mapper.FileInfoMapper;
import com.swx.easypan.redis.RedisComponent;
import com.swx.easypan.service.FileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swx.easypan.utils.FfmpegUtil;
import com.swx.easypan.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件信息表 服务实现类
 * </p>
 *
 * @author sw-code
 * @since 2023-05-19
 */
@Slf4j
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    private final AppConfig appConfig;
    private final RedisComponent redisComponent;

    public FileInfoServiceImpl(AppConfig appConfig, RedisComponent redisComponent) {
        this.appConfig = appConfig;
        this.redisComponent = redisComponent;
    }

    @Override
    public IPage<FileInfoVO> pageInfo(Page<FileInfo> pageParam, FileInfoQuery query) {
        IPage<FileInfo> iPage = baseMapper.selectPageInfo(pageParam, query);
        List<FileInfo> records = iPage.getRecords();
        List<FileInfoVO> fileInfoVOS = records.stream().map(item -> {
            FileInfoVO fileInfoVO = new FileInfoVO();
            BeanUtils.copyProperties(item, fileInfoVO);
            return fileInfoVO;
        }).collect(Collectors.toList());
        IPage<FileInfoVO> page = new Page<FileInfoVO>(pageParam.getCurrent(), pageParam.getSize(), iPage.getTotal());
        page.setRecords(fileInfoVOS);
        return page;
    }

    /**
     * 查询用户使用的空间
     *
     * @param userId 用户ID
     */
    @Override
    public Long getUseSpace(String userId) {
        return baseMapper.selectUseSpace(userId);
    }

    @Override
    public boolean saveFileInfo(String userId, String fileId, FileUploadDTO fileDTO) {
        String month = DateFormatUtils.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
        String fileSuffix = StringTools.getFileSuffix(fileDTO.getFilename());
        FileTypeEnums fileTypeEnums = FileTypeEnums.getBySuffix(fileSuffix);
        String realFileName = userId + fileId + fileSuffix;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(fileId);
        fileInfo.setUserId(userId);
        fileInfo.setFileMd5(fileDTO.getFileMd5());
        fileInfo.setFilename(autoRename(fileDTO.getFilePid(), userId, fileDTO.getFilename()));
        fileInfo.setFilePath(month + "/" + realFileName);
        fileInfo.setFilePid(fileDTO.getFilePid());
        fileInfo.setFileCategory(fileTypeEnums.getCategory().getCategory());
        fileInfo.setFileType(fileTypeEnums.getType());
        fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());
        fileInfo.setFolderType(FileFolderTypeEnums.FILE.getType());
        return save(fileInfo);
    }

    @Override
    public boolean saveFileInfoFromFile(String userId, String fileId, FileUploadDTO fileDTO, FileInfo dbFile) {
        dbFile.setId(fileId);
        dbFile.setFilePid(fileDTO.getFilePid());
        dbFile.setUserId(userId);
        dbFile.setStatus(FileStatusEnums.USING.getStatus());
        dbFile.setFileMd5(fileDTO.getFileMd5());
        dbFile.setDeleted(FileDelFlagEnums.USING.getFlag());
        dbFile.setFilename(autoRename(fileDTO.getFilePid(), userId, fileDTO.getFilename()));
        return save(dbFile);
    }

    /**
     * 获取文件路径
     *
     * @param id     文件ID
     * @param userId 用户ID
     */
    @Override
    public String getFilePath(String id, String userId) {
        String filePath = null;
        if (id.endsWith(".ts")) {
            String[] tsArray = id.split("_");
            String realFileId = tsArray[0];
            FileInfo fileInfo = this.getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getId, realFileId).eq(FileInfo::getUserId, userId));
            String fileName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            String folderPath = StringTools.getFilename(fileName);
            filePath = folderPath + "/" + id;
        } else {
            FileInfo fileInfo = this.getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getId, id).eq(FileInfo::getUserId, userId));
            if (null == fileInfo) {
                throw new BizException("文件不存在");
            }
            if (FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                String fileName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
                String folderPath = StringTools.getFilename(fileName);
                filePath = folderPath + "/" + Constants.M3U8_NAME;
            } else {
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }
        }
        return filePath;
    }

    @Override
    public FileInfoVO newFolder(NewFolderDTO folderDTO, String userId) {
        // 校验文件夹名
        String rename = autoRename(folderDTO.getFilePid(), userId, folderDTO.getFilename());
        // 构造属性
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(StringTools.getRandomString(Constants.LENGTH_10));
        fileInfo.setUserId(userId);
        fileInfo.setFilename(rename);
        fileInfo.setFilePid(folderDTO.getFilePid());
        fileInfo.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        fileInfo.setStatus(FileStatusEnums.USING.getStatus());
        // 保存
        this.save(fileInfo);
        // 返回
        FileInfoVO fileInfoVO = new FileInfoVO();
        BeanUtils.copyProperties(fileInfo, fileInfoVO);
        return fileInfoVO;
    }

    /**
     * 根据ids获取目录信息
     *
     * @param ids ids
     */
    @Override
    public List<FileInfoVO> listFolderByIds(String[] ids) {
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(FileInfo::getId, FileInfo::getFilename).in(FileInfo::getId, Arrays.asList(ids)).last("order by field(id, \"" + StringUtils.join(ids, "\",\"") + "\")");
        List<FileInfo> list = list(wrapper);
        List<FileInfoVO> fileInfoVOS = list.stream().map(item -> {
            FileInfoVO fileInfoVO = new FileInfoVO();
            BeanUtils.copyProperties(item, fileInfoVO);
            return fileInfoVO;
        }).collect(Collectors.toList());
        return fileInfoVOS;
    }

    /**
     * 文件重命名
     *
     * @param userId        用户ID
     * @param renameFileDTO 新文件信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfoVO rename(String userId, RenameFileDTO renameFileDTO) {
        FileInfo fileInfo = getOne(new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getId, renameFileDTO.getId())
                .eq(FileInfo::getDeleted, FileDelFlagEnums.USING.getFlag())
                .eq(FileInfo::getUserId, userId));
        if (null == fileInfo) {
            throw new BizException("文件不存在");
        }
        String filename = renameFileDTO.getFilename();
        if (FileFolderTypeEnums.FILE.getType().equals(fileInfo.getFolderType())) {
            filename = filename + StringTools.getFileSuffix(fileInfo.getFilename());
        }
        String rename = autoRename(fileInfo.getFilePid(), userId, filename);
        FileInfo dbFile = new FileInfo();
        dbFile.setFilename(rename);
        Boolean update = updateByMultiId(dbFile, renameFileDTO.getId(), userId);
        if (update) {
            fileInfo.setFilename(rename);
            fileInfo.setUpdateTime(LocalDateTime.now());
        }
        FileInfoVO fileInfoVO = new FileInfoVO();
        BeanUtils.copyProperties(fileInfo, fileInfoVO);
        return fileInfoVO;
    }

    /**
     * 移动文件
     *
     * @param userId      用户ID
     * @param moveFileDTO 移动文件信息
     */
    @Override
    public void changeFileFolder(String userId, MoveFileDTO moveFileDTO) {
        String filePid = moveFileDTO.getFilePid();
        String ids = moveFileDTO.getIds();
        if (ids.equals(filePid)) {
            throw new BizException("不能将文件移动到自身目录下");
        }
        if (!Constants.ZERO_STR.equals(filePid)) {
            // 不在根目录
            FileInfo fileInfo = getByMultiId(filePid, userId);
            if (null == fileInfo || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDeleted())) {
                throw new BizException("正在尝试非法移动");
            }
        }
        // 查询Pid下的文件
        List<FileInfo> dbFile = list(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFilePid, filePid).eq(FileInfo::getUserId, userId));
        Map<String, FileInfo> dbFilenameMap = dbFile.stream().collect(Collectors.toMap(FileInfo::getFilename, Function.identity(), (a, b) -> b));
        // 查询选中的文件
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getUserId, userId)
                .in(StringUtils.isNotEmpty(ids), FileInfo::getId, Arrays.asList(ids.split(",")));
        List<FileInfo> selectFileList = list(wrapper);

        // 将所选文件重命名
        for (FileInfo item : selectFileList) {
            FileInfo rootFileInfo = dbFilenameMap.get(item.getFilename());
            // 文件名已存在，重命名被还原的文件名
            FileInfo updateInfo = new FileInfo();
            if (null != rootFileInfo) {
                String rename = StringTools.rename(item.getFilename());
                updateInfo.setFilename(rename);
            }
            updateInfo.setFilePid(filePid);
            updateByMultiId(updateInfo, item.getId(), userId);
        }
    }

    /**
     * 创建下载链接
     *
     * @param userId 用户ID
     * @param id     文件ID
     */
    @Override
    public String createDownloadUrl(String userId, String id) {
        // 防小人
        FileInfo fileInfo = getByMultiId(id, userId);
        if (fileInfo == null) {
            throw new BizException(ResultCode.PARAM_IS_INVALID);
        }
        if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
            throw new BizException(ResultCode.PARAM_IS_INVALID);
        }
        String code = StringTools.getRandomString(Constants.LENGTH_50);
        DownloadFileDTO fileDTO = new DownloadFileDTO();
        fileDTO.setCode(code);
        fileDTO.setFilename(fileInfo.getFilename());
        fileDTO.setFilePath(fileInfo.getFilePath());
        // 保存到Redis
        redisComponent.saveDownloadCode(code, fileDTO);
        return code;
    }

    /**
     * 将文件移入回收站
     *
     * @param userId 用户ID
     * @param ids    文件IDS，逗号分隔
     */
    @Override
    public void removeFile2RecycleBatch(String userId, String ids) {
        // 查询文件是否已经在回收站
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getUserId, userId)
                .eq(FileInfo::getDeleted, FileDelFlagEnums.USING.getFlag())
                .in(StringUtils.isNotEmpty(ids), FileInfo::getId, Arrays.asList(ids.split(",")));
        List<FileInfo> dbFileList = list(wrapper);
        if (dbFileList.isEmpty()) {
            return;
        }
        // 递归删除文件
        ArrayList<String> delFilePidList = new ArrayList<>();
        for (FileInfo fileInfo : dbFileList) {
            findAllSubFolderList(delFilePidList, userId, fileInfo.getId(), FileDelFlagEnums.USING.getFlag());
        }
        if (!delFilePidList.isEmpty()) {
            // 目录下的文件，非选中文件直接删除，即deleted置为2
            FileInfo delFileInfo = new FileInfo();
            delFileInfo.setDeleted(FileDelFlagEnums.DEL.getFlag());
            // 根据Pid删除文件，即删除目录下的所有文件
            baseMapper.updateFileDelFlagBatch(delFileInfo, userId, delFilePidList, null, FileDelFlagEnums.USING.getFlag());
        }
        // 将选中的文件更新为回收站，即deleted置为1
        List<String> recIds = Arrays.asList(ids.split(","));
        FileInfo recFileInfo = new FileInfo();
        recFileInfo.setDeleted(FileDelFlagEnums.RECYCLE.getFlag());
        recFileInfo.setRecoveryTime(LocalDateTime.now());
        baseMapper.updateFileDelFlagBatch(recFileInfo, userId, null, recIds, FileDelFlagEnums.USING.getFlag());
    }

    // 递归查找当前目录下的子目录
    public void findAllSubFolderList(List<String> idList, String userId, String id, Integer delFlag) {
        idList.add(id);
        // 查找当前目录下的所有子目录
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getUserId, userId)
                .eq(FileInfo::getFilePid, id)
                .eq(FileInfo::getDeleted, delFlag)
                .eq(FileInfo::getFolderType, FileFolderTypeEnums.FOLDER.getType());
        List<FileInfo> fileInfoList = list(wrapper);
        for (FileInfo fileInfo : fileInfoList) {
            findAllSubFolderList(idList, userId, fileInfo.getId(), delFlag);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverFileBatch(String userId, String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getUserId, userId)
                .eq(FileInfo::getDeleted, FileDelFlagEnums.RECYCLE.getFlag())
                .in(!ids.isEmpty(), FileInfo::getId, idList);
        List<FileInfo> fileInfoList = list(wrapper);
        ArrayList<String> delFileSubFolderFileIdList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
                // 查询目录下的所有文件
                findAllSubFolderList(delFileSubFolderFileIdList, userId, fileInfo.getId(), FileDelFlagEnums.DEL.getFlag());
            }
        }
        // 查询所有根目录的文件
        List<FileInfo> rootFileList = list(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getUserId, userId)
                .eq(FileInfo::getDeleted, FileDelFlagEnums.USING.getFlag())
                .eq(FileInfo::getFilePid, Constants.ZERO_STR));
        Map<String, FileInfo> rootFileMap = rootFileList.stream().collect(Collectors.toMap(FileInfo::getFilename, Function.identity(), (a, b) -> b));
        // 将目录下所有删除的文件更新为使用中
        if (!delFileSubFolderFileIdList.isEmpty()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setDeleted(FileDelFlagEnums.USING.getFlag());
            baseMapper.updateFileDelFlagBatch(fileInfo, userId, delFileSubFolderFileIdList, null, FileDelFlagEnums.DEL.getFlag());
        }
        // 将所选文件更新为正常，且父级目录设置为根目录
        FileInfo fileInfo = new FileInfo();
        fileInfo.setDeleted(FileDelFlagEnums.USING.getFlag());
        fileInfo.setFilePid(Constants.ZERO_STR);
        fileInfo.setUpdateTime(LocalDateTime.now());
        baseMapper.updateFileDelFlagBatch(fileInfo, userId, null, idList, FileDelFlagEnums.RECYCLE.getFlag());

        // 将所选文件重命名
        for (FileInfo info : fileInfoList) {
            FileInfo rootFileInfo = rootFileMap.get(info.getFilename());
            // 文件名已存在，重命名
            if (null != rootFileInfo) {
                FileInfo updateInfo = new FileInfo();
                updateInfo.setFilename(StringTools.rename(info.getFilename()));
                updateByMultiId(updateInfo, info.getId(), userId);
            }
        }
    }

    @Override
    public Boolean delFileBatch(String userId, List<String> delFilePidList, List<String> idList, Integer oldDelFlag) {
        Integer deleted = baseMapper.delFileBatch(userId, delFilePidList, idList, oldDelFlag);
        return deleted != null && deleted > 0;
    }

    @Async
    public void transferFile(String fileId, String userId) {
        boolean transferSuccess = true;
        String targetFilePath = null;
        String cover = null;
        FileInfo fileInfo = getByMultiId(fileId, userId);
        if (fileInfo == null || !FileStatusEnums.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
            return;
        }
        try {
            // 临时目录
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = userId + fileId;
            File fileFolder = new File(tempFolderName + currentUserFolderName);
            String fileSuffix = StringTools.getFileSuffix(fileInfo.getFilename());
            String month = fileInfo.getCreateTime().format(DateTimeFormatter.ofPattern(DateTimePatternEnum.YYYYMM.getPattern()));
            // 目标目录
            String targetFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
            File targetFolder = new File(targetFolderName + "/" + month);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
            // 真实文件名
            String realFilename = currentUserFolderName + fileSuffix;
            targetFilePath = targetFolder.getPath() + "/" + realFilename;
            // 合并文件
            union(fileFolder.getPath(), targetFilePath, fileInfo.getFilename(), true);
            // 视频文件切割
            Integer fileType = fileInfo.getFileType();
            // 文件缩略图
            if (FileTypeEnums.VIDEO.getType().equals(fileType)) {
                cutFile4Video(fileId, targetFilePath);
                // 视频缩略图
                cover = month + "/" + currentUserFolderName + Constants.IMAGE_PNG_SUFFIX;
                String coverPath = targetFolderName + "/" + cover;
                FfmpegUtil.createTargetThumbnail(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath));
            } else if (FileTypeEnums.IMAGE.getType().equals(fileType)) {
                // 图片缩略图
                cover = month + "/" + realFilename.replace(".", "_.");
                String coverPath = targetFolderName + "/" + cover;
                Boolean created = FfmpegUtil.createThumbnailWidthFFmpeg(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath), false);
                if (!created) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        } catch (Exception e) {
            log.error("文件转码失败， 文件ID: {}, userId: {}", fileId, userId, e);
            transferSuccess = false;
        } finally {
            // 更新文件Size和封面
            FileInfo updateInfo = new FileInfo();
            if (targetFilePath == null) {
                updateInfo.setFileSize(0L);
            } else {
                updateInfo.setFileSize(new File(targetFilePath).length());
            }
            updateInfo.setFileCover(cover);
            updateInfo.setStatus(transferSuccess ? FileStatusEnums.USING.getStatus() : FileStatusEnums.TRANSFER_FAIL.getStatus());
            updateByMultiId(updateInfo, fileId, userId);
        }
    }

    /**
     * 多主键更新
     *
     * @param id     文件ID
     * @param userId 用户ID
     */
    private Boolean updateByMultiId(FileInfo fileInfo, String id, String userId) {
        return update(fileInfo, new LambdaUpdateWrapper<FileInfo>().eq(FileInfo::getId, id).eq(FileInfo::getUserId, userId));
    }

    /**
     * 多主键查询
     *
     * @param id     文件ID
     * @param userId 用户ID
     */
    private FileInfo getByMultiId(String id, String userId) {
        return getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getId, id).eq(FileInfo::getUserId, userId));
    }

    /**
     * 文件合并
     *
     * @param dirPath    分片所在目录
     * @param toFilePath 合并目标文件
     * @param filename   合并文件名
     * @param delSource  是否删除分片文件
     */
    private void union(String dirPath, String toFilePath, String filename, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BizException("目录不存在");
        }
        File[] files = dir.listFiles();
        File targetFile = new File(toFilePath);
        RandomAccessFile writeFile = null;
        try {
            writeFile = new RandomAccessFile(targetFile, "rw");
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < files.length; i++) {
                int len = -1;
                File chunkFile = new File(dirPath + "/" + i);
                try (RandomAccessFile readFile = new RandomAccessFile(chunkFile, "r")) {
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败", e);
                    throw new BizException("合并分片失败");
                }
            }
        } catch (Exception e) {
            log.error("合并文件:{}失败", filename, e);
            throw new BizException("合并文件" + filename + "出错了");
        } finally {
            if (null != writeFile) {
                try {
                    writeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (delSource && dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 当文件名字相同时，重命名文件
     *
     * @param filePid  文件PID
     * @param userId   用户ID
     * @param filename 文件名
     */
    private String autoRename(String filePid, String userId, String filename) {
        int count = this.count(new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getUserId, userId)
                .eq(FileInfo::getFilePid, filePid)
                .eq(FileInfo::getDeleted, FileDelFlagEnums.USING.getFlag())
                .eq(FileInfo::getFilename, filename));
        if (count > 0) {
            filename = StringTools.rename(filename);
        }
        return filename;
    }

    /**
     * 视频文件切片
     *
     * @param fileId        文件ID
     * @param videoFilePath 分片到目录
     */
    private void cutFile4Video(String fileId, String videoFilePath) {
        // 创建同名切片目录
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        // 生成ts
        String tsPath = tsFolder + "/" + Constants.TS_NAME;
        FfmpegUtil.transfer2ts(videoFilePath, tsPath);
        // 生成索引文件.m3u8和切片.ts文件
        String indexTs = tsFolder.getPath() + "/" + Constants.M3U8_NAME;
        FfmpegUtil.cutTs(tsPath, indexTs, tsFolder.getPath(), fileId);
        // 删除index.ts
        new File(tsPath).delete();
    }
}
