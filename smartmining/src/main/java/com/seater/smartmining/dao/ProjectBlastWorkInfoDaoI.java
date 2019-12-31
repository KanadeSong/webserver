package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectBlastWorkInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/12 0012 12:54
 */
public interface ProjectBlastWorkInfoDaoI {

    ProjectBlastWorkInfo get(Long id) throws IOException;
    ProjectBlastWorkInfo save(ProjectBlastWorkInfo log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectBlastWorkInfo> query();
    Page<ProjectBlastWorkInfo> query(Specification<ProjectBlastWorkInfo> spec);
    Page<ProjectBlastWorkInfo> query(Pageable pageable, Specification<ProjectBlastWorkInfo> spec);
    Page<ProjectBlastWorkInfo> query(Pageable pageable);
    List<ProjectBlastWorkInfo> getAll();
}
