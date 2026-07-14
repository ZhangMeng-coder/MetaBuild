package com.zm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zm.common.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体类
 * <p>对应数据库表 sys_dict_data，存储字典的具体键值对数据。
 * 通过 dictType 字段关联到字典类型。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    /** 字典编码，主键，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 字典类型，关联 sys_dict_type 的 dict_type */
    private String dictType;

    /** 字典标签，显示在界面上的文本 */
    private String dictLabel;

    /** 字典键值，实际存储的值 */
    private String dictValue;

    /** 字典排序 */
    private Integer dictSort;

    /** 样式属性（如：primary、success、info、warning、danger） */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认：0-否，1-是 */
    private Integer isDefault;

    /** 状态：0-停用，1-启用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
