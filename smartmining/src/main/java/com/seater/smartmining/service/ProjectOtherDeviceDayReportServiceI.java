package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectOtherDeviceDayReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/19 0019 13:02
 */
public interface ProjectOtherDeviceDayReportServiceI {

    ProjectOtherDeviceDayReport get(Long id) throws IOException;
    ProjectOtherDeviceDayReport save(ProjectOtherDeviceDayReport log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectOtherDeviceDayReport> query();
    Page<ProjectOtherDeviceDayReport> query(Specification<ProjectOtherDeviceDayReport> spec);
    Page<ProjectOtherDeviceDayReport> query(Pageable pageable);
    Page<ProjectOtherDeviceDayReport> query(Specification<ProjectOtherDeviceDayReport> spec, Pageable pageable);
    List<ProjectOtherDeviceDayReport> getAll();
}
