package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectOilCarUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/2 18:27
 */
public interface ProjectOilCarUserServiceI {
    ProjectOilCarUser get(Long id) throws IOException;

    ProjectOilCarUser save(ProjectOilCarUser entity) throws IOException;

    void delete(Long id);

    void delete(List<Long> ids);

    void deleteByOilCarId(Long id);

    Page<ProjectOilCarUser> query();

    Page<ProjectOilCarUser> query(Specification<ProjectOilCarUser> spec);

    Page<ProjectOilCarUser> query(Pageable pageable);

    Page<ProjectOilCarUser> query(Specification<ProjectOilCarUser> spec, Pageable pageable);

    List<ProjectOilCarUser> getAll();

    List<ProjectOilCarUser> queryWx(Specification<ProjectOilCarUser> spec);
}
