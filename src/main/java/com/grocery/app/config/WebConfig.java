package com.grocery.app.config;

import com.grocery.app.utils.ImageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.grocery.app.config.constant.AppConstants.USER_URLS;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ImageInterceptor()).addPathPatterns(USER_URLS[2]);
    }
}
