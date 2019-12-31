package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ScheduleCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:46
 */
public interface ScheduleCarDaoI {

    ScheduleCar get(Long id) throws IOException;
    ScheduleCar save(ScheduleCar log) throws JsonProcessingException;
    void delete(Long id);
    Page<ScheduleCar> query();
    Page<ScheduleCar> query(Specification<ScheduleCar> spec);
    Page<ScheduleCar> query(Pageable pageable);
    Page<ScheduleCar> query(Specification<ScheduleCar> spec, Pageable pageable);
    List<ScheduleCar> getAll();
    List<ScheduleCar> getAllByProjectId(Long projectId);
    List<ScheduleCar> getAllByProjectIdAndCarIdAndIsVaild(Long projectId, Long carId, boolean flag);
    List<ScheduleCar> getAllByProjectIdAndGroupCode(Long projectId, String groupCode);
    void deleteByProjectIdAndCarCode(Long projectId, String carCode);
    void deleteByProjectIdAndGroupCode(Long projectId, String groupCode);
    List<ScheduleCar> getAllByQuery(Specification<ScheduleCar> spec);
    void deleteByGroupCode(String groupCode);
    List<String> getGroupCodeList();
    ScheduleCar getAllByProjectIdAndCarCode(Long projectId, String carCode);
    void batchSave(List<ScheduleCar> scheduleCarList);
    List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid);
    void deleteByProjectId(Long projectId);
    List<ScheduleCar> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId);
    void  deleteByProjectIdAndCarCodeList(Long projectId, List<String> carCodeList);
    List<ScheduleCar> getAllByProjectIdAndIsVaildObject(Long projectId, Boolean isValid);
}
