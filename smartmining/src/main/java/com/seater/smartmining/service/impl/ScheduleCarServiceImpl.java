package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ScheduleCarDaoI;
import com.seater.smartmining.entity.ScheduleCar;
import com.seater.smartmining.service.ScheduleCarServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 15:29
 */
@Service
public class ScheduleCarServiceImpl implements ScheduleCarServiceI  {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ScheduleCarDaoI scheduleCarDaoI;

    @Override
    public ScheduleCar get(Long id) throws IOException {
        return scheduleCarDaoI.get(id);
    }

    @Override
    public ScheduleCar save(ScheduleCar log) throws JsonProcessingException {
        return scheduleCarDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        scheduleCarDaoI.delete(id);
    }

    @Override
    public Page<ScheduleCar> query() {
        return scheduleCarDaoI.query();
    }

    @Override
    public Page<ScheduleCar> query(Specification<ScheduleCar> spec) {
        return scheduleCarDaoI.query(spec);
    }

    @Override
    public Page<ScheduleCar> query(Pageable pageable) {
        return scheduleCarDaoI.query(pageable);
    }

    @Override
    public Page<ScheduleCar> query(Specification<ScheduleCar> spec, Pageable pageable) {
        return scheduleCarDaoI.query(spec, pageable);
    }

    @Override
    public List<ScheduleCar> getAll() {
        return scheduleCarDaoI.getAll();
    }

    @Override
    public List<ScheduleCar> getAllByProjectId(Long projectId) {
        return scheduleCarDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndCarIdAndIsVaild(Long projectId, Long carId, boolean flag) {
        return scheduleCarDaoI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, flag);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return scheduleCarDaoI.getAllByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public void deleteByProjectIdAndCarCode(Long projectId, String carCode) {
        scheduleCarDaoI.deleteByProjectIdAndCarCode(projectId, carCode);
    }

    @Override
    public void deleteByProjectIdAndGroupCode(Long projectId, String groupCode) {
        scheduleCarDaoI.deleteByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ScheduleCar> getAllByQuery(Specification<ScheduleCar> spec) {
        return scheduleCarDaoI.getAllByQuery(spec);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleCarDaoI.deleteByGroupCode(groupCode);
    }

    @Override
    public List<String> getGroupCodeList() {
        return scheduleCarDaoI.getGroupCodeList();
    }

    @Override
    public ScheduleCar getAllByProjectIdAndCarCode(Long projectId, String carCode) {
        return scheduleCarDaoI.getAllByProjectIdAndCarCode(projectId, carCode);
    }

    @Override
    public void batchSave(List<ScheduleCar> scheduleCarList) {
        scheduleCarDaoI.batchSave(scheduleCarList);
    }

    @Override
    public List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid) {
        return scheduleCarDaoI.getAllByProjectIdAndIsVaild(projectId, isValid);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        scheduleCarDaoI.deleteByProjectId(projectId);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId) {
        return scheduleCarDaoI.getAllByProjectIdAndIsVaildAndInSchedule(projectId);
    }

    @Override
    public void deleteByProjectIdAndCarCodeList(Long projectId, List<String> carCodeList) {
        scheduleCarDaoI.deleteByProjectIdAndCarCodeList(projectId, carCodeList);
    }

    @Override
    public List<ScheduleCar> getAllByProjectIdAndIsVaildObject(Long projectId, Boolean isValid) {
        return scheduleCarDaoI.getAllByProjectIdAndIsVaildObject(projectId, isValid);
    }
}
