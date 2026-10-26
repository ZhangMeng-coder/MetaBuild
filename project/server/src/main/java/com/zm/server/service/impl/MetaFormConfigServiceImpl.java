package com.zm.server.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zm.common.util.UserContext;
import com.zm.server.entity.MetaFormConfig;
import com.zm.server.mapper.MetaFormConfigMapper;
import com.zm.server.service.MetaFormConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 元数据表单配置 Service 实现
 */
@Service
@Transactional
public class MetaFormConfigServiceImpl extends ServiceImpl<MetaFormConfigMapper, MetaFormConfig> implements MetaFormConfigService {

    @Autowired
    private MetaFormConfigMapper metaFormConfigMapper;

    @Override
    public void batchSave(Long tableId, List<MetaFormConfig> configs) {
        if (configs == null || configs.isEmpty()) {
            return;
        }
        Integer userId = UserContext.getUserId() != null ? UserContext.getUserId().intValue() : null;
        for (MetaFormConfig config : configs) {
            config.setTableId(tableId);
            if (config.getId() != null) {
                // 有ID，走修改
                if (userId != null) {
                    config.setUpdateUser(userId);
                }
                metaFormConfigMapper.updateById(config);
            } else {
                // 无ID，走新增
                if (userId != null) {
                    config.setCreateUser(userId);
                }
                metaFormConfigMapper.insert(config);
            }
        }
    }
}