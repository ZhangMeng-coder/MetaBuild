package com.zm.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.system.entity.SysDictData;

import java.util.List;

public interface DictDataService extends IService<SysDictData> {

    void add(SysDictData dictData);

    void edit(SysDictData dictData);

    void delete(Long id);

    /**
     * 根据字典类型获取启用的字典数据列表
     */
    List<SysDictData> getDictDataByType(String dictType);
}
