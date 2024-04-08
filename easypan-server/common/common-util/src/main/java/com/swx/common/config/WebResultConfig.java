package com.swx.common.config;

import com.swx.common.interceptor.ResponseResultInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResultConfig implements WebMvcConfigurer {

    // SpringMVC 需要手动添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        ResponseResultInterceptor interceptor = new ResponseResultInterceptor();
        registry.addInterceptor(interceptor);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}

