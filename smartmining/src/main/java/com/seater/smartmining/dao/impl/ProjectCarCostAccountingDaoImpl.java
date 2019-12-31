package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarCostAccountingDaoI;
import com.seater.smartmining.entity.ProjectCarCostAccounting;
import com.seater.smartmining.entity.repository.ProjectCarCostAccountingRepository;
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
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/22 0022 9:39
 */
@Component
public class ProjectCarCostAccountingDaoImpl implements ProjectCarCostAccountingDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarCostAccountingRepository projectCarCostAccountingRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarcostaccounting:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCarCostAccounting get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectCarCostAccounting.class);
        }
        if(projectCarCostAccountingRepository.existsById(id)){
            ProjectCarCostAccounting log = projectCarCostAccountingRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectCarCostAccounting save(ProjectCarCostAccounting log) throws JsonProcessingException {
        ProjectCarCostAccounting log1 = projectCarCostAccountingRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarCostAccountingRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarCostAccounting> query() {
        return projectCarCostAccountingRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarCostAccounting> query(Specification<ProjectCarCostAccounting> spec) {
        return projectCarCostAccountingRepository.findAll(spec,PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarCostAccounting> query(Pageable pageable, Specification<ProjectCarCostAccounting> spec) {
        return projectCarCostAccountingRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectCarCostAccounting> query(Pageable pageable) {
        return projectCarCostAccountingRepository.findAll(pageable);
    }

    @Override
    public List<ProjectCarCostAccounting> getAll() {
        return projectCarCostAccountingRepository.findAll();
    }

    @Override
    public List<ProjectCarCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectCarCostAccountingRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectCarCostAccountingRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<Map> getCarAmountReport(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingRepository.getCarAmountReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarAmountReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingRepository.getCarAmountReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Date> getMaxReportDate() {
        return projectCarCostAccountingRepository.getMaxReportDate();
    }

    @Override
    public List<ProjectCarCostAccounting> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingRepository.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingRepository.getHistoryAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryAmountHistory(Long projectId, Date endTime) {
        return projectCarCostAccountingRepository.getHistoryAmountHistory(projectId, endTime);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingRepository.getHistoryFillAmountAndAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmountHistory(Long projectId, Date date) {
        return projectCarCostAccountingRepository.getHistoryFillAmountAndAmountHistory(projectId, date);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingRepository.getHistoryFillAmountAndAmountMonth(projectId, startTime, endTime);
    }

    @Override
    public Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingRepository.getAllByProjectIdAndReportDate(projectId, startTime, endTime);
    }
}
