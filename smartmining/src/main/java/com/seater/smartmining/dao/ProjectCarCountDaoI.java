package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectCarCount;
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
 * @Date 2019/8/15 0015 14:08
 */
public interface ProjectCarCountDaoI {

    ProjectCarCount get(Long id) throws IOException;
    ProjectCarCount save(ProjectCarCount log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarCount> query();
    Page<ProjectCarCount> query(Specification<ProjectCarCount> spec);
    Page<ProjectCarCount> query(Pageable pageable);
    Page<ProjectCarCount> query(Specification<ProjectCarCount> spec, Pageable pageable);
    List<ProjectCarCount> getAll();
    ProjectCarCount getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(Long projectId, String carCode, Date date, Integer shifts, Integer carType);
    List<ProjectCarCount> getAllByProjectIdAndDateIdentificationAndShiftsAndCarType(Long projectId, Date date, Integer shift, Integer carType);
}
