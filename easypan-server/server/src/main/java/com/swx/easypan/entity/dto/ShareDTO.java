package com.swx.easypan.entity.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ShareDTO implements Serializable {

    @NotNull
    private String fileId;
    private Integer validType;
    private String code;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getValidType() {
        return validType;
    }

    public void setValidType(Integer validType) {
        this.validType = validType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
