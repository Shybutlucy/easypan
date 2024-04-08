package com.swx.easypan.entity.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class LoginDTO {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String checkCode;
}
