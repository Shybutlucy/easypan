package com.swx.easypan.entity.query;
import lombok.Data;

/**
 * 文件信息参数
 */
@Data
public class FileInfoQuery {

    private Integer page = 1;
    private Integer limit = 20;
    private String orderBy = "folder_type DESC, update_time DESC";

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件MD5值
     */
    private String fileMd5;

    /**
     * 文件父ID
     */
    private String filePid;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件分类 1:视频 2:音频 3:图片 4:文档 5:其他
     */
    private Integer fileCategory;

    /**
     * 0:正常 1:回收站 2:删除
     */
    private Integer deleted;

}
