package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarLoadMaterialSetDaoI;
import com.seater.smartmining.entity.ProjectCarLoadMaterialSet;
import com.seater.smartmining.entity.repository.ProjectCarLoadMaterialSetRepository;
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

@Component
public class ProjectCarLoadMaterialSetDaoImpl implements ProjectCarLoadMaterialSetDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarLoadMaterialSetRepository projectCarLoadMaterialSetRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarloadmaterialset:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query(Specification<ProjectCarLoadMaterialSet> spec, Pageable pageable) {
        return projectCarLoadMaterialSetRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query(Specification<ProjectCarLoadMaterialSet> spec) {
        return projectCarLoadMaterialSetRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query(Pageable pageable) {
        return projectCarLoadMaterialSetRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query() {
        return projectCarLoadMaterialSetRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectCarLoadMaterialSet get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarLoadMaterialSet.class);
        }
        if(projectCarLoadMaterialSetRepository.existsById(id))
        {
            ProjectCarLoadMaterialSet log = projectCarLoadMaterialSetRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCarLoadMaterialSet save(ProjectCarLoadMaterialSet log) throws IOException {
        ProjectCarLoadMaterialSet log1 = projectCarLoadMaterialSetRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);

        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarLoadMaterialSetRepository.deleteById(id);
    }

    @Override
    public List<ProjectCarLoadMaterialSet> getAll() {
        return projectCarLoadMaterialSetRepository.findAll();
    }

   @Override
    public List<ProjectCarLoadMaterialSet> getByProjectIdAndCarIdOrderById(Long projectId, Long carId) {
        return projectCarLoadMaterialSetRepository.getByProjectIdAndCarIDOrderById(projectId, carId);
    }

    @Override
    public ProjectCarLoadMaterialSet getByProjectIdAndCarIDAndMaterialId(Long projectId, Long carId, Long materialId) {
        return projectCarLoadMaterialSetRepository.getByProjectIdAndCarIDAndMaterialId(projectId, carId, materialId);
    }
}
