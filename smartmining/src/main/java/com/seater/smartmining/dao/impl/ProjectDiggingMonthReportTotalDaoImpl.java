package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingMonthReportTotalDaoI;
import com.seater.smartmining.entity.ProjectDiggingMonthReport;
import com.seater.smartmining.entity.ProjectDiggingMonthReportTotal;
import com.seater.smartmining.entity.repository.ProjectDiggingMonthReportTotalRepository;
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
 * @Date 2019/2/16 0016 13:31
 */
@Component
public class ProjectDiggingMonthReportTotalDaoImpl implements ProjectDiggingMonthReportTotalDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectDiggingMonthReportTotalRepository projectDiggingMonthReportTotalRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingmonthreporttotal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingMonthReportTotal get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingMonthReportTotal.class);
        }
        if(projectDiggingMonthReportTotalRepository.existsById(id)){
            ProjectDiggingMonthReportTotal log = projectDiggingMonthReportTotalRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingMonthReportTotal save(ProjectDiggingMonthReportTotal log) throws IOException {
        ProjectDiggingMonthReportTotal log1 = projectDiggingMonthReportTotalRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingMonthReportTotalRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query() {
        return projectDiggingMonthReportTotalRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query(Specification<ProjectDiggingMonthReportTotal> spec) {
        return projectDiggingMonthReportTotalRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query(Pageable pageable) {
        return projectDiggingMonthReportTotalRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query(Specification<ProjectDiggingMonthReportTotal> spec, Pageable pageable) {
        return projectDiggingMonthReportTotalRepository.findAll(spec, pageable);
    }


    @Override
    public List<ProjectDiggingMonthReportTotal> getAll() {
        return projectDiggingMonthReportTotalRepository.findAll();
    }

    @Override
    public List<ProjectDiggingMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingMonthReportTotalRepository.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingMonthReportTotalRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount) {
        projectDiggingMonthReportTotalRepository.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount);
    }
}
