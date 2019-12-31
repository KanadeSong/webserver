package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectScheduleModelDaoI;
import com.seater.smartmining.entity.ProjectScheduleModel;
import com.seater.smartmining.entity.repository.ProjectScheduleModelRepository;
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
 * @Date 2019/11/15 0015 10:35
 */
@Component
public class ProjectScheduleModelDaoImpl implements ProjectScheduleModelDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectScheduleModelRepository projectScheduleModelRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectschedulemodel:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectScheduleModel get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectScheduleModel.class);
        }
        if(projectScheduleModelRepository.existsById(id)){
            ProjectScheduleModel log = projectScheduleModelRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectScheduleModel save(ProjectScheduleModel log) throws JsonProcessingException {
        ProjectScheduleModel log1 = projectScheduleModelRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectScheduleModelRepository.deleteById(id);
    }

    @Override
    public Page<ProjectScheduleModel> query() {
        return projectScheduleModelRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectScheduleModel> query(Specification<ProjectScheduleModel> spec) {
        return projectScheduleModelRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectScheduleModel> query(Pageable pageable) {
        return projectScheduleModelRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectScheduleModel> query(Specification<ProjectScheduleModel> spec, Pageable pageable) {
        return projectScheduleModelRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectScheduleModel> getAll() {
        return projectScheduleModelRepository.findAll();
    }

    @Override
    public ProjectScheduleModel getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return projectScheduleModelRepository.getAllByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        projectScheduleModelRepository.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ProjectScheduleModel> saveList) {
        projectScheduleModelRepository.saveAll(saveList);
    }

    @Override
    public List<ProjectScheduleModel> getAllByProjectId(Long projectId) {
        return projectScheduleModelRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectScheduleModel> getAllByProjectIdAndProgrammeId(Long projectId, Long programmeId) {
        return projectScheduleModelRepository.getAllByProjectIdAndProgrammeId(projectId, programmeId);
    }

    @Override
    public void deleteByGroupCodes(List<String> groupCodes) {
        projectScheduleModelRepository.deleteByGroupCodes(groupCodes);
    }

    @Override
    public List<ProjectScheduleModel> getAllByQuery(Specification<ProjectScheduleModel> spec) {
        return projectScheduleModelRepository.findAll(spec);
    }
}
