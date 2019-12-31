package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSettlementDetailDaoI;
import com.seater.smartmining.entity.ProjectSettlementDetail;
import com.seater.smartmining.entity.repository.ProjectSettlementDetailRepository;
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
 * @Date 2019/3/2 0002 14:10
 */
@Component
public class ProjectSettlementDetailDaoImpl implements ProjectSettlementDetailDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSettlementDetailRepository projectSettlementDetailRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectsettlementdetail:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSettlementDetail get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSettlementDetail.class);
        }
        if(projectSettlementDetailRepository.existsById(id)){
            ProjectSettlementDetail log = projectSettlementDetailRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSettlementDetail save(ProjectSettlementDetail log) throws JsonProcessingException {
        ProjectSettlementDetail log1 = projectSettlementDetailRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSettlementDetailRepository.deleteById(id);
    }

    @Override
    public Page<ProjectSettlementDetail> query() {
        return projectSettlementDetailRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSettlementDetail> query(Specification<ProjectSettlementDetail> spec) {
        return projectSettlementDetailRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSettlementDetail> query(Pageable pageable) {
        return projectSettlementDetailRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSettlementDetail> query(Specification<ProjectSettlementDetail> spec, Pageable pageable) {
        return projectSettlementDetailRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSettlementDetail> getAll() {
        return projectSettlementDetailRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId) {
        projectSettlementDetailRepository.deleteByProjectIdAndReportDate(projectId, reportDate, carId);
    }

    @Override
    public List<ProjectSettlementDetail> getByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectSettlementDetailRepository.getByProjectIdAndTotalId(projectId, totalId, reportDate);
    }

    @Override
    public List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long carId, Long totalId) {
        return projectSettlementDetailRepository.getReportDateByProjectIdAndCarIdAndTotalId(projectId, carId, totalId);
    }

    @Override
    public List<Map> getTotalInfoByTotalId(Long totalId) {
        return projectSettlementDetailRepository.getTotalInfoByTotalId(totalId);
    }
}
