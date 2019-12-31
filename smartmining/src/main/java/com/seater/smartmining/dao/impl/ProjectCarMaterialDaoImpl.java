package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarMaterialDaoI;
import com.seater.smartmining.entity.ProjectCarMaterial;
import com.seater.smartmining.entity.repository.ProjectCarMaterialRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
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
public class ProjectCarMaterialDaoImpl implements ProjectCarMaterialDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarMaterialRepository projectCarMaterialRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarmaterial:";

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
    public Page<ProjectCarMaterial> query(Specification<ProjectCarMaterial> spec, Pageable pageable) {
        return projectCarMaterialRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectCarMaterial> query(Specification<ProjectCarMaterial> spec) {
        return projectCarMaterialRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarMaterial> query(Pageable pageable) {
        return projectCarMaterialRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarMaterial> query() {
        return projectCarMaterialRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectCarMaterial get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarMaterial.class);
        }
        if(projectCarMaterialRepository.existsById(id))
        {
            ProjectCarMaterial log = projectCarMaterialRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCarMaterial save(ProjectCarMaterial log) throws IOException {
        ProjectCarMaterial log1 = projectCarMaterialRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarMaterialRepository.deleteById(id);
    }

    @Override
    public List<ProjectCarMaterial> getAll() {
        return projectCarMaterialRepository.findAll();
    }

    @Override
    public List<ProjectCarMaterial> getByProjectIdOrderById(Long projectId) {
        return projectCarMaterialRepository.getByProjectIdOrderById(projectId);
    }

    @Override
    public ProjectCarMaterial getPayableByProjectIdAndDistance(Long projectId, Long distance) {
        return projectCarMaterialRepository.getByProjectIdAndDistanceLessThanEqualOrderByDistanceDesc(projectId, distance).get(0);
    }

    @Override
    public Long getMaxDistanceByProjectId(Long projectId) {
        List<Long> list = projectCarMaterialRepository.getMaxDistanceByProjectId(projectId);
        if(list == null || list.size() <= 0)
            return null;

        return list.get(0);
    }

    @Override
    public Long getOverDistancePriceByProjectId(Long projectId) {
        List<Long> list = projectCarMaterialRepository.getOverDistancePriceByProjectId(projectId);
        if(list == null || list.size() <= 0)
            return null;

        return list.get(0);
    }
}