package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCubicDetailDaoI;
import com.seater.smartmining.entity.ProjectCubicDetail;
import com.seater.smartmining.entity.repository.ProjectCubicDetailRepository;
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
 * @Date 2019/3/4 0004 16:38
 */
@Component
public class ProjectCubicDetailDaoImpl implements ProjectCubicDetailDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCubicDetailRepository projectCubicDetailRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcubicdetail:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCubicDetail get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectCubicDetail.class);
        }
        if(projectCubicDetailRepository.existsById(id)){
            ProjectCubicDetail log = projectCubicDetailRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectCubicDetail save(ProjectCubicDetail log) throws JsonProcessingException {
        ProjectCubicDetail log1 = projectCubicDetailRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCubicDetailRepository.deleteById(id);
    }

    @Override
    public Page<ProjectCubicDetail> query() {
        return projectCubicDetailRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCubicDetail> query(Specification<ProjectCubicDetail> spec) {
        return projectCubicDetailRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCubicDetail> query(Pageable pageable) {
        return projectCubicDetailRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCubicDetail> query(Specification<ProjectCubicDetail> spec, Pageable pageable) {
        return projectCubicDetailRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCubicDetail> getAll() {
        return projectCubicDetailRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId) {
        projectCubicDetailRepository.deleteByProjectIdAndCreateDateAndMachineId(projectId, createDate, machineId);
    }

    @Override
    public List<ProjectCubicDetail> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectCubicDetailRepository.getAllByProjectIdAndTotalId(projectId, totalId, reportDate);
    }

    @Override
    public List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long machineId, Long totalId) {
        return projectCubicDetailRepository.getReportDateByProjectIdAndCarIdAndTotalId(projectId, machineId, totalId);
    }

    @Override
    public List<Map> getByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectCubicDetailRepository.getByProjectIdAndReportDate(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId) {
        return projectCubicDetailRepository.getByProjectIdAndReportDateAndMachineId(projectId, startTime, endTime, machineId);
    }

    @Override
    public List<Map> getByProjectIdAndDate(Long projectId, Date startTime, Date endTime) {
        return projectCubicDetailRepository.getByProjectIdAndDate(projectId, startTime, endTime);
    }
}
