package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/6 0006 14:46
 */
public interface ProjectPlaceServiceI {

    ProjectPlace get(Long id) throws IOException;
    ProjectPlace save(ProjectPlace log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectPlace> query();
    Page<ProjectPlace> query(Specification<ProjectPlace> spec);
    Page<ProjectPlace> query(Pageable pageable);
    Page<ProjectPlace> query(Specification<ProjectPlace> spec, Pageable pageable);
    List<ProjectPlace> getAll();
}
