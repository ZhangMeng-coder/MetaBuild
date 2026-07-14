package com.zm.system.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final List<String> roles;
    /** 权限标识列表（如 system:user:add），用于 @PreAuthorize 校验 */
    private final List<String> permissions;

    public CustomUserDetails(Long userId, String username, List<String> roles, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 角色编码作为 GrantedAuthority（以 ROLE_ 开头）
        roles.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
        // 权限标识也作为 GrantedAuthority，用于 hasAuthority() 校验
        if (permissions != null) {
            permissions.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
