package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSlagCarLogDaoI;
import com.seater.smartmining.entity.ProjectSlagCarLog;
import com.seater.smartmining.entity.repository.ProjectSlagCarLogRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/16 0016 16:57
 */
@Component
public class ProjectSlagCarLogDaoImpl implements ProjectSlagCarLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSlagCarLogRepository projectSlagCarLogRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectslagcarlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSlagCarLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,  ProjectSlagCarLog.class);
        }
        if(projectSlagCarLogRepository.existsById(id))
        {
            ProjectSlagCarLog log = projectSlagCarLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSlagCarLog save(ProjectSlagCarLog log) throws IOException {
        ProjectSlagCarLog log1 = projectSlagCarLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSlagCarLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectSlagCarLog> query() {
        return projectSlagCarLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSlagCarLog> query(Specification<ProjectSlagCarLog> spec) {
        return projectSlagCarLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSlagCarLog> query(Pageable pageable) {
        return projectSlagCarLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSlagCarLog> query(Specification<ProjectSlagCarLog> spec, Pageable pageable) {
        return projectSlagCarLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSlagCarLog> getAll() {
        return projectSlagCarLogRepository.findAll();
    }

    @Override
    public List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectSlagCarLogRepository.getCarCountByProjectIDAndTime(projectId, startTime, endTime);
    }

    @Override
    public ProjectSlagCarLog getAllByProjectIDAndCarCodeAndTerminalTime(Long projectId, String carCode, Long terminalTime) {
        return projectSlagCarLogRepository.getAllByProjectIDAndCarCodeAndTerminalTime(projectId, carCode, terminalTime);
    }

    @Override
    public List<ProjectSlagCarLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        return projectSlagCarLogRepository.getAllByProjectIDAndTimeDischarge(projectId, startTime, endTime);
    }
}
