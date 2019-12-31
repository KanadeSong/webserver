package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingDayReportHistoryDaoI;
import com.seater.smartmining.entity.ProjectDiggingDayReportHistory;
import com.seater.smartmining.entity.repository.ProjectDiggingDayReportHistoryRepository;
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
 * @Date 2019/6/11 0011 16:31
 */
@Component
public class ProjectDiggingDayReportHistoryDaoImpl implements ProjectDiggingDayReportHistoryDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingDayReportHistoryRepository projectDiggingDayReportHistoryRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingdayreporthistory:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingDayReportHistory get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingDayReportHistory.class);
        }
        if(projectDiggingDayReportHistoryRepository.existsById(id))
        {
            ProjectDiggingDayReportHistory log = projectDiggingDayReportHistoryRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDiggingDayReportHistory save(ProjectDiggingDayReportHistory log) throws JsonProcessingException {
        ProjectDiggingDayReportHistory log1 = projectDiggingDayReportHistoryRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingDayReportHistoryRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query() {
        return projectDiggingDayReportHistoryRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query(Specification<ProjectDiggingDayReportHistory> spec) {
        return projectDiggingDayReportHistoryRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query(Pageable pageable) {
        return projectDiggingDayReportHistoryRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query(Specification<ProjectDiggingDayReportHistory> spec, Pageable pageable) {
        return projectDiggingDayReportHistoryRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingDayReportHistory> getAll() {
        return projectDiggingDayReportHistoryRepository.findAll();
    }

    @Override
    public ProjectDiggingDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingDayReportHistoryRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingDayReportHistoryRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }
}
