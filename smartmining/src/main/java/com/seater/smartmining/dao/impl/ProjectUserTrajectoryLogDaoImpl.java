package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectUserTrajectoryLogDaoI;
import com.seater.smartmining.entity.ProjectUserTrajectoryLog;
import com.seater.smartmining.entity.repository.ProjectUserTrajectoryLogRepository;
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
 * @Date 2019/12/18 0018 15:33
 */
@Component
public class ProjectUserTrajectoryLogDaoImpl implements ProjectUserTrajectoryLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectUserTrajectoryLogRepository projectUserTrajectoryLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:ProjectUserTrajectoryLog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectUserTrajectoryLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectUserTrajectoryLog.class);
        }
        if(projectUserTrajectoryLogRepository.existsById(id)){
            ProjectUserTrajectoryLog log = projectUserTrajectoryLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectUserTrajectoryLog save(ProjectUserTrajectoryLog log) throws IOException {
        ProjectUserTrajectoryLog log1 = projectUserTrajectoryLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectUserTrajectoryLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query() {
        return projectUserTrajectoryLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query(Specification<ProjectUserTrajectoryLog> spec) {
        return projectUserTrajectoryLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query(Pageable pageable) {
        return projectUserTrajectoryLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query(Specification<ProjectUserTrajectoryLog> spec, Pageable pageable) {
        return projectUserTrajectoryLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectUserTrajectoryLog> getAll() {
        return projectUserTrajectoryLogRepository.findAll();
    }

    @Override
    public void saveAll(List<ProjectUserTrajectoryLog> saveList) {
        projectUserTrajectoryLogRepository.saveAll(saveList);
    }
}
