package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectProgramme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/14 0014 17:57
 */
public interface ProjectProgrammeDaoI {

    ProjectProgramme get(Long id) throws IOException;
    ProjectProgramme save(ProjectProgramme log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectProgramme> query();
    Page<ProjectProgramme> query(Specification<ProjectProgramme> spec);
    Page<ProjectProgramme> query(Pageable pageable);
    Page<ProjectProgramme> query(Specification<ProjectProgramme> spec, Pageable pageable);
    List<ProjectProgramme> getAll();
}
