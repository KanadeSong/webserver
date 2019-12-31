package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCubicDetailTotalDaoI;
import com.seater.smartmining.entity.ProjectCubicDetailTotal;
import com.seater.smartmining.entity.repository.ProjectCubicDetailTotalRepository;
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
 * @Date 2019/3/4 0004 16:58
 */
@Component
public class ProjectCubicDetailTotalDaoImpl implements ProjectCubicDetailTotalDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCubicDetailTotalRepository projectCubicDetailTotalRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcubicdetailtotal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCubicDetailTotal get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectCubicDetailTotal.class);
        }
        if(projectCubicDetailTotalRepository.existsById(id)){
            ProjectCubicDetailTotal log = projectCubicDetailTotalRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectCubicDetailTotal save(ProjectCubicDetailTotal log) throws JsonProcessingException {
        ProjectCubicDetailTotal log1 = projectCubicDetailTotalRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCubicDetailTotalRepository.deleteById(id);
    }

    @Override
    public Page<ProjectCubicDetailTotal> query() {
        return projectCubicDetailTotalRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCubicDetailTotal> query(Specification<ProjectCubicDetailTotal> spec) {
        return projectCubicDetailTotalRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCubicDetailTotal> query(Pageable pageable) {
        return projectCubicDetailTotalRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCubicDetailTotal> query(Specification<ProjectCubicDetailTotal> spec, Pageable pageable) {
        return projectCubicDetailTotalRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCubicDetailTotal> getAll() {
        return projectCubicDetailTotalRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectCubicDetailTotalRepository.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public List<ProjectCubicDetailTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        return projectCubicDetailTotalRepository.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }
}
