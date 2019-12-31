package com.seater.smartmining.service;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectOtherDeviceServiceI {
     ProjectOtherDevice get(Long id) throws IOException;
     ProjectOtherDevice save(ProjectOtherDevice log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectOtherDevice> query();
     Page<ProjectOtherDevice> query(Specification<ProjectOtherDevice> spec);
     Page<ProjectOtherDevice> query(Pageable pageable);
     Page<ProjectOtherDevice> query(Specification<ProjectOtherDevice> spec, Pageable pageable);
     List<ProjectOtherDevice> getAll();
     List<ProjectOtherDevice> queryWx(Specification<ProjectOtherDevice> spec);
     ProjectOtherDevice getAllByUid(String uid);
     List<ProjectOtherDevice> getByProjectIdAndCarTypeIs(Long projectId, CarType carType);
     void saveAll(List<ProjectOtherDevice> projectOtherDeviceList);
     ProjectOtherDevice getAllByProjectIdAndCodeAndCarType(Long projectId, String code, CarType carType);
     List<ProjectOtherDevice> getAllByProjectId(Long projectId);
}
