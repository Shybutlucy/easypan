package com.swx.easypan.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {

    @Value("${spring.mail.username}")
    private String sendUsername;

    @Value("${admin.emails}")
    private String emails;

    @Value("${project.folder}")
    private String projectFolder;

    public String getSendUsername() {
        return sendUsername;
    }

    public String getEmails() {
        return emails;
    }
    public String getProjectFolder() {
        return projectFolder;
    }
}
