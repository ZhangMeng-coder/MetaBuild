package com.zm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型实体类
 * <p>对应数据库表 sys_dict_type，存储字典类型的定义。
 * 每种字典类型对应一类数据，如：用户状态、性别等。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    /** 字典主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 字典名称，如：用户状态 */
    private String dictName;

    /** 字典类型，如：sys_user_status，具有唯一性 */
    private String dictType;

    /** 状态：0-停用，1-启用 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 排序号，数值越小排列越靠前 */
    private Integer sort;
}
