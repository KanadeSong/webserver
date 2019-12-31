package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectAperture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 17:58
 */
public interface ProjectApertureServiceI {

    ProjectAperture get(Long id) throws IOException;
    ProjectAperture save(ProjectAperture log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectAperture> query();
    Page<ProjectAperture> query(Specification<ProjectAperture> spec);
    Page<ProjectAperture> query(Pageable pageable);
    Page<ProjectAperture> query(Specification<ProjectAperture> spec, Pageable pageable);
    List<ProjectAperture> getAll();
}
