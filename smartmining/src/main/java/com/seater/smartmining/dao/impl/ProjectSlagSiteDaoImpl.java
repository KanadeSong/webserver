package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSlagSiteDaoI;
import com.seater.smartmining.entity.ProjectSlagSite;
import com.seater.smartmining.entity.repository.ProjectSlagSiteRepository;
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
public class ProjectSlagSiteDaoImpl implements ProjectSlagSiteDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSlagSiteRepository projectSlagSiteRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectslagsite:";

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
    public Page<ProjectSlagSite> query(Specification<ProjectSlagSite> spec, Pageable pageable) {
        return projectSlagSiteRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectSlagSite> query(Specification<ProjectSlagSite> spec) {
        return projectSlagSiteRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSlagSite> query(Pageable pageable) {
        return projectSlagSiteRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSlagSite> query() {
        return projectSlagSiteRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectSlagSite get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSlagSite.class);
        }
        if(projectSlagSiteRepository.existsById(id))
        {
            ProjectSlagSite log = projectSlagSiteRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectSlagSite save(ProjectSlagSite log) throws IOException {
        ProjectSlagSite log1 = projectSlagSiteRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSlagSiteRepository.deleteById(id);
    }

    @Override
    public List<ProjectSlagSite> getAll() {
        return projectSlagSiteRepository.findAll();
    }

    @Override
    public ProjectSlagSite getByProjectIdAndDeviceUid(Long projectId, String deviceUid) {
        return projectSlagSiteRepository.getByProjectIdAndDeviceUid(projectId, deviceUid);
    }

    @Override
    public ProjectSlagSite getByProjectIdAndDistance(Long projectId, Long distance) {
        return projectSlagSiteRepository.getByProjectIdAndDistance(projectId, distance);
    }

    @Override
    public List<ProjectSlagSite> getAllByProjectId(Long projectId) {
        return projectSlagSiteRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectSlagSite> getAllByProjectIdAndName(Long projectId, String name) {
        return projectSlagSiteRepository.getAllByProjectIdAndName(projectId, name);
    }

    @Override
    public ProjectSlagSite getAllByProjectIdAndSlagSiteCode(Long projectId, String slagSiteCode) {
        return projectSlagSiteRepository.getAllByProjectIdAndSlagSiteCode(projectId, slagSiteCode);
    }
}
