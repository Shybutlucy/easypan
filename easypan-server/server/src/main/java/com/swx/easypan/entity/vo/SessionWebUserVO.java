package com.swx.easypan.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SessionWebUserVO implements Serializable {
    private String nickname;
    private String id;
    private Boolean isAdmin;
    private String avatar;
}
