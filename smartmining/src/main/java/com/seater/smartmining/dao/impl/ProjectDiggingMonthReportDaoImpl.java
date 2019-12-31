package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingMonthReportDaoI;
import com.seater.smartmining.entity.ProjectDiggingMachineMaterial;
import com.seater.smartmining.entity.ProjectDiggingMonthReport;
import com.seater.smartmining.entity.repository.ProjectDiggingMonthReportRepository;
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
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/28 0028 18:39
 */
@Component
public class ProjectDiggingMonthReportDaoImpl implements ProjectDiggingMonthReportDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectDiggingMonthReportRepository projectDiggingMonthReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingmonthreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingMonthReport get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingMonthReport.class);
        }
        if(projectDiggingMonthReportRepository.existsById(id))
        {
            ProjectDiggingMonthReport log = projectDiggingMonthReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDiggingMonthReport save(ProjectDiggingMonthReport log) throws IOException {
        ProjectDiggingMonthReport log1 = projectDiggingMonthReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingMonthReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingMonthReport> query() {
        return projectDiggingMonthReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingMonthReport> query(Specification<ProjectDiggingMonthReport> spec) {
        return projectDiggingMonthReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingMonthReport> query(Pageable pageable) {
        return projectDiggingMonthReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingMonthReport> query(Specification<ProjectDiggingMonthReport> spec, Pageable pageable) {
        return projectDiggingMonthReportRepository.findAll(spec,pageable);
    }

    @Override
    public List<ProjectDiggingMonthReport> getAll() {
        return projectDiggingMonthReportRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDay) {
        projectDiggingMonthReportRepository.deleteByProjectIdAndReportDate(projectId, reportDay);
    }

    @Override
    public List<ProjectDiggingMonthReport> getAllByProjectId(Long projectId) {
        return projectDiggingMonthReportRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectDiggingMonthReport> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingMonthReportRepository.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<ProjectDiggingMonthReport> getByTotalId(Long totalId) {
        return projectDiggingMonthReportRepository.getByTotalId(totalId);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount, Long workTotalAmount) {
        projectDiggingMonthReportRepository.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount, workTotalAmount);
    }

    @Override
    public List<ProjectDiggingMonthReport> getByTotalIdAndOwnerId(Long totalId, Long ownerId) {
        return projectDiggingMonthReportRepository.getByTotalIdAndOwnerId(totalId,ownerId);
    }

    @Override
    public List<ProjectDiggingMonthReport> saveAll(List<ProjectDiggingMonthReport> saveList) {
        return projectDiggingMonthReportRepository.saveAll(saveList);
    }
}
