package com.zm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.common.util.UserContext;
import com.zm.system.entity.SysDictData;
import com.zm.system.mapper.DictDataMapper;
import com.zm.system.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, SysDictData> implements DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Override
    public void add(SysDictData dictData) {
        if (dictData.getDictSort() == null) {
            dictData.setDictSort(0);
        }
        if (dictData.getStatus() == null) {
            dictData.setStatus(1);
        }
        if (UserContext.getUserId() != null) {
            dictData.setCreateUser(UserContext.getUserId().intValue());
        }
        dictDataMapper.insert(dictData);
    }

    @Override
    public void edit(SysDictData dictData) {
        if (UserContext.getUserId() != null) {
            dictData.setUpdateUser(UserContext.getUserId().intValue());
        }
        dictDataMapper.updateById(dictData);
    }

    @Override
    public void delete(Long id) {
        SysDictData dictData = new SysDictData();
        dictData.setId(id);
        if (UserContext.getUserId() != null) {
            dictData.setUpdateUser(UserContext.getUserId().intValue());
        }
        dictDataMapper.deleteById(dictData);
    }

    @Override
    public List<SysDictData> getDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, 1)
                .orderByAsc(SysDictData::getDictSort);
        return dictDataMapper.selectList(wrapper);
    }
}
