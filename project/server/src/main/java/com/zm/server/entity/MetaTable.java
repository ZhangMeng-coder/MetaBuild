package com.zm.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据表定义实体类
 * <p>对应数据库表 tb_metadata_table，存储元数据表的定义信息。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_metadata_table")
public class MetaTable extends BaseEntity {

    /** 主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 表名（对应数据库实际表名） */
    private String tableName;

    /** 表备注/显示名称 */
    private String tableComment;

    /** 状态：0-停用，1-启用 */
    private Integer status;

    /** 备注 */
    private String remark;
}