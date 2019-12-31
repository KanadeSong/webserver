package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ScheduleCarModelDaoI;
import com.seater.smartmining.entity.ScheduleCarModel;
import com.seater.smartmining.service.ScheduleCarModelServiceI;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Date 2019/11/15 0015 11:00
 */
@Service
public class ScheduleCarModelServiceImpl implements ScheduleCarModelServiceI {

    @Autowired
    private ScheduleCarModelDaoI scheduleCarModelDaoI;

    @Override
    public ScheduleCarModel get(Long id) throws IOException {
        return scheduleCarModelDaoI.get(id);
    }

    @Override
    public ScheduleCarModel save(ScheduleCarModel log) throws JsonProcessingException {
        return scheduleCarModelDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        scheduleCarModelDaoI.delete(id);
    }

    @Override
    public Page<ScheduleCarModel> query() {
        return scheduleCarModelDaoI.query();
    }

    @Override
    public Page<ScheduleCarModel> query(Specification<ScheduleCarModel> spec) {
        return scheduleCarModelDaoI.query(spec);
    }

    @Override
    public Page<ScheduleCarModel> query(Pageable pageable) {
        return scheduleCarModelDaoI.query(pageable);
    }

    @Override
    public Page<ScheduleCarModel> query(Specification<ScheduleCarModel> spec, Pageable pageable) {
        return scheduleCarModelDaoI.query(spec, pageable);
    }

    @Override
    public List<ScheduleCarModel> getAll() {
        return scheduleCarModelDaoI.getAll();
    }

    @Override
    public List<ScheduleCarModel> getAllByProjectId(Long projectId) {
        return scheduleCarModelDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleCarModel> getAllByProjectIdAndGroupCodeAndIsVaild(Long projectId, String groupCode, boolean valid) {
        return scheduleCarModelDaoI.getAllByProjectIdAndGroupCodeAndIsVaild(projectId, groupCode, valid);
    }

    @Override
    public void deleteByProjectIdAndCarCode(Long projectId, String carCode) {
        scheduleCarModelDaoI.deleteByProjectIdAndCarCode(projectId, carCode);
    }

    @Override
    public void deleteByProjectIdAndCarCodeListAndProgrammeId(Long projectId, List<String> carCodeList, Long programmeId) {
        scheduleCarModelDaoI.deleteByProjectIdAndCarCodeListAndProgrammeId(projectId, carCodeList, programmeId);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleCarModelDaoI.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ScheduleCarModel> saveList) {
        scheduleCarModelDaoI.batchSave(saveList);
    }

    @Override
    public void deleteByGroupCodes(List<String> groupCodes) {
        scheduleCarModelDaoI.deleteByGroupCodes(groupCodes);
    }

    @Override
    public ScheduleCarModel getAllByProjectIdAndGroupCodes(Long projectId, List<String> groupCodeList, String carCode) {
        return scheduleCarModelDaoI.getAllByProjectIdAndGroupCodes(projectId, groupCodeList, carCode);
    }
}
