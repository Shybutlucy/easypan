package com.swx.easypan.entity.dto;

import java.io.Serializable;

public class DownloadFileDTO implements Serializable {

    private String code;
    private String filename;
    private String filePath;

    public DownloadFileDTO() {
    }

    public DownloadFileDTO(String code, String filename, String filePath) {
        this.code = code;
        this.filename = filename;
        this.filePath = filePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
