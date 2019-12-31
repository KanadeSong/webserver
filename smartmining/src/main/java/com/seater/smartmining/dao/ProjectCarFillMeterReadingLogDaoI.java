package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectCarFillMeterReadingLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/4 20:34
 */
public interface ProjectCarFillMeterReadingLogDaoI {
    ProjectCarFillMeterReadingLog get(Long id) throws IOException;
    ProjectCarFillMeterReadingLog save(ProjectCarFillMeterReadingLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarFillMeterReadingLog> query();
    Page<ProjectCarFillMeterReadingLog> query(Specification<ProjectCarFillMeterReadingLog> spec);
    Page<ProjectCarFillMeterReadingLog> query(Pageable pageable);
    Page<ProjectCarFillMeterReadingLog> query(Specification<ProjectCarFillMeterReadingLog> spec, Pageable pageable);
    List<ProjectCarFillMeterReadingLog> getAll();
    public ProjectCarFillMeterReadingLog getByProjectIdAndOilCarIdAndAddTime(Long projectId,Long oilCarId, Date addTime);
    List<ProjectCarFillMeterReadingLog> queryWx(Specification<ProjectCarFillMeterReadingLog> spec);
    ProjectCarFillMeterReadingLog querySingle(Specification<ProjectCarFillMeterReadingLog> spec);
    Long getHistoryByOilCarId(Long oilCarId);
}
