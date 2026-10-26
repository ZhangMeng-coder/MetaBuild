package com.zm.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.common.util.UserContext;
import com.zm.server.entity.MetaColumn;
import com.zm.server.entity.MetaTable;
import com.zm.server.mapper.MetaColumnMapper;
import com.zm.server.mapper.MetaTableMapper;
import com.zm.server.service.MetaTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 元数据表 Service 实现
 */
@Service
@Transactional
public class MetaTableServiceImpl extends ServiceImpl<MetaTableMapper, MetaTable> implements MetaTableService {

    @Autowired
    private MetaTableMapper metaTableMapper;

    @Autowired
    private MetaColumnMapper metaColumnMapper;

    @Override
    public void add(MetaTable metaTable, List<Map<String, Object>> columns) {
        // 设置默认值
        if (metaTable.getStatus() == null) {
            metaTable.setStatus(1);
        }
        if (UserContext.getUserId() != null) {
            metaTable.setCreateUser(UserContext.getUserId().intValue());
        }
        // 保存元数据表
        metaTableMapper.insert(metaTable);

        // 保存列定义
        if (columns != null && !columns.isEmpty()) {
            for (int i = 0; i < columns.size(); i++) {
                Map<String, Object> colMap = columns.get(i);
                MetaColumn column = new MetaColumn();
                column.setTableId(metaTable.getId());
                column.setColumnName((String) colMap.get("columnName"));
                column.setColumnComment((String) colMap.get("columnComment"));
                column.setDataType((String) colMap.get("dataType"));
                // 是否必填：IS_NULLABLE = 'NO' 表示必填
                column.setIsRequired("NO".equalsIgnoreCase((String) colMap.get("isNullable")) ? 1 : 0);
                // 是否主键：COLUMN_KEY = 'PRI' 表示主键
                column.setIsPk("PRI".equalsIgnoreCase((String) colMap.get("columnKey")) ? 1 : 0);
                column.setShowType("text");
                column.setSort(i + 1);
                column.setStatus(1);
                if (UserContext.getUserId() != null) {
                    column.setCreateUser(UserContext.getUserId().intValue());
                }
                metaColumnMapper.insert(column);
            }
        }
    }

    @Override
    public void edit(MetaTable metaTable) {
        if (UserContext.getUserId() != null) {
            metaTable.setUpdateUser(UserContext.getUserId().intValue());
        }
        metaTableMapper.updateById(metaTable);
    }

    @Override
    public void delete(Long id) {
        // 逻辑删除表
        MetaTable metaTable = new MetaTable();
        metaTable.setId(id);
        if (UserContext.getUserId() != null) {
            metaTable.setUpdateUser(UserContext.getUserId().intValue());
        }
        metaTableMapper.deleteById(metaTable);

        // 逻辑删除该表下的所有列
        MetaColumn updateColumn = new MetaColumn();
        if (UserContext.getUserId() != null) {
            updateColumn.setUpdateUser(UserContext.getUserId().intValue());
        }
        LambdaQueryWrapper<MetaColumn> wrapper = new LambdaQueryWrapper<MetaColumn>()
                .eq(MetaColumn::getTableId, id);
        metaColumnMapper.delete(wrapper);
    }
}