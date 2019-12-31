package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectCubicDetailElse;
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
 * @Date 2019/3/5 0005 10:43
 */
public interface ProjectCubicDetailElseDaoI {

    ProjectCubicDetailElse get(Long id) throws IOException;
    ProjectCubicDetailElse save(ProjectCubicDetailElse log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectCubicDetailElse> query();
    Page<ProjectCubicDetailElse> query(Specification<ProjectCubicDetailElse> spec);
    Page<ProjectCubicDetailElse> query(Pageable pageable);
    Page<ProjectCubicDetailElse> query(Specification<ProjectCubicDetailElse> spec, Pageable pageable);
    List<ProjectCubicDetailElse> getAll();
    void  deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId);
    List<ProjectCubicDetailElse> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate);
}
