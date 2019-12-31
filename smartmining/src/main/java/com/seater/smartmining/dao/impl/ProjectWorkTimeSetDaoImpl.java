package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectWorkTimeSetDaoI;
import com.seater.smartmining.entity.ProjectWorkTimeSet;
import com.seater.smartmining.entity.repository.ProjectWorkTimeSetRepository;
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
public class ProjectWorkTimeSetDaoImpl implements ProjectWorkTimeSetDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectWorkTimeSetRepository projectWorkTimeSetRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectworktimeset:";

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
    public Page<ProjectWorkTimeSet> query(Specification<ProjectWorkTimeSet> spec, Pageable pageable) {
        return projectWorkTimeSetRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectWorkTimeSet> query(Specification<ProjectWorkTimeSet> spec) {
        return projectWorkTimeSetRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectWorkTimeSet> query(Pageable pageable) {
        return projectWorkTimeSetRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectWorkTimeSet> query() {
        return projectWorkTimeSetRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectWorkTimeSet get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectWorkTimeSet.class);
        }
        if(projectWorkTimeSetRepository.existsById(id))
        {
            ProjectWorkTimeSet log = projectWorkTimeSetRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectWorkTimeSet save(ProjectWorkTimeSet log) throws IOException {
        ProjectWorkTimeSet log1 = projectWorkTimeSetRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectWorkTimeSetRepository.deleteById(id);
    }

    @Override
    public List<ProjectWorkTimeSet> getAll() {
        return projectWorkTimeSetRepository.findAll();
    }

    //public List<ProjectWorkTimeSet> getByProjectId(Long projectId) { return projectWorkTimeSetRepository.getByProjectId(projectId); }
}
