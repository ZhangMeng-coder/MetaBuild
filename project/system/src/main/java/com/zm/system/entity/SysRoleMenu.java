package com.zm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-菜单关联实体类
 * <p>对应数据库表 sys_role_menu，建立角色与菜单之间的多对多关联关系。
 * 一个角色可以访问多个菜单（权限），一个菜单也可以被多个角色访问。</p>
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {

    /** 关联记录ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 角色ID，关联 sys_role 表的主键 */
    private Long roleId;

    /** 菜单ID，关联 sys_menu 表的主键 */
    private Long menuId;
}
