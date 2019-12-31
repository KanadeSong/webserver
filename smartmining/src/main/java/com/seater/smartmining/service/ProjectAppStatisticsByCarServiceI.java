package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectAppStatisticsByCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/9 0009 12:03
 */
public interface ProjectAppStatisticsByCarServiceI {

    ProjectAppStatisticsByCar get(Long id) throws IOException;
    ProjectAppStatisticsByCar save(ProjectAppStatisticsByCar log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectAppStatisticsByCar> query();
    Page<ProjectAppStatisticsByCar> query(Specification<ProjectAppStatisticsByCar> spec);
    Page<ProjectAppStatisticsByCar> query(Pageable pageable);
    Page<ProjectAppStatisticsByCar> query(Specification<ProjectAppStatisticsByCar> spec, Pageable pageable);
    List<ProjectAppStatisticsByCar> getAll();
    void deleteByCreateDate(Date createDate, Long projectId);
    ProjectAppStatisticsByCar getAllByProjectIdAndCarCodeAndShiftAndDate(Long projectId, String carCode, Integer value, Date date);
    List<ProjectAppStatisticsByCar> getAllByProjectIdAndShiftAndCreateDate(Long projectId, Integer value, Date date);
    void batchSave(List<ProjectAppStatisticsByCar> saveList);
}
