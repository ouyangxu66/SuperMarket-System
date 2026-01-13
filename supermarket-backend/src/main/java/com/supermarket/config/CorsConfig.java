package com.supermarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类 (CORS Configuration)
 *
 * 作用：
 * 解决前后端分离架构中，前端（如 localhost:5173）访问后端（localhost:8080）时的跨域限制问题。
 * 如果不配置此类，浏览器会拦截前端的请求并报 "CORS error"。
 */
@Configuration // 标识这是一个配置类，Spring 启动时会自动加载
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 重写 addCorsMappings 方法，配置跨域规则
     *
     * @param registry CORS 注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加映射路径，"/**" 表示拦截所有的 HTTP 请求
        registry.addMapping("/**")
                // 允许跨域请求的来源
                // 在开发阶段，允许所有来源 (Patterns: "*")
                // 生产环境建议修改为具体的前端域名，如 "http://supermarket.com"
                .allowedOriginPatterns("*")
                // 允许是否发送 Cookie 信息
                .allowCredentials(true)
                // 允许的请求方式 (GET, POST, PUT, DELETE, OPTIONS)
                // OPTIONS 请求是浏览器发出的预检请求，用于探测接口是否可用
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头，"*" 表示允许所有请求头 (如 Authorization, Content-Type)
                .allowedHeaders("*")
                // 跨域允许时间 (秒)，在 1 小时内不需要再次发送预检请求
                .maxAge(3600);
    }
}