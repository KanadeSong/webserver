package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.enums.ProjectDeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectDeviceDaoI {
    ProjectDevice get(Long id) throws IOException;
    ProjectDevice save(ProjectDevice log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDevice> query();
    Page<ProjectDevice> query(Specification<ProjectDevice> spec);
    Page<ProjectDevice> query(Pageable pageable);
    Page<ProjectDevice> query(Specification<ProjectDevice> spec, Pageable pageable);
    List<ProjectDevice> getAll();
    ProjectDevice getByProjectIdAndUid(Long projectId, String uid);
    ProjectDevice getByUid(String uid);
    ProjectDevice getAllByProjectIdAndCodeAndDeviceType(Long projectId, String code, Integer type);
    List<ProjectDevice> getAllByAndFileName(String fileName);
    List<ProjectDevice> getAllByCodeAndDeviceType(String code, Integer deviceType);
    ProjectDevice getAllByDeviceCode(String deviceCode);
    List<ProjectDevice> getAllByProjectId(Long projectId);
    void batchSave(List<ProjectDevice> projectDeviceList);
    List<ProjectDevice> getAllByProjectIdAndDeviceType(Long projectId, Integer deviceType);
    List<ProjectDevice> getAllCarDeviceAndMachineDevice(Long projectId);
}
