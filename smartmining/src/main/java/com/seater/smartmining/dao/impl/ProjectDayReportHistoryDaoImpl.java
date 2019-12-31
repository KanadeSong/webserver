package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDayReportHistoryDaoI;
import com.seater.smartmining.entity.ProjectDayReportHistory;
import com.seater.smartmining.entity.repository.ProjectDayReportHistoryRepository;
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
 * @Date 2019/5/7 0007 15:50
 */
@Component
public class ProjectDayReportHistoryDaoImpl implements ProjectDayReportHistoryDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDayReportHistoryRepository projectDayReportHistoryRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdayreporthistory:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDayReportHistory get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDayReportHistory.class);
        }
        if(projectDayReportHistoryRepository.existsById(id))
        {
            ProjectDayReportHistory log = projectDayReportHistoryRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDayReportHistory save(ProjectDayReportHistory log) throws JsonProcessingException {
        ProjectDayReportHistory log1 = projectDayReportHistoryRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDayReportHistoryRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDayReportHistory> query() {
        return projectDayReportHistoryRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDayReportHistory> query(Specification<ProjectDayReportHistory> spec) {
        return projectDayReportHistoryRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDayReportHistory> query(Pageable pageable) {
        return projectDayReportHistoryRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDayReportHistory> query(Specification<ProjectDayReportHistory> spec, Pageable pageable) {
        return projectDayReportHistoryRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDayReportHistory> getAll() {
        return projectDayReportHistoryRepository.findAll();
    }

    @Override
    public ProjectDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportHistoryRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportHistoryRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }
}
