package com.zm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.system.config.security.CustomUserDetails;
import com.zm.common.enums.ServiceExceptionEnums;
import com.zm.common.exception.ServiceException;
import com.zm.system.entity.SysMenu;
import com.zm.system.mapper.MenuMapper;
import com.zm.system.mapper.UserRoleMapper;
import com.zm.system.service.MenuService;
import com.zm.common.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuServiceImpl extends ServiceImpl<MenuMapper, SysMenu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<SysMenu> tree() {
        List<SysMenu> allMenus = menuMapper.selectAllMenus();
        return buildMenuTree(allMenus);
    }

    @Override
    public Long add(SysMenu menu) {
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        // 设置创建人
        if (UserContext.getUserId() != null) {
            menu.setCreateUser(UserContext.getUserId().intValue());
        }
        menuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public void update(SysMenu menu) {
        // 设置更新人
        if (UserContext.getUserId() != null) {
            menu.setUpdateUser(UserContext.getUserId().intValue());
        }
        menuMapper.updateById(menu);
    }

    @Override
    public void delete(Long id) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        if (menuMapper.selectCount(wrapper) > 0) {
            throw new ServiceException(ServiceExceptionEnums.DATA_EXIST);
        }
        SysMenu menu = new SysMenu();
        menu.setId(id);
        // 设置更新人（逻辑删除需要更新人）
        if (UserContext.getUserId() != null) {
            menu.setUpdateUser(UserContext.getUserId().intValue());
        }
        menuMapper.deleteById(menu);
    }

    @Override
    public List<SysMenu> getCurrentUserMenus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return Collections.emptyList();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // 获取用户角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);

        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<SysMenu> menus = menuMapper.selectMenusByRoleIds(roleIds);
        return buildMenuTree(menus);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<SysMenu>> parentMap = menus.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() > 0)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        List<SysMenu> tree = menus.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .sorted(Comparator.comparingInt(SysMenu::getSort))
                .collect(Collectors.toList());

        for (SysMenu menu : tree) {
            setChildren(menu, parentMap);
        }

        return tree;
    }

    private void setChildren(SysMenu parent, Map<Long, List<SysMenu>> parentMap) {
        List<SysMenu> children = parentMap.get(parent.getId());
        if (children != null) {
            children.sort(Comparator.comparingInt(SysMenu::getSort));
            parent.setChildren(children);
            for (SysMenu child : children) {
                setChildren(child, parentMap);
            }
        }
    }
}
