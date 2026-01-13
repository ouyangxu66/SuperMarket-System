package com.supermarket.config;

import com.supermarket.auth.service.impl.UserDetailsServiceImpl;
import com.supermarket.common.utils.JwtUtils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 认证过滤器
 * 作用：拦截所有请求，检查 Header 中的 Token
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. 获取请求头中的 Token (通常格式: Authorization: Bearer <token>)
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            // 2. 截取 Token 字符串 (去掉 "Bearer " 前缀)
            String token = header.substring(7);

            // 3. 校验 Token 是否有效
            if (jwtUtils.validateToken(token)) {
                // 4. 解析用户名
                String username = jwtUtils.getUsernameFromToken(token);

                // 5. 根据用户名加载用户详情 (查询数据库或缓存)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 6. 构建认证对象 (告诉 Spring Security：这个人已经认证通过了)
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 7. 放入安全上下文 (这一步至关重要，后续 Controller 才能通过 SecurityContext 获取当前用户)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 8. 继续执行过滤器链
        chain.doFilter(request, response);
    }
}