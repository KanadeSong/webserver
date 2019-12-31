package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectRunningTrajectoryLogDaoI;
import com.seater.smartmining.entity.ProjectRunningTrajectoryLog;
import com.seater.smartmining.entity.repository.ProjectRunningTrajectoryLogRepository;
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
 * @Date 2019/12/17 0017 9:15
 */
@Component
public class ProjectRunningTrajectoryLogDaoImpl implements ProjectRunningTrajectoryLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectRunningTrajectoryLogRepository projectRunningTrajectoryLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectrunningtrajectorylog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectRunningTrajectoryLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectRunningTrajectoryLog.class);
        }
        if(projectRunningTrajectoryLogRepository.existsById(id)){
            ProjectRunningTrajectoryLog log = projectRunningTrajectoryLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectRunningTrajectoryLog save(ProjectRunningTrajectoryLog log) throws IOException {
        ProjectRunningTrajectoryLog log1 = projectRunningTrajectoryLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectRunningTrajectoryLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query() {
        return projectRunningTrajectoryLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query(Specification<ProjectRunningTrajectoryLog> spec) {
        return projectRunningTrajectoryLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query(Pageable pageable) {
        return projectRunningTrajectoryLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query(Specification<ProjectRunningTrajectoryLog> spec, Pageable pageable) {
        return projectRunningTrajectoryLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectRunningTrajectoryLog> getAll() {
        return projectRunningTrajectoryLogRepository.findAll();
    }

    @Override
    public void saveAll(List<ProjectRunningTrajectoryLog> saveList) {
        projectRunningTrajectoryLogRepository.saveAll(saveList);
    }
}
