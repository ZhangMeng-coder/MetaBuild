package com.zm.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据列定义实体类
 * <p>对应数据库表 tb_metadata_column，存储元数据表的列定义信息，包括展示类型、字典项等。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_metadata_column")
public class MetaColumn extends BaseEntity {

    /** 主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 所属元数据表ID */
    private Long tableId;

    /** 列名（对应数据库实际列名） */
    private String columnName;

    /** 列备注/显示名称 */
    private String columnComment;

    /** 数据类型（如 varchar, int, datetime） */
    private String dataType;

    /** 是否必填：0-否，1-是 */
    private Integer isRequired;

    /** 是否主键：0-否，1-是 */
    private Integer isPk;

    /** 展示类型：text/select/radio/checkbox/date/time/datetime */
    private String showType;

    /** 字典类型（关联 sys_dict_type.dict_type） */
    private String dictType;

    /** SQL 动态字典项（需返回 id 和 name 字段） */
    private String dictSql;

    /** 排序号 */
    private Integer sort;

    /** 状态：0-停用，1-启用 */
    private Integer status;

    /** 备注 */
    private String remark;
}