package com.swx.easypan.utils;


import com.swx.common.utils.FileUtil;
import com.swx.easypan.entity.constants.Constants;
import com.swx.easypan.entity.enums.FileCategoryEnums;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileUtils {

    /**
     * response写入文件资源
     *
     * @param response response
     * @param filePath 文件路径
     */
    public static void writeImage(HttpServletResponse response, String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return;
        }
        String imageSuffix = StringTools.getFileSuffix(filePath);
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        FileUtil.readFile(response, filePath);
    }

    /**
     * 写入下载文件
     *
     * @param response response
     * @param request  request
     * @param filename 文件名
     * @param filePath 文件路径
     */
    public static void writeDownloadFile(HttpServletResponse response, HttpServletRequest request, String filename, String filePath) throws UnsupportedEncodingException {
        response.setContentType("application/x-msdownload; character=UTF-8");
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "UTF-8");
        } else {
            filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        FileUtil.readFile(response, filePath);
    }
}
