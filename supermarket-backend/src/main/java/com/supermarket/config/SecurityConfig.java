package com.supermarket.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 密码加密器
     * Spring Security 要求存入数据库的密码必须加密。
     * BCrypt 是一种强大的哈希算法。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 暴露 AuthenticationManager Bean
     * 登录接口需要用它来验证账号密码
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 核心过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF (前后端分离项目不需要，因为不使用 Session)
                .csrf().disable()

                // 2. 允许跨域 (使用我们在 CorsConfig 中的配置)
                .cors()
                .and()

                // 3. 设置 Session 管理策略为无状态 (Stateless)
                // 因为我们用 JWT，服务器不存 Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 4. 请求权限配置
                .authorizeRequests()
                // 放行登录接口，允许匿名访问
                .antMatchers("/auth/login").permitAll()
                // 放行静态资源 (如果需要)
                .antMatchers("/static/**").permitAll()
                // 其他所有请求都需要认证 (登录后才能访问)
                .anyRequest().authenticated();

        // 5. 将我们的 JWT 过滤器添加到 UsernamePasswordAuthenticationFilter 之前
        // 这样请求进来会先检查 Token
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}