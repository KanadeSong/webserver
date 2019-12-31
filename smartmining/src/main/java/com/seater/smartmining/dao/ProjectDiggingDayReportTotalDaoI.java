package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingDayReport;
import com.seater.smartmining.entity.ProjectDiggingDayReportTotal;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/15 0015 15:18
 */
public interface ProjectDiggingDayReportTotalDaoI {

    ProjectDiggingDayReportTotal get(Long id) throws IOException;
    ProjectDiggingDayReportTotal save(ProjectDiggingDayReportTotal log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingDayReportTotal> query();
    List<ProjectDiggingDayReportTotal> getAll();
    List<ProjectDiggingDayReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<ProjectDiggingDayReportTotal> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

}
