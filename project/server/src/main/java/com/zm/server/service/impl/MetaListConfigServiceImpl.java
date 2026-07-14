package com.zm.server.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.common.util.UserContext;
import com.zm.server.entity.MetaListConfig;
import com.zm.server.mapper.MetaListConfigMapper;
import com.zm.server.service.MetaListConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 元数据列表配置 Service 实现
 */
@Service
@Transactional
public class MetaListConfigServiceImpl extends ServiceImpl<MetaListConfigMapper, MetaListConfig> implements MetaListConfigService {

    @Autowired
    private MetaListConfigMapper metaListConfigMapper;

    @Override
    public void batchSave(Long tableId, List<MetaListConfig> configs) {
        if (configs == null || configs.isEmpty()) {
            return;
        }
        Integer userId = UserContext.getUserId() != null ? UserContext.getUserId().intValue() : null;
        for (MetaListConfig config : configs) {
            config.setTableId(tableId);
            if (config.getId() != null) {
                // 有ID，走修改
                if (userId != null) {
                    config.setUpdateUser(userId);
                }
                metaListConfigMapper.updateById(config);
            } else {
                // 无ID，走新增
                if (userId != null) {
                    config.setCreateUser(userId);
                }
                metaListConfigMapper.insert(config);
            }
        }
    }
}