package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ScheduleCarDaoI;
import com.seater.smartmining.entity.ScheduleCar;
import com.seater.smartmining.entity.repository.ScheduleCarRepository;
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
 * @Date 2019/5/23 0023 14:48
 */
@Component
public class ScheduleCarDaoImpl implements ScheduleCarDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ScheduleCarRepository scheduleCarRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:schedulecar:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ScheduleCar get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ScheduleCar.class);
        }
        if(scheduleCarRepository.existsById(id)){
            ScheduleCar log = scheduleCarRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ScheduleCar save(ScheduleCar log) throws JsonProcessingException {
        ScheduleCar log1 = scheduleCarRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        scheduleCarRepository.deleteById(id);
    }

    @Override
    public Page<ScheduleCar> query() {
        return scheduleCarRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleCar> query(Specification<ScheduleCar> spec) {
        return scheduleCarRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleCar> query(Pageable pageable) {
        return scheduleCarRepository.findAll(pageable);
    }

    @Override
    public Page<ScheduleCar> query(Specification<ScheduleCar> spec, Pageable pageable) {
        return scheduleCarRepository.findAll(spec, pageable);
    }

    @Override
    public List<ScheduleCar> getAll() {
        return scheduleCarRepository.findAll();
    }

    @Override
    public List<ScheduleCar> getAllByProjectId(Long projectId) {
        return scheduleCarRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndCarIdAndIsVaild(Long projectId, Long carId, boolean flag) {
        return scheduleCarRepository.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, flag);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        //return scheduleCarRepository.getAllByProjectIdAndGroupCode(projectId, groupCode);
        return scheduleCarRepository.findByProjectIdAndGroupCodeAndIsVaildIsTrue(projectId, groupCode);
    }

    @Override
    public void deleteByProjectIdAndCarCode(Long projectId, String carCode) {
        scheduleCarRepository.deleteByProjectIdAndCarCode(projectId, carCode);
    }

    @Override
    public void deleteByProjectIdAndGroupCode(Long projectId, String groupCode) {
        scheduleCarRepository.deleteByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ScheduleCar> getAllByQuery(Specification<ScheduleCar> spec) {
        return scheduleCarRepository.findAll(spec);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleCarRepository.deleteByGroupCode(groupCode);
    }

    @Override
    public List<String> getGroupCodeList() {
        return scheduleCarRepository.getGroupCodeList();
    }

    @Override
    public ScheduleCar getAllByProjectIdAndCarCode(Long projectId, String carCode) {
        return scheduleCarRepository.getAllByProjectIdAndCarCode(projectId, carCode);
    }

    @Override
    public void batchSave(List<ScheduleCar> scheduleCarList) {
        scheduleCarRepository.saveAll(scheduleCarList);
    }

    @Override
    public List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid) {
        return scheduleCarRepository.getAllByProjectIdAndIsVaild(projectId, isValid);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        scheduleCarRepository.deleteByProjectId(projectId);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId) {
        return scheduleCarRepository.getAllByProjectIdAndIsVaildAndInSchedule(projectId);
    }

    @Override
    public void deleteByProjectIdAndCarCodeList(Long projectId, List<String> carCodeList) {
        scheduleCarRepository.deleteByProjectIdAndCarCodeList(projectId, carCodeList);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndIsVaildObject(Long projectId, Boolean isValid) {
        return scheduleCarRepository.getAllByProjectIdAndIsVaildObject(projectId, isValid);
    }
}
