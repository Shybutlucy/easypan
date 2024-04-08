package com.swx.easypan.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class FileUploadDTO {

    private String id;

    @NotEmpty
    private String filename;

    private String filePid;

    @NotEmpty
    private String fileMd5;

    @NotNull
    private Integer chunkIndex;

    @NotNull
    private Integer chunks;
}
