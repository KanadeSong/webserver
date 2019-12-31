package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectScheduled;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Deprecated
public interface ProjectScheduledDaoI {
     ProjectScheduled get(Long id) throws IOException;
     ProjectScheduled save(ProjectScheduled log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectScheduled> query();
     Page<ProjectScheduled> query(Specification<ProjectScheduled> spec);
     Page<ProjectScheduled> query(Pageable pageable);
     Page<ProjectScheduled> query(Specification<ProjectScheduled> spec, Pageable pageable);
     List<ProjectScheduled> getAll();
     List<ProjectScheduled> getByProjectIdOrderById(Long projectId);
     List<ProjectScheduled> getByProjectIdAndCarIdOrderById(Long projectId, Long carId);
     List<ProjectScheduled> getByProjectIdAndDiggingMachineIdOrderById(Long projectId, Long diggingMachineId);
     ProjectScheduled getByProjectIdAndDiggingMachineIdAndCarIdOrderById(Long projectId, Long diggingMachineId, Long carId);
     List<Map> getByProjectIdPage(Long projectId, int cur, int page);
     List<ProjectScheduled> getAllByProjectIdAndDiggingMachineId(Long projectId, Long machineId);
     void deleteByDiggingMachineIdAndProjectId(Long machineId, Long projectId);
     void deleteByCarIdAndProjectId(Long carId, Long projectId);
     List<Map> getByProjectIdAndCarIdOnDigging(Long projectId, Long carId);
     List<Map> getByProjectIdCount(Long projectId);
     ProjectScheduled getByProjectIdAndCarId(Long projectId, Long carId);
     List<Map> getByProjectIdOnGroupId(Long projectId, int current, int page);
     List<Map> getByProjectIdAndManagerIdOnGroupId(Long projectId, String managerId,int current, int page);
     List<Map> getByAllProjectIdOnGroupId(Long projectId);
     List<Map> getByProjectIdAndGroupCode(Long projectId, String groupCode);
     List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineId(Long projectId, Long machineId);
     List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(Long projectId, Long machineId, String managerId);
     List<Map> getByProjectIdAndDiggingMachineId(Long projectId, Long diggingMachineId);
     List<ProjectScheduled> getAllByProjectIdAndCarId(Long projectId, Long carId);
     void deleteByDiggingMachineCodeAndProjectId(String machineCode, Long projectId);
     List<ProjectScheduled> getGroupCodeByProjectIdAndCarId(Long projectId, Long carId);
     List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndManagerId(Long projectId, Long carId, String managerId);
     List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId);
     List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(Long projectId, Long carId, Long machineId, String managerId);
     List<ProjectScheduled> getAllByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId);
     List<ProjectScheduled> queryWx(Specification<ProjectScheduled> spec);
     void saveOrModify(ProjectScheduled projectScheduled);
     List<Map> getByProjectIdAndGroupCodeOrderByDiggingMachineId(Long projectId, String groupCode);
     List<Map> getByAllProjectIdAndPricingType(Long projectId, Integer pricingType);
}
