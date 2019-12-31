package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMachineLocationDaoI;
import com.seater.smartmining.entity.ProjectMachineLocation;
import com.seater.smartmining.entity.repository.ProjectMachineLocationRepository;
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
 * @Date 2019/11/4 0004 21:07
 */
@Component
public class ProjectMachineLocationDaoImpl implements ProjectMachineLocationDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectMachineLocationRepository projectMachineLocationRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmachinelocation:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectMachineLocation get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMachineLocation.class);
        }
        if(projectMachineLocationRepository.existsById(id)){
            ProjectMachineLocation log = projectMachineLocationRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectMachineLocation save(ProjectMachineLocation log) throws IOException {
        ProjectMachineLocation log1 = projectMachineLocationRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMachineLocationRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectMachineLocation> query() {
        return projectMachineLocationRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMachineLocation> query(Specification<ProjectMachineLocation> spec) {
        return projectMachineLocationRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMachineLocation> query(Pageable pageable) {
        return projectMachineLocationRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMachineLocation> query(Specification<ProjectMachineLocation> spec, Pageable pageable) {
        return projectMachineLocationRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectMachineLocation> getAll() {
        return projectMachineLocationRepository.findAll();
    }

    @Override
    public void batchSave(List<ProjectMachineLocation> saveList) {
        projectMachineLocationRepository.saveAll(saveList);
    }

    @Override
    public List<ProjectMachineLocation> getAllByProjectIdAndCarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime) {
        return projectMachineLocationRepository.getAllByProjectIdAndCarCodeAndCreateTime(projectId, carCode, startTime, endTime);
    }
}
