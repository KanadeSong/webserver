package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectAppStatisticsByMachine;
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
 * @Date 2019/6/9 0009 12:59
 */
public interface ProjectAppStatisticsByMachineServiceI {

    ProjectAppStatisticsByMachine get(Long id) throws IOException;
    ProjectAppStatisticsByMachine save(ProjectAppStatisticsByMachine log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectAppStatisticsByMachine> query();
    Page<ProjectAppStatisticsByMachine> query(Specification<ProjectAppStatisticsByMachine> spec);
    Page<ProjectAppStatisticsByMachine> query(Pageable pageable);
    Page<ProjectAppStatisticsByMachine> query(Specification<ProjectAppStatisticsByMachine> spec, Pageable pageable);
    List<ProjectAppStatisticsByMachine> getAll();
    void deleteByCreateDate(Date createDate, Long projectId);
    ProjectAppStatisticsByMachine getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date, String machineCode);
    List<ProjectAppStatisticsByMachine> getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date);
    void batchSave(List<ProjectAppStatisticsByMachine> saveList);
}
