package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectMachineLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 21:11
 */
public interface ProjectMachineLocationServiceI {

    ProjectMachineLocation get(Long id) throws IOException;
    ProjectMachineLocation save(ProjectMachineLocation log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectMachineLocation> query();
    Page<ProjectMachineLocation> query(Specification<ProjectMachineLocation> spec);
    Page<ProjectMachineLocation> query(Pageable pageable);
    Page<ProjectMachineLocation> query(Specification<ProjectMachineLocation> spec, Pageable pageable);
    List<ProjectMachineLocation> getAll();
    void batchSave(List<ProjectMachineLocation> saveList);
    List<ProjectMachineLocation> getAllByProjectIdAndCarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime);
}
