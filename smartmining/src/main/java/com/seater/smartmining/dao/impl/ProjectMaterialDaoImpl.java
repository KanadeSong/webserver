package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMaterialDaoI;
import com.seater.smartmining.entity.ProjectMaterial;
import com.seater.smartmining.entity.repository.ProjectMaterialRepository;
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
public class ProjectMaterialDaoImpl implements ProjectMaterialDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectMaterialRepository projectMaterialRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmaterial:";

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
    public Page<ProjectMaterial> query(Specification<ProjectMaterial> spec, Pageable pageable) {
        return projectMaterialRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectMaterial> query(Specification<ProjectMaterial> spec) {
        return projectMaterialRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMaterial> query(Pageable pageable) {
        return projectMaterialRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMaterial> query() {
        return projectMaterialRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectMaterial get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMaterial.class);
        }
        if(projectMaterialRepository.existsById(id))
        {
            ProjectMaterial log = projectMaterialRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectMaterial save(ProjectMaterial log) throws IOException {
        ProjectMaterial log1 = projectMaterialRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMaterialRepository.deleteById(id);
    }

    @Override
    public List<ProjectMaterial> getAll() {
        return projectMaterialRepository.findAll();
    }

    @Override
    public List<ProjectMaterial> getByProjectIdOrderById(Long projectId) {
        return projectMaterialRepository.getByProjectIdOrderById(projectId);
    }

    @Override
    public ProjectMaterial getByProjectIdAndName(Long projectId, String name) {
        return projectMaterialRepository.getByProjectIdAndName(projectId, name);
    }
}