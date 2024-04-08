package com.swx.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
public class FileUtil {

    /**
     * 写文件到response
     *
     * @param response response
     * @param filePath 文件路径
     */
    public static void readFile(HttpServletResponse response, String filePath) {
        if (!StringUtils.hasLength(filePath)) {
            return;
        }
        if (filePath.contains("../") || filePath.contains("..\\")) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        FileInputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("IO异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("IO异常", e);
                }
            }
        }
    }
}
