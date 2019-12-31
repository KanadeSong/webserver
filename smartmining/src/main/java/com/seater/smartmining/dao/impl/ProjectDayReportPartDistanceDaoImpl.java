package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDayReportPartDistanceDaoI;
import com.seater.smartmining.entity.ProjectDayReportPartDistance;
import com.seater.smartmining.entity.repository.ProjectDayReportPartDistanceRepository;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectDayReportPartDistanceDaoImpl implements ProjectDayReportPartDistanceDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDayReportPartDistanceRepository projectDayReportPartDistanceRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdayreportpartdistance:";

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
    public Page<ProjectDayReportPartDistance> query(Specification<ProjectDayReportPartDistance> spec, Pageable pageable) {
        return projectDayReportPartDistanceRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectDayReportPartDistance> query(Specification<ProjectDayReportPartDistance> spec) {
        return projectDayReportPartDistanceRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDayReportPartDistance> query(Pageable pageable) {
        return projectDayReportPartDistanceRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDayReportPartDistance> query() {
        return projectDayReportPartDistanceRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectDayReportPartDistance get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDayReportPartDistance.class);
        }
        if(projectDayReportPartDistanceRepository.existsById(id))
        {
            ProjectDayReportPartDistance log = projectDayReportPartDistanceRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDayReportPartDistance save(ProjectDayReportPartDistance log) throws IOException {
        ProjectDayReportPartDistance log1 = projectDayReportPartDistanceRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDayReportPartDistanceRepository.deleteById(id);
    }

    @Override
    public List<ProjectDayReportPartDistance> getAll() {
        return projectDayReportPartDistanceRepository.findAll();
    }

    @Override
    public List<ProjectDayReportPartDistance> getByReportIdOrderByDistance(Long reportId) {
        return projectDayReportPartDistanceRepository.getByReportIdOrderByDistance(reportId);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportPartDistanceRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<Map> getByProjectIdAndReportId(Long projectId, Long reportId) {
        return projectDayReportPartDistanceRepository.getByProjectIdAndReportId(projectId,reportId);
    }
}
