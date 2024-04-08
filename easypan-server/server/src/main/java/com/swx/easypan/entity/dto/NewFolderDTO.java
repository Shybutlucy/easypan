package com.swx.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewFolderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String filePid;
    @NotEmpty
    private String filename;

    public String getFilePid() {
        return filePid;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
