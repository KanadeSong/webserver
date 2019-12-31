package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingMachineMaterialDaoI;
import com.seater.smartmining.entity.ProjectDiggingMachineMaterial;
import com.seater.smartmining.entity.repository.ProjectDiggingMachineMaterialRepository;
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
public class ProjectDiggingMachineMaterialDaoImpl implements ProjectDiggingMachineMaterialDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingMachineMaterialRepository projectDiggingMachineMaterialRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingmachinematerial:";

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
    public Page<ProjectDiggingMachineMaterial> query(Specification<ProjectDiggingMachineMaterial> spec, Pageable pageable) {
        return projectDiggingMachineMaterialRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectDiggingMachineMaterial> query(Specification<ProjectDiggingMachineMaterial> spec) {
        return projectDiggingMachineMaterialRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingMachineMaterial> query(Pageable pageable) {
        return projectDiggingMachineMaterialRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingMachineMaterial> query() {
        return projectDiggingMachineMaterialRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectDiggingMachineMaterial get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingMachineMaterial.class);
        }
        if(projectDiggingMachineMaterialRepository.existsById(id))
        {
            ProjectDiggingMachineMaterial log = projectDiggingMachineMaterialRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDiggingMachineMaterial save(ProjectDiggingMachineMaterial log) throws IOException {
        ProjectDiggingMachineMaterial log1 = projectDiggingMachineMaterialRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingMachineMaterialRepository.deleteById(id);
    }

    @Override
    public List<ProjectDiggingMachineMaterial> getAll() {
        return projectDiggingMachineMaterialRepository.findAll();
    }

    @Override
    public List<ProjectDiggingMachineMaterial> getByProjectIdOrderById(Long projectId) {
        return projectDiggingMachineMaterialRepository.getByProjectIdOrderById(projectId);
    }

    @Override
    public List<ProjectDiggingMachineMaterial> getByProjectIdAndMaterialId(Long projectId, Long materialId) {
        return projectDiggingMachineMaterialRepository.getByProjectIdAndMaterialId(projectId,materialId);
    }
}