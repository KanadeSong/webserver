package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarTotalCountReportByTotalDaoI;
import com.seater.smartmining.entity.ProjectCarTotalCountReportByTotal;
import com.seater.smartmining.entity.repository.ProjectCarTotalCountReportByTotalRepository;
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
 * @Date 2019/11/22 0022 17:13
 */
@Component
public class ProjectCarTotalCountReportByTotalDaoImpl implements ProjectCarTotalCountReportByTotalDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarTotalCountReportByTotalRepository projectCarTotalCountReportByTotalRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcartotalcountreportbytotal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCarTotalCountReportByTotal get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarTotalCountReportByTotal.class);
        }
        if(projectCarTotalCountReportByTotalRepository.existsById(id))
        {
            ProjectCarTotalCountReportByTotal log = projectCarTotalCountReportByTotalRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCarTotalCountReportByTotal save(ProjectCarTotalCountReportByTotal log) throws IOException {
        ProjectCarTotalCountReportByTotal log1 = projectCarTotalCountReportByTotalRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarTotalCountReportByTotalRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query() {
        return projectCarTotalCountReportByTotalRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query(Specification<ProjectCarTotalCountReportByTotal> spec) {
        return projectCarTotalCountReportByTotalRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query(Pageable pageable) {
        return projectCarTotalCountReportByTotalRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query(Specification<ProjectCarTotalCountReportByTotal> spec, Pageable pageable) {
        return projectCarTotalCountReportByTotalRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCarTotalCountReportByTotal> queryAll(Specification<ProjectCarTotalCountReportByTotal> spec) {
        return projectCarTotalCountReportByTotalRepository.findAll(spec);
    }

    @Override
    public List<ProjectCarTotalCountReportByTotal> getAll() {
        return projectCarTotalCountReportByTotalRepository.findAll();
    }

    @Override
    public List<ProjectCarTotalCountReportByTotal> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectCarTotalCountReportByTotalRepository.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        projectCarTotalCountReportByTotalRepository.deleteByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }
}
