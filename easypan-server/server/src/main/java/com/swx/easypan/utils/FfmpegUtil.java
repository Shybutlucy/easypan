package com.swx.easypan.utils;

import com.swx.common.pojo.BizException;
import com.swx.easypan.entity.constants.Constants;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FfmpegUtil {
    private static final Logger logger = LoggerFactory.getLogger(FfmpegUtil.class);

    /**
     * 生成index.ts
     *
     * @param videoFilePath 视频路径
     * @param tsPath        ts文件路径
     */
    public static void transfer2ts(String videoFilePath, String tsPath) {
        // "ffmpeg -y -i %s -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s"
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-y");
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(videoFilePath);
            ffmpeg.addArgument("-vcodec");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-acodec");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-vbsf");
            ffmpeg.addArgument("h264_mp4toannexb");
            ffmpeg.addArgument(tsPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
        } catch (IOException e) {
            logger.error("ts文件生成失败", e);
            throw new BizException("视频转换失败");
        }
    }

    /**
     * 视频切片
     *
     * @param tsPath   ts文件路径
     * @param m3u8Path index.m3u8路径
     * @param tsFolder 切片文件目录路径
     * @param fileId   文件ID
     */
    public static void cutTs(String tsPath, String m3u8Path, String tsFolder, String fileId) {
        // "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts"
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(tsPath);
            ffmpeg.addArgument("-c");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-map");
            ffmpeg.addArgument("0");
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("segment");
            ffmpeg.addArgument("-segment_list");
            ffmpeg.addArgument(m3u8Path);
            ffmpeg.addArgument("-segment_time");
            ffmpeg.addArgument("30");
            ffmpeg.addArgument(String.format("%s/%s_%%4d.ts", tsFolder, fileId));
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
        } catch (IOException e) {
            logger.error("文件切片失败", e);
            throw new BizException("视频转换失败");
        }
    }


    /**
     * 获取视频缩略图
     *
     * @param sourceFile 视频文件地址
     * @param width      缩略图宽度
     * @param targetFile 缩略图地址
     */
    public static void createTargetThumbnail(File sourceFile, Integer width, File targetFile) {
        // "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s"
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(sourceFile.getAbsoluteFile().toString());
            ffmpeg.addArgument("-y");
            ffmpeg.addArgument("-vframes");
            ffmpeg.addArgument("1");
            ffmpeg.addArgument("-vf");
            ffmpeg.addArgument(String.format("scale=%d:%d/a", width, width));
            ffmpeg.addArgument(targetFile.getAbsoluteFile().toString());
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
        } catch (IOException e) {
            logger.error("生成封面失败", e);
            throw new BizException("生成封面失败");
        }
    }

    public static Boolean createThumbnailWidthFFmpeg(File sourceFile, Integer width, File targetFile, Boolean delSource) {
        try {
            BufferedImage src = ImageIO.read(sourceFile);
            int sourceW = src.getWidth();
            // 小于指定高宽不要压缩
            if (sourceW < width) {
                return false;
            }
            compressImage(sourceFile, width, targetFile, delSource);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 压缩图片，获取图片缩略图
     *
     * @param sourceFile 源图片地址
     * @param width      图片压缩宽度
     * @param targetFile 缩略图
     * @param delSource  是否删除原图
     */
    public static void compressImage(File sourceFile, Integer width, File targetFile, Boolean delSource) {
        // String cmd = "ffmpeg -i %s -vf scale=%d:-1 %s -y"
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(sourceFile.getAbsoluteFile().toString());
            ffmpeg.addArgument("-vf");
            ffmpeg.addArgument(String.format("scale=%d:-1", width));
            ffmpeg.addArgument(targetFile.getAbsolutePath());
            ffmpeg.addArgument("-y");
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            if (delSource) {
                FileUtils.deleteDirectory(sourceFile);
            }
        } catch (IOException e) {
            logger.error("压缩图片失败", e);
            throw new BizException("生成封面失败");
        }
    }

    /**
     * 等待命令执行成功，退出
     *
     * @param br
     * @throws IOException
     */
    private static void blockFfmpeg(BufferedReader br) throws IOException {
        String line;
        // 该方法阻塞线程，直至合成成功
        while ((line = br.readLine()) != null) {
            doNothing(line);
        }
    }

    private static void doNothing(String line) {
        // logger.info("ffmpeg命令执行中————{}", line);
    }

}
