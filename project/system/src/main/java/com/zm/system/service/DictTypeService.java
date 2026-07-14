package com.zm.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zm.system.entity.SysDictType;

public interface DictTypeService extends IService<SysDictType> {

    void add(SysDictType dictType);

    void edit(SysDictType dictType);

    void delete(Long id);

    /**
     * 校验字典类型是否唯一
     */
    boolean checkDictTypeUnique(String dictType, Long id);
}
