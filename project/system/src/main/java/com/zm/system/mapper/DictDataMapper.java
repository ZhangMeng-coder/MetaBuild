package com.zm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.system.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典类型查询启用的字典数据列表
     */
    List<SysDictData> selectDictDataByType(String dictType);
}
