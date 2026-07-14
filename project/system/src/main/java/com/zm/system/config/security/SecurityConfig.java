package com.zm.system.config.security;

import com.zm.common.vo.RespData;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Spring Security 安全配置类
 * <p>配置 HTTP 安全策略，包括：禁用 CSRF、无状态会话管理（JWT）、
 * 接口权限控制、异常处理（401/403）以及注册 JWT 认证过滤器。</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // 启用方法级权限控制（@PreAuthorize）
@RequiredArgsConstructor
public class SecurityConfig {

    /** JWT 认证过滤器，用于在每次请求时校验 Token */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * <p>定义请求的认证规则、异常处理策略，并注册 JWT 认证过滤器。</p>
     *
     * @param http HttpSecurity 对象
     * @return 构建好的 SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 登录接口对外开放，无需认证
                .requestMatchers("/api/auth/login").permitAll()
                // 其他 /api/** 接口需要认证
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                // 401: 未登录/登录过期/token无效
                .authenticationEntryPoint(authenticationEntryPoint())
                // 403: 已登录但无权限
                .accessDeniedHandler(accessDeniedHandler())
            )
            // 在 UsernamePasswordAuthenticationFilter 之前执行 JWT 认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 未认证处理器（401）
     * <p>当用户未登录或 Token 过期/无效时，返回 401 错误响应。</p>
     *
     * @return AuthenticationEntryPoint 实例
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(RespData.error(401, "未登录或登录已过期")));
        };
    }

    /**
     * 权限不足处理器（403）
     * <p>当用户已登录但无权限访问某个接口时，返回 403 错误响应。</p>
     *
     * @return AccessDeniedHandler 实例
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JSON.toJSONString(RespData.error(403, "权限不足")));
        };
    }

    /**
     * 密码编码器
     * <p>使用 BCrypt 加密算法对密码进行加密和校验。</p>
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     *
     * @param config 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
