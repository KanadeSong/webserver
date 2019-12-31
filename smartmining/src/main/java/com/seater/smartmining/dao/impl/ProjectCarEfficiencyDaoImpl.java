package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarEfficiencyDaoI;
import com.seater.smartmining.entity.ProjectCarEfficiency;
import com.seater.smartmining.entity.repository.ProjectCarEfficiencyRepository;
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
 * @Date 2019/12/17 0017 16:48
 */
@Component
public class ProjectCarEfficiencyDaoImpl implements ProjectCarEfficiencyDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarEfficiencyRepository projectCarEfficiencyRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarefficiency:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCarEfficiency get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarEfficiency.class);
        }
        if(projectCarEfficiencyRepository.existsById(id)){
            ProjectCarEfficiency log = projectCarEfficiencyRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectCarEfficiency save(ProjectCarEfficiency log) throws IOException {
        ProjectCarEfficiency log1 = projectCarEfficiencyRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarEfficiencyRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarEfficiency> query() {
        return projectCarEfficiencyRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarEfficiency> query(Specification<ProjectCarEfficiency> spec) {
        return projectCarEfficiencyRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarEfficiency> query(Pageable pageable) {
        return projectCarEfficiencyRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarEfficiency> query(Specification<ProjectCarEfficiency> spec, Pageable pageable) {
        return projectCarEfficiencyRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCarEfficiency> getAll() {
        return projectCarEfficiencyRepository.findAll();
    }
}
