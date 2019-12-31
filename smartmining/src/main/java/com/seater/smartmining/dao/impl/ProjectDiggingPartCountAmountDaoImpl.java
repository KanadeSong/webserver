package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingPartCountAmountDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.entity.ProjectDiggingPartCountAmount;
import com.seater.smartmining.entity.repository.ProjectDiggingPartCountAmountRepository;
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
 * @Date 2019/5/16 0016 11:45
 */
@Component
public class ProjectDiggingPartCountAmountDaoImpl implements ProjectDiggingPartCountAmountDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectDiggingPartCountAmountRepository projectDiggingPartCountAmountRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingpartcountamount:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingPartCountAmount get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingPartCountAmount.class);
        }
        if(projectDiggingPartCountAmountRepository.existsById(id)){
            ProjectDiggingPartCountAmount log = projectDiggingPartCountAmountRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingPartCountAmount save(ProjectDiggingPartCountAmount log) throws JsonProcessingException {
        ProjectDiggingPartCountAmount log1 = projectDiggingPartCountAmountRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingPartCountAmountRepository.deleteById(id);
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query() {
        return projectDiggingPartCountAmountRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query(Specification<ProjectDiggingPartCountAmount> spec) {
        return projectDiggingPartCountAmountRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query(Pageable pageable) {
        return projectDiggingPartCountAmountRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query(Specification<ProjectDiggingPartCountAmount> spec, Pageable pageable) {
        return projectDiggingPartCountAmountRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCountAmount> getAll() {
        return projectDiggingPartCountAmountRepository.findAll();
    }

    @Override
    public ProjectDiggingPartCountAmount getAllByProjectIdAndCountId(Long projectId, Long countId) {
        return projectDiggingPartCountAmountRepository.getAllByProjectIdAndCountId(projectId, countId);
    }
}
