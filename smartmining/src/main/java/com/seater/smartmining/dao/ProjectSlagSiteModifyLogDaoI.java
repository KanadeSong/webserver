package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectSlagSiteModifyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/15 18:00
 */
public interface ProjectSlagSiteModifyLogDaoI {

    ProjectSlagSiteModifyLog get(Long id) throws IOException;
    ProjectSlagSiteModifyLog save(ProjectSlagSiteModifyLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectSlagSiteModifyLog> query();
    Page<ProjectSlagSiteModifyLog> query(Specification<ProjectSlagSiteModifyLog> spec);
    Page<ProjectSlagSiteModifyLog> query(Pageable pageable);
    Page<ProjectSlagSiteModifyLog> query(Specification<ProjectSlagSiteModifyLog> spec, Pageable pageable);
    List<ProjectSlagSiteModifyLog> getAll();
}
