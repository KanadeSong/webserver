package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectWorkTimeByCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/18 0018 11:28
 */
public interface ProjectWorkTimeByCarServiceI {

    ProjectWorkTimeByCar get(Long id) throws IOException;
    ProjectWorkTimeByCar save(ProjectWorkTimeByCar log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectWorkTimeByCar> query();
    Page<ProjectWorkTimeByCar> query(Specification<ProjectWorkTimeByCar> spec);
    Page<ProjectWorkTimeByCar> query(Pageable pageable);
    Page<ProjectWorkTimeByCar> query(Specification<ProjectWorkTimeByCar> spec, Pageable pageable);
    List<ProjectWorkTimeByCar> getAll();
    List<ProjectWorkTimeByCar> getAllByProjectIdAndCarCodeAndStatus(Long projectId, String carCode, Integer status);
}
