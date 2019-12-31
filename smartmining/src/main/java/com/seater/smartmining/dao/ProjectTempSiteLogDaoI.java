package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectTempSiteLog;
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
 * @Date 2019/9/21 0021 11:31
 */
public interface ProjectTempSiteLogDaoI {

    ProjectTempSiteLog get(Long id) throws IOException;
    ProjectTempSiteLog save(ProjectTempSiteLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectTempSiteLog> query();
    Page<ProjectTempSiteLog> query(Specification<ProjectTempSiteLog> spec);
    Page<ProjectTempSiteLog> query(Pageable pageable);
    Page<ProjectTempSiteLog> query(Specification<ProjectTempSiteLog> spec, Pageable pageable);
    List<ProjectTempSiteLog> queryAll(Specification<ProjectTempSiteLog> specification);
    List<ProjectTempSiteLog> getAll();
    Date getMaxUnloadDateByCarCode(String carCode);
}
