package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarCountDaoI;
import com.seater.smartmining.entity.ProjectCarCount;
import com.seater.smartmining.entity.repository.ProjectCarCountRepository;
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
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/15 0015 14:09
 */
@Component
public class ProjectCarCountDaoImpl implements ProjectCarCountDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarCountRepository projectCarCountRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarcount:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCarCount get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarCount.class);
        }
        if(projectCarCountRepository.existsById(id))
        {
            ProjectCarCount log = projectCarCountRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCarCount save(ProjectCarCount log) throws IOException {
        ProjectCarCount log1 = projectCarCountRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarCountRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarCount> query() {
        return projectCarCountRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarCount> query(Specification<ProjectCarCount> spec) {
        return projectCarCountRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarCount> query(Pageable pageable) {
        return projectCarCountRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarCount> query(Specification<ProjectCarCount> spec, Pageable pageable) {
        return projectCarCountRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCarCount> getAll() {
        return projectCarCountRepository.findAll();
    }

    @Override
    public ProjectCarCount getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(Long projectId, String carCode, Date date, Integer shifts, Integer carType) {
        return projectCarCountRepository.getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(projectId, carCode, date, shifts, carType);
    }

    @Override
    public List<ProjectCarCount> getAllByProjectIdAndDateIdentificationAndShiftsAndCarType(Long projectId, Date date, Integer shift, Integer carType) {
        return projectCarCountRepository.getAllByProjectIdAndDateIdentificationAndShiftsAndCarType(projectId, date, shift, carType);
    }
}
