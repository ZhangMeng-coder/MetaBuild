package com.zm.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 数据库元数据查询 Mapper
 * <p>用于查询 information_schema 获取数据库表和列信息。</p>
 */
@Mapper
public interface MetaDbMapper {

    /**
     * 查询当前数据库中所有表（排除 sys_ 和 tb_ 开头的表）
     *
     * @return 表信息列表，包含 TABLE_NAME 和 TABLE_COMMENT
     */
    List<Map<String, Object>> selectDbTables();

    /**
     * 查询指定表的列信息
     *
     * @param tableName 表名
     * @return 列信息列表，包含 COLUMN_NAME、COLUMN_COMMENT、DATA_TYPE、IS_NULLABLE、COLUMN_KEY、ORDINAL_POSITION
     */
    List<Map<String, Object>> selectDbColumns(@Param("tableName") String tableName);
}