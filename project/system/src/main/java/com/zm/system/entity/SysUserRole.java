package com.zm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-角色关联实体类
 * <p>对应数据库表 sys_user_role，建立用户与角色之间的多对多关联关系。
 * 一个用户可以拥有多个角色，一个角色也可以被多个用户拥有。</p>
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {

    /** 关联记录ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户ID，关联 sys_user 表的主键 */
    private Long userId;

    /** 角色ID，关联 sys_role 表的主键 */
    private Long roleId;
}
