package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMonthReportTotalDaoI;
import com.seater.smartmining.entity.ProjectMonthReportTotal;
import com.seater.smartmining.entity.repository.ProjectMonthReportTotalRepository;
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
 * @Date 2019/2/19 0019 10:52
 */
@Component
public class ProjectMonthReportTotalDaoImpl implements ProjectMonthReportTotalDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectMonthReportTotalRepository projectMonthReportTotalRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmonthreporttotal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectMonthReportTotal get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMonthReportTotal.class);
        }
        if(projectMonthReportTotalRepository.existsById(id)){
            ProjectMonthReportTotal log = projectMonthReportTotalRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectMonthReportTotal save(ProjectMonthReportTotal log) throws JsonProcessingException {
        ProjectMonthReportTotal log1 = projectMonthReportTotalRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMonthReportTotalRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectMonthReportTotal> query() {
        return projectMonthReportTotalRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMonthReportTotal> query(Specification<ProjectMonthReportTotal> spec) {
        return projectMonthReportTotalRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMonthReportTotal> query(Pageable pageable) {
        return projectMonthReportTotalRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMonthReportTotal> query(Specification<ProjectMonthReportTotal> spec, Pageable pageable) {
        return projectMonthReportTotalRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectMonthReportTotal> getAll() {
        return projectMonthReportTotalRepository.findAll();
    }

    @Override
    public List<ProjectMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectMonthReportTotalRepository.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectMonthReportTotalRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount) {
        projectMonthReportTotalRepository.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount);
    }
}
