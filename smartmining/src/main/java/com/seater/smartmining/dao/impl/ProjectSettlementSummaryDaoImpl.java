package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSettlementSummaryDaoI;
import com.seater.smartmining.entity.ProjectSettlementDetail;
import com.seater.smartmining.entity.ProjectSettlementSummary;
import com.seater.smartmining.entity.repository.ProjectSettlementDetailRepository;
import com.seater.smartmining.entity.repository.ProjectSettlementSummaryRespository;
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
 * @Date 2019/3/2 0002 17:40
 */
@Component
public class ProjectSettlementSummaryDaoImpl implements ProjectSettlementSummaryDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSettlementSummaryRespository projectSettlementSummaryRespository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectsettlementsummary:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSettlementSummary get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSettlementSummary.class);
        }
        if(projectSettlementSummaryRespository.existsById(id)){
            ProjectSettlementSummary log = projectSettlementSummaryRespository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSettlementSummary save(ProjectSettlementSummary log) throws JsonProcessingException {
        ProjectSettlementSummary log1 = projectSettlementSummaryRespository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSettlementSummaryRespository.deleteById(id);
    }

    @Override
    public Page<ProjectSettlementSummary> query() {
        return projectSettlementSummaryRespository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSettlementSummary> query(Specification<ProjectSettlementSummary> spec) {
        return projectSettlementSummaryRespository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSettlementSummary> query(Pageable pageable) {
        return projectSettlementSummaryRespository.findAll(pageable);
    }

    @Override
    public Page<ProjectSettlementSummary> query(Specification<ProjectSettlementSummary> spec, Pageable pageable) {
        return projectSettlementSummaryRespository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSettlementSummary> getAll() {
        return projectSettlementSummaryRespository.findAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId) {
        projectSettlementSummaryRespository.deleteByProjectIdAndReportDate(projectId, reportDate, carId);
    }

    @Override
    public List<ProjectSettlementSummary> getByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectSettlementSummaryRespository.getByProjectIdAndTotalId(projectId, totalId, reportDate);
    }
}
