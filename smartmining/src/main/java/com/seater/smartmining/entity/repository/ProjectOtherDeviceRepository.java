package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectOtherDeviceRepository  extends JpaRepository<ProjectOtherDevice, Long>, JpaSpecificationExecutor<ProjectOtherDevice> {
    List<ProjectOtherDevice> getByProjectIdOrderById(Long projectId);

    List<ProjectOtherDevice> getByProjectIdAndCarTypeIs(Long projectId, CarType carType);

    ProjectOtherDevice getAllByProjectIdAndCodeAndCarType(Long projectId, String code, CarType carType);

    ProjectOtherDevice getAllByUid(String uid);

    List<ProjectOtherDevice> getAllByProjectId(Long projectId);
}
