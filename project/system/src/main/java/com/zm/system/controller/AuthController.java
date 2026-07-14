package com.zm.system.controller;

import com.zm.common.vo.RespData;
import com.zm.system.annotation.LogInfo;
import com.zm.system.dto.LoginRequest;
import com.zm.system.dto.LoginResponse;
import com.zm.system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * <p>提供用户登录和获取当前用户信息的 RESTful 接口。
 * 登录接口无需认证，获取用户信息接口需要携带 JWT Token。</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * <p>接收用户名和密码，认证通过后返回 JWT Token 和用户基本信息。</p>
     *
     * @param loginRequest 登录请求，包含用户名和密码
     * @return 包含 Token、用户信息和角色列表的响应
     */
    @PostMapping("/login")
    @LogInfo("用户登录")
    public RespData<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return RespData.success(response);
    }

    /**
     * 获取当前用户信息
     * <p>根据请求头中的 JWT Token 解析当前用户，返回用户基本信息和可访问的菜单树。</p>
     *
     * @return 包含用户信息和菜单树的响应
     */
    @GetMapping("/me")
    @LogInfo("获取用户信息")
    public RespData<Map<String, Object>> me() {
        Map<String, Object> userInfo = authService.getUserInfo();
        return RespData.success(userInfo);
    }
}
