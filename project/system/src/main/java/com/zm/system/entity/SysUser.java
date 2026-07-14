package com.zm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体类
 * <p>对应数据库表 sys_user，存储系统用户的基本信息，包括用户名、密码、状态等。
 * 继承 BaseEntity 获取创建时间、更新时间等通用字段。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户ID，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名，登录时使用的唯一标识 */
    private String username;

    /** 密码，经过 BCrypt 加密后存储 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 邮箱地址 */
    private String email;

    /** 手机号码 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 角色名称（逗号分隔），非数据库字段，用于列表展示 */
    @TableField(exist = false)
    private String roleNames;
}
