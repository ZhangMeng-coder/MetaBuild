package com.zm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体类
 * <p>对应数据库表 sys_role，存储系统角色的定义信息，包括角色名称、编码、状态等。
 * 继承 BaseEntity 获取创建时间、更新时间等通用字段。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 角色名称，如"管理员"、"普通用户"等 */
    private String roleName;

    /** 角色编码，如"admin"、"user"，用于权限校验 */
    private String roleCode;

    /** 角色描述，说明该角色的用途和权限范围 */
    private String description;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 排序号，数值越小排列越靠前 */
    private Integer sort;
}
