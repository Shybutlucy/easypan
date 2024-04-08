package com.swx.easypan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swx.common.annotation.ResponseResult;
import com.swx.common.utils.FileUtil;
import com.swx.easypan.annotation.LoginValidator;
import com.swx.easypan.entity.config.AppConfig;
import com.swx.easypan.entity.constants.Constants;
import com.swx.easypan.entity.dto.*;
import com.swx.easypan.entity.enums.FileCategoryEnums;
import com.swx.easypan.entity.enums.FileDelFlagEnums;
import com.swx.easypan.entity.enums.FileFolderTypeEnums;
import com.swx.easypan.entity.query.FileInfoQuery;
import com.swx.easypan.entity.vo.FileInfoVO;
import com.swx.easypan.entity.vo.SessionWebUserVO;
import com.swx.easypan.entity.vo.UploadResultVO;
import com.swx.easypan.pojo.FileInfo;
import com.swx.easypan.redis.RedisComponent;
import com.swx.easypan.service.FileInfoService;
import com.swx.easypan.service.common.UserFileService;
import com.swx.easypan.utils.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件信息表 前端控制器
 * </p>
 *
 * @author sw-code
 * @since 2023-05-19
 */
@RestController
@RequestMapping("/file")
@ResponseResult
@LoginValidator
@Validated
public class FileInfoController {

    private final FileInfoService fileInfoService;
    private final UserFileService userFileService;
    private final RedisComponent redisComponent;
    private final AppConfig appConfig;

    public FileInfoController(FileInfoService fileInfoService, UserFileService userFileService, RedisComponent redisComponent, AppConfig appConfig) {
        this.fileInfoService = fileInfoService;
        this.userFileService = userFileService;
        this.redisComponent = redisComponent;
        this.appConfig = appConfig;
    }

    // 根据category，加载所有数据
    @GetMapping("/loadDataList")
    public IPage<FileInfoVO> loadDataList(HttpSession session,
                                          FileInfoQuery query,
                                          String category) {
        FileCategoryEnums categoryEnums = FileCategoryEnums.getByCode(category);
        if (null != categoryEnums) {
            query.setFileCategory(categoryEnums.getCategory());
        }
        Page<FileInfo> pageParam = new Page<>(query.getPage(), query.getLimit());
        query.setUserId(((SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY)).getId());
        query.setDeleted(FileDelFlagEnums.USING.getFlag());
        return fileInfoService.pageInfo(pageParam, query);
    }

    // 上传文件
    @PostMapping("/uploadFile")
    public UploadResultVO uploadFile(HttpSession session,
                                     MultipartFile file,
                                     FileUploadDTO fileDTO) throws IOException {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        return userFileService.uploadFile(user.getId(), file, fileDTO);
    }

    // 获取文件封面
    @GetMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                         @PathVariable("imageFolder") @NotNull String imageFolder,
                         @PathVariable("imageName") @NotNull String imageName) {
        String imagePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        FileUtils.writeImage(response, imagePath);
    }

    // 获取视频文件
    @GetMapping("/ts/getVideoInfo/{id}")
    public void getVideoInfo(HttpSession session,
                             HttpServletResponse response,
                             @PathVariable("id") @NotBlank String id) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        String filePath = fileInfoService.getFilePath(id, user.getId());
        FileUtil.readFile(response, filePath);
    }

    // 获取文件
    @GetMapping("/getFile/{id}")
    public void getFile(HttpSession session,
                        HttpServletResponse response,
                        @PathVariable("id") @NotBlank String id) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        String filePath = fileInfoService.getFilePath(id, user.getId());
        FileUtil.readFile(response, filePath);
    }

    // 新建文件夹
    @PostMapping("/newFolder")
    public FileInfoVO newFolder(HttpSession session, @RequestBody NewFolderDTO folderDTO) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        return fileInfoService.newFolder(folderDTO, user.getId());
    }

    // 获取目录信息
    @GetMapping("/getFolderInfo")
    public List<FileInfoVO> getFolderInfo(HttpSession session, @NotEmpty String path) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        String[] ids = path.split("/");
        return fileInfoService.listFolderByIds(ids);
    }

    // 文件重命名
    @PutMapping("/rename")
    public FileInfoVO rename(HttpSession session, @RequestBody RenameFileDTO renameFileDTO) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        return fileInfoService.rename(user.getId(), renameFileDTO);
    }

    // 获取所有目录
    @GetMapping("/loadAllFolder")
    public List<FileInfoVO> loadAllFolder(HttpSession session,
                                          @NotEmpty String filePid) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getFilePid, filePid)
                .eq(FileInfo::getUserId, user.getId())
                .eq(FileInfo::getFolderType, FileFolderTypeEnums.FOLDER.getType());
        List<FileInfo> list = fileInfoService.list(wrapper);
        return list.stream().map(item -> {
            FileInfoVO fileInfoVO = new FileInfoVO();
            BeanUtils.copyProperties(item, fileInfoVO);
            return fileInfoVO;
        }).collect(Collectors.toList());
    }

    // 移动文件
    @PutMapping("/changeFileFolder")
    public void changeFileFolder(HttpSession session, @RequestBody MoveFileDTO moveFileDTO) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        fileInfoService.changeFileFolder(user.getId(), moveFileDTO);
    }

    // 创建下载链接
    @GetMapping("/createDownloadUrl/{id}")
    public Map<String, String> createDownloadUrl(HttpSession session, @PathVariable("id") @NotEmpty String id) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        // 需要校验登陆状态
        String code = fileInfoService.createDownloadUrl(user.getId(), id);
        HashMap<String, String> data = new HashMap<>();
        data.put("code", code);
        return data;
    }

    // 下载文件，无需登陆
    @LoginValidator(validated = false)
    @GetMapping("/download/{code}")
    public void download(HttpServletRequest request,
                         HttpServletResponse response,
                         @PathVariable @NotEmpty String code) throws UnsupportedEncodingException {
        DownloadFileDTO fileDTO = redisComponent.getDownloadCode(code);
        if (null == fileDTO) {
            return;
        }
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileDTO.getFilePath();
        String filename = fileDTO.getFilename();
        FileUtils.writeDownloadFile(response, request, filename, filePath);
    }

    // 删除文件
    @DeleteMapping("/delFile/{ids}")
    public void delFile(HttpSession session, @PathVariable("ids") @NotEmpty String ids) {
        SessionWebUserVO user = (SessionWebUserVO) session.getAttribute(Constants.SESSION_KEY);
        // 需要校验登陆状态
        fileInfoService.removeFile2RecycleBatch(user.getId(), ids);
    }

}

