package com.zm.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据表单配置实体类
 * <p>对应数据库表 tb_metadata_form_config，配置元数据表在新增/编辑表单中的字段展示方式。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_metadata_form_config")
public class MetaFormConfig extends BaseEntity {

    /** 主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 所属元数据表ID */
    private Long tableId;

    /** 关联的元数据列ID */
    private Long columnId;

    /** 字段显示名称（覆盖默认） */
    private String fieldLabel;

    /** 是否必填（覆盖默认，null表示使用列默认） */
    private Integer isRequired;

    /** 栅格宽度(1-24, 默认12即半行) */
    private Integer span;

    /** 占位提示文字 */
    private String placeholder;

    /** 默认值 */
    private String defaultValue;

    /** 排序号 */
    private Integer sort;
}