package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDaoI;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectWorkTimePoint;
import com.seater.smartmining.entity.repository.ProjectRepository;
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
import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectDaoImpl implements ProjectDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectRepository projectRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:project:";

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
    public Page<Project> query(Specification<Project> spec, Pageable pageable) {
        return projectRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Project> query(Specification<Project> spec) {
        return projectRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<Project> query(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Override
    public Page<Project> query() {
        return projectRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Project get(Long id) throws IOException {
        if(id == 0L) return null;

        /*String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, Project.class);
        }*/
        /*if(projectRepository.existsById(id))
        {*/
            Project log = projectRepository.findById(id).get();
            /*getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);*/
            return log;
        /*}*/

        /*return null;*/
    }

    @Override
    public Project save(Project log) throws IOException {
        Project log1 = projectRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectRepository.deleteById(id);
    }

    @Override
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    @Override
    public void setWorkTime(Long id, Time earlyStart, ProjectWorkTimePoint earlyEndPoint, Time earlyEnd, ProjectWorkTimePoint nightStartPoint, Time nightStart, ProjectWorkTimePoint nightEndPoint, Time nightEnd) {
        projectRepository.setWorkTime(id, earlyStart, earlyEndPoint, earlyEnd, nightStartPoint, nightStart, nightEndPoint, nightEnd);
    }

    @Override
    public Page<Project> findByUserId(Long userId, Pageable pageable) {
        return projectRepository.findByUserId(userId,pageable);
    }

}