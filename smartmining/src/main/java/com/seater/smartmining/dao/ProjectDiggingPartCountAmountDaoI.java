package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.entity.ProjectDiggingPartCountAmount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/16 0016 11:44
 */
public interface ProjectDiggingPartCountAmountDaoI {

    ProjectDiggingPartCountAmount get(Long id) throws IOException;
    ProjectDiggingPartCountAmount save(ProjectDiggingPartCountAmount log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectDiggingPartCountAmount> query();
    Page<ProjectDiggingPartCountAmount> query(Specification<ProjectDiggingPartCountAmount> spec);
    Page<ProjectDiggingPartCountAmount> query(Pageable pageable);
    Page<ProjectDiggingPartCountAmount> query(Specification<ProjectDiggingPartCountAmount> spec, Pageable pageable);
    List<ProjectDiggingPartCountAmount> getAll();
    ProjectDiggingPartCountAmount getAllByProjectIdAndCountId(Long projectId, Long countId);
}
