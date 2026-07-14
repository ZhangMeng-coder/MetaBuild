package com.zm.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.system.entity.SysMenu;

import java.util.List;

public interface MenuService extends IService<SysMenu> {

    List<SysMenu> tree();

    void add(SysMenu menu);

    void update(SysMenu menu);

    void delete(Long id);

    List<SysMenu> getCurrentUserMenus();
}
