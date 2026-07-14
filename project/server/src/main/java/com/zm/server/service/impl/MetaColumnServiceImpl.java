package com.zm.server.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.common.util.UserContext;
import com.zm.server.entity.MetaColumn;
import com.zm.server.mapper.MetaColumnMapper;
import com.zm.server.service.MetaColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 元数据列 Service 实现
 */
@Service
@Transactional
public class MetaColumnServiceImpl extends ServiceImpl<MetaColumnMapper, MetaColumn> implements MetaColumnService {

    @Autowired
    private MetaColumnMapper metaColumnMapper;

    @Override
    public void edit(MetaColumn column) {
        if (UserContext.getUserId() != null) {
            column.setUpdateUser(UserContext.getUserId().intValue());
        }
        metaColumnMapper.updateById(column);
    }
}