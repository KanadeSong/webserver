package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMonthReportDaoI;
import com.seater.smartmining.entity.ProjectMonthReport;
import com.seater.smartmining.entity.repository.ProjectMonthReportRepository;
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
 * @Date 2019/2/19 0019 10:39
 */
@Component
public class ProjectMonthReportDaoImpl implements ProjectMonthReportDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectMonthReportRepository projectMonthReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmonthreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectMonthReport get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMonthReport.class);
        }
        if(projectMonthReportRepository.existsById(id)){
            ProjectMonthReport log = projectMonthReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectMonthReport save(ProjectMonthReport log) throws IOException {
        ProjectMonthReport log1 = projectMonthReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMonthReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectMonthReport> query() {
        return projectMonthReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMonthReport> query(Specification<ProjectMonthReport> spec) {
        return projectMonthReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMonthReport> query(Pageable pageable) {
        return projectMonthReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMonthReport> query(Specification<ProjectMonthReport> spec, Pageable pageable) {
        return projectMonthReportRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectMonthReport> getAll() {
        return projectMonthReportRepository.findAll();
    }

    @Override
    public List<ProjectMonthReport> getByTotalId(Long totalId) {
        return projectMonthReportRepository.getByTotalId(totalId);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDay) {
        projectMonthReportRepository.deleteByProjectIdAndReportDate(projectId, reportDay);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount) {
        projectMonthReportRepository.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount);
    }

    @Override
    public List<ProjectMonthReport> getByTotalIdAndCarIdIn(Long totalId, List<Long> carIds) {
        return projectMonthReportRepository.getByTotalIdAndCarIdIn(totalId,carIds);
    }

    @Override
    public ProjectMonthReport getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate) {
        return projectMonthReportRepository.getAllByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
    }

}
