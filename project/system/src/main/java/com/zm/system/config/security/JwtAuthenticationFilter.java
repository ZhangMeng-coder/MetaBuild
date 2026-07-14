package com.zm.system.config.security;

import com.zm.common.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 * <p>继承 OncePerRequestFilter，确保每次请求只执行一次过滤。
 * 从请求头中提取 JWT Token，解析并设置认证信息到 SecurityContext 中。</p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT Token 提供者，用于解析和校验 Token */
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头中提取 Token
            String token = extractToken(request);
            // 如果 Token 存在且有效，则解析并设置认证信息
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                String username = jwtTokenProvider.getUsernameFromToken(token);
                List<String> roles = jwtTokenProvider.getRolesFromToken(token);
                List<String> permissions = jwtTokenProvider.getPermissionsFromToken(token);

                // 设置当前线程的用户上下文
                UserContext.set(userId, username);

                // 构建 CustomUserDetails 和认证令牌（同时包含角色和权限标识）
                CustomUserDetails userDetails = new CustomUserDetails(userId, username, roles, permissions);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息设置到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 继续执行过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束，清理 ThreadLocal 防止内存泄漏
            UserContext.clear();
        }
    }

    /**
     * 从请求头中提取 Bearer Token
     * <p>从 Authorization 请求头中提取 "Bearer &lt;token&gt;" 格式的 JWT Token。</p>
     *
     * @param request HTTP 请求
     * @return JWT Token 字符串，如果不存在则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
