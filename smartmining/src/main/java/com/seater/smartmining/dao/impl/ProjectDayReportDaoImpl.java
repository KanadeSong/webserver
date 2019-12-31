package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDayReportDaoI;
import com.seater.smartmining.entity.ProjectDayReport;
import com.seater.smartmining.entity.repository.ProjectDayReportRepository;
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
public class ProjectDayReportDaoImpl implements ProjectDayReportDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDayReportRepository projectDayReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdayreport:";

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
    public Page<ProjectDayReport> query(Specification<ProjectDayReport> spec, Pageable pageable) {
        return projectDayReportRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectDayReport> query(Specification<ProjectDayReport> spec) {
        return projectDayReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDayReport> query(Pageable pageable) {
        return projectDayReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDayReport> query() {
        return projectDayReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectDayReport get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDayReport.class);
        }
        if(projectDayReportRepository.existsById(id))
        {
            ProjectDayReport log = projectDayReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDayReport save(ProjectDayReport log) throws IOException {
        ProjectDayReport log1 = projectDayReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDayReportRepository.deleteById(id);
    }

    @Override
    public List<ProjectDayReport> getAll() {
        return projectDayReportRepository.findAll();
    }

    @Override
    public ProjectDayReport getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportRepository.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public Map getOnDutyCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDayReportRepository.getOnDutyCountByProjectIdAndReportDate(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDayReport> getSettlementDetailByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDayReportRepository.getSettlementDetailByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDayReport> getByProjectIdAndCreateDate(Long projectId, Date createDate) {
        return projectDayReportRepository.getByProjectIdAndCreateDate(projectId, createDate);
    }

    @Override
    public List<Map> getAvgCarInfo(Long projectId, Date startTime, Date endTime) {
        return projectDayReportRepository.getAvgCarInfo(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAvgCarInfoMonth(Long projectId, Date startTime, Date endTime) {
        return projectDayReportRepository.getAvgCarInfoMonth(projectId, startTime, endTime);
    }
}
