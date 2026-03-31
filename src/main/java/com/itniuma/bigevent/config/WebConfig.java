package com.itniuma.bigevent.config;

import com.itniuma.bigevent.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author aceFelix
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 注入拦截器对象
    @Autowired
    private LoginInterceptor loginInterceptor;
    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求，除了登录和注册
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/**",
                        "/error"
                );
    }
}
