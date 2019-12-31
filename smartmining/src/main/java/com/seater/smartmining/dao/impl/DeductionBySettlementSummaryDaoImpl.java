package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.DeductionBySettlementSummaryDaoI;
import com.seater.smartmining.entity.DeductionBySettlementSummary;
import com.seater.smartmining.entity.repository.DeductionBySettlementSummaryRepository;
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
 * @Date 2019/5/20 0020 10:15
 */
@Component
public class DeductionBySettlementSummaryDaoImpl implements DeductionBySettlementSummaryDaoI {

    @Autowired
    DeductionBySettlementSummaryRepository deductionBySettlementSummaryRepository;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:deductionbysettlementsummary:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public DeductionBySettlementSummary get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, DeductionBySettlementSummary.class);
        }
        if(deductionBySettlementSummaryRepository.existsById(id))
        {
            DeductionBySettlementSummary log = deductionBySettlementSummaryRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public DeductionBySettlementSummary save(DeductionBySettlementSummary log) throws IOException {
        DeductionBySettlementSummary log1 = deductionBySettlementSummaryRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        deductionBySettlementSummaryRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<DeductionBySettlementSummary> query() {
        return deductionBySettlementSummaryRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<DeductionBySettlementSummary> query(Specification<DeductionBySettlementSummary> spec) {
        return deductionBySettlementSummaryRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<DeductionBySettlementSummary> query(Pageable pageable) {
        return deductionBySettlementSummaryRepository.findAll(pageable);
    }

    @Override
    public Page<DeductionBySettlementSummary> query(Specification<DeductionBySettlementSummary> spec, Pageable pageable) {
        return deductionBySettlementSummaryRepository.findAll(spec,pageable);
    }

    @Override
    public List<DeductionBySettlementSummary> getAll() {
        return deductionBySettlementSummaryRepository.findAll();
    }

    @Override
    public DeductionBySettlementSummary getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate) {
        return deductionBySettlementSummaryRepository.getAllByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
    }

    @Override
    public List<DeductionBySettlementSummary> getAllByProjectIdAndReportDate(Long projectId, Date createDate) {
        return deductionBySettlementSummaryRepository.getAllByProjectIdAndReportDate(projectId, createDate);
    }

    /*@Override
    public DeductionBySettlementSummary getAllBySummaryIdAndProjectId(Long summaryId, Long projectId) {
        return deductionBySettlementSummaryRepository.getAllBySummaryIdAndProjectId(summaryId, projectId);
    }*/
}
