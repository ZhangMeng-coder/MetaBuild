package com.zm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.common.enums.ServiceExceptionEnums;
import com.zm.common.exception.ServiceException;
import com.zm.common.util.UserContext;
import com.zm.system.entity.SysDictType;
import com.zm.system.mapper.DictTypeMapper;
import com.zm.system.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, SysDictType> implements DictTypeService {

    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Override
    public void add(SysDictType dictType) {
        if (checkDictTypeUnique(dictType.getDictType(), null)) {
            throw new ServiceException(ServiceExceptionEnums.DATA_EXIST);
        }
        if (dictType.getSort() == null) {
            dictType.setSort(0);
        }
        if (dictType.getStatus() == null) {
            dictType.setStatus(1);
        }
        if (UserContext.getUserId() != null) {
            dictType.setCreateUser(UserContext.getUserId().intValue());
        }
        dictTypeMapper.insert(dictType);
    }

    @Override
    public void edit(SysDictType dictType) {
        if (checkDictTypeUnique(dictType.getDictType(), dictType.getId())) {
            throw new ServiceException(ServiceExceptionEnums.DATA_EXIST);
        }
        if (UserContext.getUserId() != null) {
            dictType.setUpdateUser(UserContext.getUserId().intValue());
        }
        dictTypeMapper.updateById(dictType);
    }

    @Override
    public void delete(Long id) {
        SysDictType dictType = new SysDictType();
        dictType.setId(id);
        if (UserContext.getUserId() != null) {
            dictType.setUpdateUser(UserContext.getUserId().intValue());
        }
        dictTypeMapper.deleteById(dictType);
    }

    @Override
    public boolean checkDictTypeUnique(String dictType, Long id) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        if (id != null) {
            wrapper.ne(SysDictType::getId, id);
        }
        return dictTypeMapper.selectCount(wrapper) > 0;
    }
}
