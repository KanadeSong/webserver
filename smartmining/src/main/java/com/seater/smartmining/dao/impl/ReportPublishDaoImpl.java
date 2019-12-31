package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ReportPublishDaoI;
import com.seater.smartmining.entity.ReportPublish;
import com.seater.smartmining.entity.repository.ReportPublishRepository;
import com.seater.smartmining.enums.ReportEnum;
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
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:39
 */
@Component
public class ReportPublishDaoImpl implements ReportPublishDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ReportPublishRepository reportPublishRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:reportPublish:";

    String getKey(Long id) {
        return keyGroup + id.toString();
    }

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }


    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public Page<ReportPublish> query(Specification<ReportPublish> spec, Pageable pageable) {
        return reportPublishRepository.findAll(spec, pageable);
    }

    @Override
    public ReportPublish query(Specification<ReportPublish> spec) {
        return reportPublishRepository.findOne(spec).get();
    }

    @Override
    public Page<ReportPublish> query(Pageable pageable) {
        return reportPublishRepository.findAll(pageable);
    }

    @Override
    public Page<ReportPublish> query() {
        return reportPublishRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ReportPublish get(Long id) throws IOException {
        if (id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if (obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ReportPublish.class);
        }
        if (reportPublishRepository.existsById(id)) {
            ReportPublish log = reportPublishRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ReportPublish save(ReportPublish log) throws IOException {
        ReportPublish log1 = reportPublishRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if (id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        reportPublishRepository.deleteById(id);
    }

    @Override
    public List<ReportPublish> getAll() {
        return reportPublishRepository.findAll();
    }

    @Override
    public List<ReportPublish> queryWx(Specification<ReportPublish> spec) {
        return reportPublishRepository.findAll(spec);
    }

    @Override
    public ReportPublish findByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return reportPublishRepository.findByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public ReportPublish findByProjectIdAndReportDateAndReportEnum(Long projectId, Date reportDate, ReportEnum reportEnum) {
        return reportPublishRepository.findByProjectIdAndReportDateAndReportEnum(projectId, reportDate, reportEnum);
    }
}
