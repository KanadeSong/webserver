package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarTotalCountReportDaoI;
import com.seater.smartmining.entity.ProjectCarTotalCountReport;
import com.seater.smartmining.entity.repository.ProjectCarTotalCountReportRepository;
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
 * @Date 2019/11/19 0019 11:44
 */
@Component
public class ProjectCarTotalCountReportDaoImpl implements ProjectCarTotalCountReportDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarTotalCountReportRepository projectCarTotalCountReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcartotalcountreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCarTotalCountReport get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarTotalCountReport.class);
        }
        if(projectCarTotalCountReportRepository.existsById(id))
        {
            ProjectCarTotalCountReport log = projectCarTotalCountReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCarTotalCountReport save(ProjectCarTotalCountReport log) throws IOException {
        ProjectCarTotalCountReport log1 = projectCarTotalCountReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarTotalCountReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarTotalCountReport> query() {
        return projectCarTotalCountReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarTotalCountReport> query(Specification<ProjectCarTotalCountReport> spec) {
        return projectCarTotalCountReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarTotalCountReport> query(Pageable pageable) {
        return projectCarTotalCountReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarTotalCountReport> query(Specification<ProjectCarTotalCountReport> spec, Pageable pageable) {
        return projectCarTotalCountReportRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCarTotalCountReport> getAll() {
        return projectCarTotalCountReportRepository.findAll();
    }

    @Override
    public void batchSave(List<ProjectCarTotalCountReport> reportList) {
        projectCarTotalCountReportRepository.saveAll(reportList);
    }

    @Override
    public void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        projectCarTotalCountReportRepository.deleteByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }
}
