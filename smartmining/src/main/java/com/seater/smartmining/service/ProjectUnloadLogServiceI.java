package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectCheckLog;
import com.seater.smartmining.entity.ProjectUnloadLog;
import com.seater.user.entity.SysUser;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectUnloadLogServiceI {
     ProjectUnloadLog get(Long id) throws IOException;
     ProjectUnloadLog save(ProjectUnloadLog log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectUnloadLog> query();
     Page<ProjectUnloadLog> query(Specification<ProjectUnloadLog> spec);
     Page<ProjectUnloadLog> query(Pageable pageable);
     Page<ProjectUnloadLog> query(Specification<ProjectUnloadLog> spec, Pageable pageable);
     List<ProjectUnloadLog> queryParams(Specification<ProjectUnloadLog> spec);
     List<ProjectUnloadLog> getAll();
     Date getMaxUnloadDateByCarCode(String carCode, Date date);
     List<ProjectUnloadLog> getAllByRecviceDate(Date receiveDate);
     List<ProjectUnloadLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startDate, Date endDate, String uid);
     List<ProjectUnloadLog> queryAll(Specification<ProjectUnloadLog> specification);
     List<ProjectUnloadLog> getAllByProjectIDAndTime(Long projectId, Date startTime, Date endTime);
     List<Map> getCarCodeByProjectIDAndTime(Long projectId, Date startTime, Date endTime);
     List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime, Date uploadTime);
     List<Map> getUnValidByProjectIDAndTime(Long projectId, Date startTime, Date endTime);
     List<Map> getUploadCountByCheck(Long projectId, Date startTime, Date endTime, Date checkTime);
     List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids);
     List<Map> getReportInfoGroup(Long projectId, Date startTime, Date endTime);
     List<Map> getTotalReportInfoByCarCode(Long projectId, Date startTime, Date endTime);
     List<Map> getTotalReportInfoByCarCodeAndSlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids);
     ProjectUnloadLog getAllByProjectIDAndTimeDischargeAndCarCode(Long projectId, Date timeDischarge, String carCode);
     List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaild();
     Map getTotalCountByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime);
     List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaildAndDetail(Long projectId, Date startTime, Date endTime, Boolean valid, Boolean detail);
}
