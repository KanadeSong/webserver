package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectPositionPainting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 10:40
 */
public interface ProjectPositionPaintingServiceI {

    ProjectPositionPainting get(Long id) throws IOException;
    ProjectPositionPainting save(ProjectPositionPainting log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectPositionPainting> query();
    Page<ProjectPositionPainting> query(Specification<ProjectPositionPainting> spec);
    Page<ProjectPositionPainting> query(Pageable pageable);
    Page<ProjectPositionPainting> query(Specification<ProjectPositionPainting> spec, Pageable pageable);
    List<ProjectPositionPainting> getAll();
}
