package com.swx.easypan.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@Data
public class UserSpaceDTO implements Serializable {
    private Long useSpace;
    private Long totalSpace;

    public UserSpaceDTO() {
    }

    public UserSpaceDTO(Long useSpace, Long totalSpace) {
        this.useSpace = useSpace;
        this.totalSpace = totalSpace;
    }
}
