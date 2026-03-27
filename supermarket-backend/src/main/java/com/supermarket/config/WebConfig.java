package com.supermarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 用于配置静态资源映射等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 从配置文件读取上传路径
    @Value("${supermarket.upload-path:./uploads/}")
    private String uploadPath;

    /**
     * 配置静态资源映射
     * 将 /uploads/** 请求映射到本地磁盘目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取绝对路径
        String absPath = new java.io.File(uploadPath).getAbsolutePath();

        // 确保路径以 / 结尾 (统一处理路径分隔符)
        if (!absPath.endsWith(java.io.File.separator)) {
            absPath += java.io.File.separator;
        }

        // 配置映射规则
        // addResourceHandler: 前端访问路径
        // addResourceLocations: 后端物理路径 (必须加 file: 前缀)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absPath);
    }
}
