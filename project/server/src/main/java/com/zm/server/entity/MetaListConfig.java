package com.zm.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据列表配置实体类
 * <p>对应数据库表 tb_metadata_list_config，配置元数据表在列表页中的列展示方式。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_metadata_list_config")
public class MetaListConfig extends BaseEntity {

    /** 主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 所属元数据表ID */
    private Long tableId;

    /** 关联的元数据列ID */
    private Long columnId;

    /** 列显示名称（覆盖默认） */
    private String columnLabel;

    /** 列宽（px） */
    private Integer width;

    /** 对齐方式(left/center/right) */
    private String align;

    /** 是否可排序：0-否，1-是 */
    private Integer sortable;

    /** 是否可见：0-否，1-是 */
    private Integer visible;

    /** 冻结位置(left/right) */
    private String frozen;

    /** 是否作为搜索条件：0-否，1-是 */
    private Integer isSearch;

    /** 排序号 */
    private Integer sort;
}