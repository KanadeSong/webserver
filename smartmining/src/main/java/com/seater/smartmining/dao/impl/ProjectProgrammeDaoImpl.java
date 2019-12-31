package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectProgrammeDaoI;
import com.seater.smartmining.entity.ProjectProgramme;
import com.seater.smartmining.entity.repository.ProjectProgrammeRepository;
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
 * @Date 2019/11/14 0014 17:58
 */
@Component
public class ProjectProgrammeDaoImpl implements ProjectProgrammeDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectProgrammeRepository projectProgrammeRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectprogramme:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectProgramme get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectProgramme.class);
        }
        if(projectProgrammeRepository.existsById(id)){
            ProjectProgramme log = projectProgrammeRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectProgramme save(ProjectProgramme log) throws IOException {
        ProjectProgramme log1 = projectProgrammeRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectProgrammeRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectProgramme> query() {
        return projectProgrammeRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectProgramme> query(Specification<ProjectProgramme> spec) {
        return projectProgrammeRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectProgramme> query(Pageable pageable) {
        return projectProgrammeRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectProgramme> query(Specification<ProjectProgramme> spec, Pageable pageable) {
        return projectProgrammeRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectProgramme> getAll() {
        return projectProgrammeRepository.findAll();
    }
}
