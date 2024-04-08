package com.swx.easypan.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        boolean createTime = metaObject.hasSetter("createTime");
        boolean updateTime = metaObject.hasSetter("updateTime");
        if (updateTime) {
            strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }
        if (createTime) {
            strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        boolean updateTime = metaObject.hasSetter("updateTime");
        if (updateTime) {
            strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }
    }
}
