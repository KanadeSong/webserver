package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.enums.ProjectDeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectDeviceRepository extends JpaRepository<ProjectDevice, Long>, JpaSpecificationExecutor<ProjectDevice> {

    ProjectDevice getByProjectIdAndUid(Long projectId, String uid);

    ProjectDevice getByUid(String uid);

    @Query(nativeQuery = true, value = "select * from project_device where project_id = ?1 and code = ?2 and device_type = ?3")
    ProjectDevice getAllByProjectIdAndCodeAndDeviceType(Long projectId, String code, Integer type);

    @Query(nativeQuery = true, value = "select * from project_device where code = ?1 and device_type = ?2")
    List<ProjectDevice> getAllByCodeAndDeviceType(String code, Integer deviceType);

    @Query(nativeQuery = true, value = "select * from project_device where project_id = ?1 and device_type = ?2")
    List<ProjectDevice> getAllByProjectIdAndDeviceType(Long projectId, Integer deviceType);

    List<ProjectDevice> getAllByAndFileName(String fileName);

    ProjectDevice getAllByDeviceCode(String deviceCode);

    List<ProjectDevice> getAllByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "select * from project_device where project_id = ?1 and device_type in (2, 5)")
    List<ProjectDevice> getAllCarDeviceAndMachineDevice(Long projectId);

    List<ProjectDevice> findByProjectIdAndCodeAndDeviceType(Long projectId, String code, ProjectDeviceType deviceType);
}
