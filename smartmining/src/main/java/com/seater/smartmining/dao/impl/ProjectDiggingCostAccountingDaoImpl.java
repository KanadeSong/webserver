package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingCostAccountingDaoI;
import com.seater.smartmining.entity.ProjectDiggingCostAccounting;
import com.seater.smartmining.entity.repository.ProjectDiggingCostAccountingRepository;
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

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/21 0021 9:05
 */
@Component
public class ProjectDiggingCostAccountingDaoImpl implements ProjectDiggingCostAccountingDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingCostAccountingRepository projectDiggingCostAccountingRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingcostaccounting:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingCostAccounting get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectDiggingCostAccounting.class);
        }
        if(projectDiggingCostAccountingRepository.existsById(id)){
            ProjectDiggingCostAccounting log = projectDiggingCostAccountingRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingCostAccounting save(ProjectDiggingCostAccounting log) throws JsonProcessingException {
        ProjectDiggingCostAccounting log1 = projectDiggingCostAccountingRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingCostAccountingRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query() {
        return projectDiggingCostAccountingRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query(Specification<ProjectDiggingCostAccounting> spec) {
        return projectDiggingCostAccountingRepository.findAll(spec,PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query(Pageable pageable) {
        return projectDiggingCostAccountingRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query(Specification<ProjectDiggingCostAccounting> spec, Pageable pageable) {
        return projectDiggingCostAccountingRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingCostAccounting> getAll() {
        return projectDiggingCostAccountingRepository.findAll();
    }

    @Override
    public List<ProjectDiggingCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate){
        return projectDiggingCostAccountingRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingCostAccountingRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingRepository.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndTimeMonth(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingRepository.getAllByProjectIdAndTimeMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndTimeHistory(Long projectId, Date endTime) {
        return projectDiggingCostAccountingRepository.getAllByProjectIdAndTimeHistory(projectId, endTime);
    }

    @Override
    public List<Date> getMaxReportDate() {
        return projectDiggingCostAccountingRepository.getMaxReportDate();
    }

    @Override
    public List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingRepository.getHistoryAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryAmountHistory(Long projectId, Date endTime) {
        return projectDiggingCostAccountingRepository.getHistoryAmountHistory(projectId, endTime);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingRepository.getHistoryFillAmountAndAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingRepository.getHistoryFillAmountAndAmountMonth(projectId, startTime, endTime);
    }

    @Override
    public Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingRepository.getAllByProjectIdAndReportDate(projectId, startTime, endTime);
    }
}
