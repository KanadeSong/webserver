package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingDayReportTotalDaoI;
import com.seater.smartmining.entity.ProjectDiggingDayReport;
import com.seater.smartmining.entity.ProjectDiggingDayReportTotal;
import com.seater.smartmining.entity.repository.ProjectDiggingDayReportTotalRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/15 0015 15:29
 */
@Component
public class ProjectDiggingDayReportTotalDaoImpl implements ProjectDiggingDayReportTotalDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingDayReportTotalRepository projectDiggingDayReportTotalRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingdayreporttotal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public ProjectDiggingDayReportTotal get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingDayReportTotal.class);
        }
        if(projectDiggingDayReportTotalRepository.existsById(id)){
            ProjectDiggingDayReportTotal log = projectDiggingDayReportTotalRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingDayReportTotal save(ProjectDiggingDayReportTotal log) throws JsonProcessingException {
        ProjectDiggingDayReportTotal log1 = projectDiggingDayReportTotalRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingDayReportTotalRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingDayReportTotal> query() {
        return projectDiggingDayReportTotalRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public List<ProjectDiggingDayReportTotal> getAll() {
        return projectDiggingDayReportTotalRepository.findAll();
    }

    @Override
    public List<ProjectDiggingDayReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingDayReportTotalRepository.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingDayReportTotalRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<ProjectDiggingDayReportTotal> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDiggingDayReportTotalRepository.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }
}
