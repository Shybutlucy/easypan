package com.swx.easypan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swx.easypan.entity.vo.FileShareVo;
import com.swx.easypan.pojo.FileShare;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sw-code
 * @since 2023-07-14
 */
public interface FileShareService extends IService<FileShare> {

    IPage<FileShareVo> pageInfo(Page<FileShare> pageParam, String userId);

    FileShare saveShare(FileShare fileShare);

    void deleteShareBatch(String shareIds, String userId);
}
