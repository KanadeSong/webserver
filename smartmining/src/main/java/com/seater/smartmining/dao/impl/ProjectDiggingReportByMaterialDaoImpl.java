package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingReportByMaterialDaoI;
import com.seater.smartmining.entity.ProjectDiggingReportByMaterial;
import com.seater.smartmining.entity.repository.ProjectDiggingReportByMaterialRepository;
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
 * @Date 2019/8/20 0020 11:21
 */
@Component
public class ProjectDiggingReportByMaterialDaoImpl implements ProjectDiggingReportByMaterialDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingReportByMaterialRepository projectDiggingReportByMaterialRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingreportbymaterial:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingReportByMaterial get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingReportByMaterial.class);
        }
        if(projectDiggingReportByMaterialRepository.existsById(id))
        {
            ProjectDiggingReportByMaterial log = projectDiggingReportByMaterialRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDiggingReportByMaterial save(ProjectDiggingReportByMaterial log) throws IOException {
        ProjectDiggingReportByMaterial log1 = projectDiggingReportByMaterialRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingReportByMaterialRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query() {
        return projectDiggingReportByMaterialRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query(Specification<ProjectDiggingReportByMaterial> spec) {
        return projectDiggingReportByMaterialRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query(Pageable pageable) {
        return projectDiggingReportByMaterialRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query(Specification<ProjectDiggingReportByMaterial> spec, Pageable pageable) {
        return projectDiggingReportByMaterialRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingReportByMaterial> getAll() {
        return projectDiggingReportByMaterialRepository.findAll();
    }

    @Override
    public void batchSave(List<ProjectDiggingReportByMaterial> materialList) {
        projectDiggingReportByMaterialRepository.saveAll(materialList);
    }

    @Override
    public void deleteByProjectIdAndDateIdentification(Long projectId, Date date) {
        projectDiggingReportByMaterialRepository.deleteByProjectIdAndDateIdentification(projectId, date);
    }
}
