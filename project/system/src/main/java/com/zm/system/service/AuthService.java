package com.zm.system.service;

import com.zm.system.dto.LoginRequest;
import com.zm.system.dto.LoginResponse;

import java.util.Map;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    Map<String, Object> getUserInfo();
}
