package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ScheduleCarModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:53
 */
public interface ScheduleCarModelDaoI {

    ScheduleCarModel get(Long id) throws IOException;
    ScheduleCarModel save(ScheduleCarModel log) throws JsonProcessingException;
    void delete(Long id);
    Page<ScheduleCarModel> query();
    Page<ScheduleCarModel> query(Specification<ScheduleCarModel> spec);
    Page<ScheduleCarModel> query(Pageable pageable);
    Page<ScheduleCarModel> query(Specification<ScheduleCarModel> spec, Pageable pageable);
    List<ScheduleCarModel> getAll();
    List<ScheduleCarModel> getAllByProjectId(Long projectId);
    List<ScheduleCarModel> getAllByProjectIdAndGroupCodeAndIsVaild(Long projectId, String groupCode, boolean valid);
    void deleteByProjectIdAndCarCode(Long projectId, String carCode);
    void deleteByProjectIdAndCarCodeListAndProgrammeId(Long projectId, List<String> carCodeList, Long programmeId);
    void deleteByGroupCode(String groupCode);
    void batchSave(List<ScheduleCarModel> saveList);
    void deleteByGroupCodes(List<String> groupCodes);
    ScheduleCarModel getAllByProjectIdAndGroupCodes(Long projectId, List<String> groupCodeList, String carCode);
}
