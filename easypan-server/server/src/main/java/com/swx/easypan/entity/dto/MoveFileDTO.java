package com.swx.easypan.entity.dto;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class MoveFileDTO implements Serializable {


    @NotEmpty
    private String filePid; // 目标目录ID
    @NotEmpty
    private String ids; // 要移动的文件IDs

    public MoveFileDTO() {
    }

    public MoveFileDTO(String filePid, String ids) {
        this.filePid = filePid;
        this.ids = ids;
    }

    public String getFilePid() {
        return filePid;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
