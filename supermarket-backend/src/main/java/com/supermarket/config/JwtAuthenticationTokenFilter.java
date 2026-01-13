package com.supermarket.config;

import com.supermarket.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
 * 作用：拦截所有请求，检查 Header 中是否有有效的 Token
 * 
 * 关键思路：
 * 1. 继承 OncePerRequestFilter，确保每个请求只经过一次过滤器
 * 2. 解析 Header -> 校验 Token -> 获取用户名 -> 加载用户信息 -> 存入 SecurityContext
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 1. 从请求头中获取 Token
        // 约定：前端在 Header 中传 "Authorization: Bearer xxxxx.yyyyy.zzzzz"
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            // 截取 "Bearer " 后面的 token 字符串
            String token = authHeader.substring(7);

            // 2. 验证 Token 是否有效
            // 如果解析失败(过期或篡改)，validateToken 会返回 false 或抛出异常
            // 这里为了简单，如果校验失败就不把用户信息放入 Context，请求会在后续环节因为没权限被拒绝
            try {
                if (jwtUtils.validateToken(token)) {
                    // 3. 从 Token 中解析出用户名
                    Claims claims = jwtUtils.getClaimsByToken(token);
                    String username = claims.getSubject();

                    // 4. 如果用户名不为空，且当前 SecurityContext 还没认证过
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 去数据库加载用户信息 (确保用户没被删，且密码没变)
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 5. 构建认证对象 (UsernamePasswordAuthenticationToken)
                        // 三个参数：用户信息、密码(null)、权限列表
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 6. ★★★ 关键一步：将认证对象存入 SecurityContext
                        // 只要这里存进去了，Spring Security 就认为你是“已登录”状态
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // Token 无效或过期，直接忽略，放行给后面的过滤器处理(后面的过滤器会发现没登录从而报错)
                logger.error("Token认证失败: " + e.getMessage());
            }
        }

        // 7. 放行，进入下一个过滤器
        chain.doFilter(request, response);
    }
}