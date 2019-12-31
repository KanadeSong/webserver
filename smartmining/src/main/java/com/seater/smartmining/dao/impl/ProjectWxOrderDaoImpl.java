package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectWxOrderDaoI;
import com.seater.smartmining.entity.ProjectWxOrder;
import com.seater.smartmining.entity.repository.ProjectWxOrderRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 14:12
 */
@Component
public class ProjectWxOrderDaoImpl implements ProjectWxOrderDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectWxOrderRepository projectWxOrderRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectwxorder:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectWxOrder get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectWxOrder.class);
        }
        if(projectWxOrderRepository.existsById(id))
        {
            ProjectWxOrder log = projectWxOrderRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectWxOrder save(ProjectWxOrder log) throws IOException {
        ProjectWxOrder log1 = projectWxOrderRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectWxOrderRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectWxOrder> query() {
        return projectWxOrderRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectWxOrder> query(Specification<ProjectWxOrder> spec) {
        return projectWxOrderRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectWxOrder> query(Pageable pageable) {
        return projectWxOrderRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectWxOrder> query(Specification<ProjectWxOrder> spec, Pageable pageable) {
        return projectWxOrderRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectWxOrder> getAll() {
        return projectWxOrderRepository.findAll();
    }

    @Override
    public ProjectWxOrder getAllByOrderNoAndAppIdAndOpenId(String orderNo, String appId, String openId) {
        return projectWxOrderRepository.getAllByOrderNoAndAppIdAndOpenId(orderNo, appId, openId);
    }

    @Override
    public ProjectWxOrder getAllByWechatOrderNo(String wechatOrderNo) {
        return projectWxOrderRepository.getAllByWechatOrderNo(wechatOrderNo);
    }

    @Override
    public void deleteByOrderNo(String orderNo) {
        projectWxOrderRepository.deleteByOrderNo(orderNo);
    }
}
