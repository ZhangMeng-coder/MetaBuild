package com.zm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.system.config.security.CustomUserDetails;
import com.zm.system.config.security.JwtTokenProvider;
import com.zm.common.enums.ServiceExceptionEnums;
import com.zm.common.exception.ServiceException;
import com.zm.system.dto.LoginRequest;
import com.zm.system.dto.LoginResponse;
import com.zm.system.entity.SysMenu;
import com.zm.system.entity.SysUser;
import com.zm.system.mapper.*;
import com.zm.system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * <p>实现用户登录认证、获取当前用户信息等功能。
 * 使用 Spring Security 进行密码校验，JWT 生成令牌。</p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据用户名查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginRequest.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        // 用户不存在，抛出异常
        if (user == null) {
            throw new ServiceException(ServiceExceptionEnums.USERNAME_PASSWORD_ERROR);
        }

        // 校验密码是否匹配
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ServiceException(ServiceExceptionEnums.USERNAME_PASSWORD_ERROR);
        }

        // 检查用户是否被禁用
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ServiceException(ServiceExceptionEnums.USER_DISABLED);
        }

        // 查询用户拥有的角色编码列表
        List<String> roleCodes = roleMapper.selectRoleCodesByUserId(user.getId());

        // 查询用户角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());

        // 根据角色ID查询菜单，提取所有权限标识（perms）
        List<String> permissions = Collections.emptyList();
        if (!roleIds.isEmpty()) {
            List<SysMenu> menus = menuMapper.selectMenusByRoleIds(roleIds);
            permissions = menus.stream()
                    .map(SysMenu::getPerms)
                    .filter(Objects::nonNull)
                    .filter(p -> !p.isEmpty())
                    .collect(Collectors.toList());
        }

        // 生成 JWT Token（包含角色编码和权限标识）
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roleCodes, permissions);

        // 返回登录响应
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRealName(), roleCodes);
    }

    @Override
    public Map<String, Object> getUserInfo() {
        // 从 SecurityContext 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 认证信息不存在或不是 CustomUserDetails 类型，抛出异常
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ServiceException(ServiceExceptionEnums.DATA_NOT_EXIST);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // 查询用户基本信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ServiceExceptionEnums.DATA_NOT_EXIST);
        }

        // 获取用户角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);

        // 根据角色ID列表查询菜单，并构建树形结构
        List<SysMenu> menus;
        if (roleIds.isEmpty()) {
            menus = Collections.emptyList();
        } else {
            List<SysMenu> allMenus = menuMapper.selectMenusByRoleIds(roleIds);
            menus = buildMenuTree(allMenus);
        }

        // 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("menus", menus);

        return result;
    }

    /**
     * 构建菜单树
     * <p>将扁平的菜单列表转换为树形结构，只保留顶级菜单（parentId 为 null 或 0），
     * 并将子菜单挂载到对应的父菜单下。</p>
     *
     * @param menus 扁平菜单列表
     * @return 树形菜单列表
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        // 按父菜单ID分组，过滤掉顶级菜单
        Map<Long, List<SysMenu>> parentMap = menus.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() > 0)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 获取所有顶级菜单，按排序号排序
        List<SysMenu> tree = menus.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .sorted(Comparator.comparingInt(SysMenu::getSort))
                .collect(Collectors.toList());

        // 递归设置每个顶级菜单的子菜单
        for (SysMenu menu : tree) {
            setChildren(menu, parentMap);
        }

        return tree;
    }

    /**
     * 递归设置子菜单
     * <p>从 parentMap 中查找当前菜单的子菜单列表，递归设置子菜单的 children。</p>
     *
     * @param parent    父菜单
     * @param parentMap 父菜单ID到子菜单列表的映射
     */
    private void setChildren(SysMenu parent, Map<Long, List<SysMenu>> parentMap) {
        List<SysMenu> children = parentMap.get(parent.getId());
        if (children != null) {
            // 子菜单按排序号排序
            children.sort(Comparator.comparingInt(SysMenu::getSort));
            parent.setChildren(children);
            // 递归设置子菜单的子菜单
            for (SysMenu child : children) {
                setChildren(child, parentMap);
            }
        }
    }
}
