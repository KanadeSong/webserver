package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCubicDetailElseDaoI;
import com.seater.smartmining.entity.ProjectCubicDetailElse;
import com.seater.smartmining.entity.repository.ProjectCubicDetailElseRepository;
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
 * @Date 2019/3/5 0005 10:56
 */
@Component
public class ProjectCubicDetailElseDaoImpl implements ProjectCubicDetailElseDaoI {


    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCubicDetailElseRepository projectCubicDetailElseRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcubicdetailelse:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCubicDetailElse get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectCubicDetailElse.class);
        }
        if(projectCubicDetailElseRepository.existsById(id)){
            ProjectCubicDetailElse log = projectCubicDetailElseRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectCubicDetailElse save(ProjectCubicDetailElse log) throws JsonProcessingException {
        ProjectCubicDetailElse log1 = projectCubicDetailElseRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCubicDetailElseRepository.deleteById(id);
    }

    @Override
    public Page<ProjectCubicDetailElse> query() {
        return projectCubicDetailElseRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCubicDetailElse> query(Specification<ProjectCubicDetailElse> spec) {
        return projectCubicDetailElseRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCubicDetailElse> query(Pageable pageable) {
        return projectCubicDetailElseRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCubicDetailElse> query(Specification<ProjectCubicDetailElse> spec, Pageable pageable) {
        return projectCubicDetailElseRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCubicDetailElse> getAll() {
        return projectCubicDetailElseRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId) {
        projectCubicDetailElseRepository.deleteByProjectIdAndCreateDateAndMachineId(projectId, createDate, machineId);
    }

    @Override
    public List<ProjectCubicDetailElse> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectCubicDetailElseRepository.getAllByProjectIdAndTotalId(projectId, totalId, reportDate);
    }
}
