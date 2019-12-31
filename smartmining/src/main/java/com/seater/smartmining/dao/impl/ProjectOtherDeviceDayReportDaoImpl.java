package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectOtherDeviceDayReportDaoI;
import com.seater.smartmining.entity.ProjectOtherDeviceDayReport;
import com.seater.smartmining.entity.repository.ProjectOtherDeviceDayReportRepository;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/19 0019 12:56
 */
@Component
public class ProjectOtherDeviceDayReportDaoImpl implements ProjectOtherDeviceDayReportDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectOtherDeviceDayReportRepository projectOtherDeviceDayReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectotherdevicedayreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}
    @Override
    public ProjectOtherDeviceDayReport get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectOtherDeviceDayReport.class);
        }
        if(projectOtherDeviceDayReportRepository.existsById(id))
        {
            ProjectOtherDeviceDayReport log = projectOtherDeviceDayReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectOtherDeviceDayReport save(ProjectOtherDeviceDayReport log) throws IOException {
        ProjectOtherDeviceDayReport log1 = projectOtherDeviceDayReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectOtherDeviceDayReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query() {
        return null;
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query(Specification<ProjectOtherDeviceDayReport> spec) {
        return projectOtherDeviceDayReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query(Pageable pageable) {
        return projectOtherDeviceDayReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query(Specification<ProjectOtherDeviceDayReport> spec, Pageable pageable) {
        return projectOtherDeviceDayReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public List<ProjectOtherDeviceDayReport> getAll() {
        return projectOtherDeviceDayReportRepository.findAll();
    }
}
