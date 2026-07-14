package com.zm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统菜单实体类
 * <p>对应数据库表 sys_menu，存储系统菜单和权限信息。
 * 支持树形结构，通过 parentId 关联父级菜单，children 字段存储子菜单列表（非数据库字段）。
 * 继承 BaseEntity 获取创建时间、更新时间等通用字段。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 菜单ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 父菜单ID，顶级菜单该值为 0 */
    private Long parentId;

    /** 菜单名称，显示在界面上的文本 */
    private String menuName;

    /** 菜单类型：0-目录，1-菜单，2-按钮（权限标识） */
    private Integer menuType;

    /** 路由路径，前端路由跳转的路径 */
    private String path;

    /** 组件路径，前端页面组件的加载路径 */
    private String component;

    /** 权限标识，如 "system:user:list"，用于后端接口鉴权 */
    private String perms;

    /** 菜单图标，使用图标库中的图标名称 */
    private String icon;

    /** 排序号，数值越小排列越靠前 */
    private Integer sort;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 子菜单列表，非数据库字段，用于构建树形菜单结构 */
    @TableField(exist = false)
    private List<SysMenu> children;
}
