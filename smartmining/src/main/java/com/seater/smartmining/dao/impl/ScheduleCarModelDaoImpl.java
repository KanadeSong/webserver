package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ScheduleCarModelDaoI;
import com.seater.smartmining.entity.ScheduleCar;
import com.seater.smartmining.entity.ScheduleCarModel;
import com.seater.smartmining.entity.repository.ScheduleCarModelRepository;
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
 * @Date 2019/11/15 0015 10:53
 */
@Component
public class ScheduleCarModelDaoImpl implements ScheduleCarModelDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ScheduleCarModelRepository scheduleCarModelRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:schedulecarmodel:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ScheduleCarModel get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ScheduleCarModel.class);
        }
        if(scheduleCarModelRepository.existsById(id)){
            ScheduleCarModel log = scheduleCarModelRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ScheduleCarModel save(ScheduleCarModel log) throws JsonProcessingException {
        ScheduleCarModel log1 = scheduleCarModelRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        scheduleCarModelRepository.deleteById(id);
    }

    @Override
    public Page<ScheduleCarModel> query() {
        return scheduleCarModelRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleCarModel> query(Specification<ScheduleCarModel> spec) {
        return scheduleCarModelRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleCarModel> query(Pageable pageable) {
        return scheduleCarModelRepository.findAll(pageable);
    }

    @Override
    public Page<ScheduleCarModel> query(Specification<ScheduleCarModel> spec, Pageable pageable) {
        return scheduleCarModelRepository.findAll(spec, pageable);
    }

    @Override
    public List<ScheduleCarModel> getAll() {
        return scheduleCarModelRepository.findAll();
    }

    @Override
    public List<ScheduleCarModel> getAllByProjectId(Long projectId) {
        return scheduleCarModelRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleCarModel> getAllByProjectIdAndGroupCodeAndIsVaild(Long projectId, String groupCode, boolean valid) {
        return scheduleCarModelRepository.getAllByProjectIdAndGroupCodeAndIsVaild(projectId, groupCode, valid);
    }

    @Override
    public void deleteByProjectIdAndCarCode(Long projectId, String carCode) {
        scheduleCarModelRepository.deleteByProjectIdAndCarCode(projectId, carCode);
    }

    @Override
    public void deleteByProjectIdAndCarCodeListAndProgrammeId(Long projectId, List<String> carCodeList, Long programmeId) {
        scheduleCarModelRepository.deleteByProjectIdAndCarCodeListAndProgrammeId(projectId, carCodeList, programmeId);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleCarModelRepository.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ScheduleCarModel> saveList) {
        scheduleCarModelRepository.saveAll(saveList);
    }

    @Override
    public void deleteByGroupCodes(List<String> groupCodes) {
        scheduleCarModelRepository.deleteByGroupCodes(groupCodes);
    }

    @Override
    public ScheduleCarModel getAllByProjectIdAndGroupCodes(Long projectId, List<String> groupCodeList, String carCode) {
        return scheduleCarModelRepository.getAllByProjectIdAndGroupCodes(projectId, groupCodeList, carCode);
    }
}
