package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingDayReportDaoI;
import com.seater.smartmining.entity.ProjectDiggingDayReport;
import com.seater.smartmining.entity.repository.ProjectDiggingDayReportRepository;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/12 0012 16:23
 */
@Component
public class ProjectDiggingDayReportDaoImpl implements ProjectDiggingDayReportDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingDayReportRepository projectDiggingDayReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingdayreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingDayReport get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingDayReport.class);
        }
        if(projectDiggingDayReportRepository.existsById(id)){
            ProjectDiggingDayReport log = projectDiggingDayReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingDayReport save(ProjectDiggingDayReport log) throws JsonProcessingException {
        ProjectDiggingDayReport log1 = projectDiggingDayReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingDayReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingDayReport> query() {
        return projectDiggingDayReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingDayReport> query(Specification<ProjectDiggingDayReport> spec) {
        return projectDiggingDayReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingDayReport> query(Pageable pageable) {
        return projectDiggingDayReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingDayReport> query(Specification<ProjectDiggingDayReport> spec, Pageable pageable) {
        return projectDiggingDayReportRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingDayReport> getAll() {
        return projectDiggingDayReportRepository.findAll();
    }

    @Override
    public List<ProjectDiggingDayReport> getByTotalId(Long totalId) {
        return projectDiggingDayReportRepository.getByTotalId(totalId);
    }

    @Override
    public List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startDate, Date endDate) {
        return projectDiggingDayReportRepository.getMonthReportByProjectIdAndReportDate(projectId, startDate, endDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingDayReportRepository.deleteByProjectIdAndReportDate(projectId,reportDate);
    }

    @Override
    public Map getTotalInfoByProjectIdAndTime(Long projectId, Date reportDate) {
        return projectDiggingDayReportRepository.getTotalInfoByProjectIdAndTime(projectId, reportDate);
    }

    @Override
    public Map getGrandInfoByProjectIdAndTime(Long projectId, Date reportDate) {
        return projectDiggingDayReportRepository.getGrandInfoByProjectIdAndTime(projectId, reportDate);
    }

    @Override
    public Map getHistoryInfoByProjectId(Long projectId) {
        return projectDiggingDayReportRepository.getHistoryInfoByProjectId(projectId);
    }

    @Override
    public void setDeductionTimeByDayAndDeductionTimeByNightOrderById(Long id, BigDecimal deductionTimeByDay, BigDecimal deductionTimeByNight) {
        projectDiggingDayReportRepository.setDeductionTimeByDayAndDeductionTimeByNightOrderById(id, deductionTimeByDay, deductionTimeByNight);
    }

    @Override
    public List<Map> getCubicDetailByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId) {
        return projectDiggingDayReportRepository.getCubicDetailByProjectIdAndReportDateAndMachineId(projectId, startTime, endTime, machineId);
    }

    @Override
    public List<ProjectDiggingDayReport> queryWx(Specification<ProjectDiggingDayReport> spec) {
        return projectDiggingDayReportRepository.findAll(spec);
    }

    @Override
    public List<ProjectDiggingDayReport> getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate) {
        return projectDiggingDayReportRepository.getAllByProjectIdAndMachineIdAndReportDate(projectId, machineId, reportDate);
    }

    @Override
    public void batchSave(List<ProjectDiggingDayReport> reportList) {
        projectDiggingDayReportRepository.saveAll(reportList);
    }
}
