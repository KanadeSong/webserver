package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingMachineDaoI;
import com.seater.smartmining.dao.ProjectWorkTimeByDiggingDaoI;
import com.seater.smartmining.entity.ProjectWorkTimeByDigging;
import com.seater.smartmining.enums.WorkInfoStatusEnums;
import com.seater.smartmining.enums.WorkStatusEnums;
import com.seater.smartmining.service.ProjectWorkTimeByDiggingServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/26 0026 13:58
 */
@Service
public class ProjectWorkTimeByDiggingServiceImpl implements ProjectWorkTimeByDiggingServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectWorkTimeByDiggingDaoI projectWorkTimeByDiggingDaoI;

    @Override
    public ProjectWorkTimeByDigging get(Long id) throws IOException {
        return projectWorkTimeByDiggingDaoI.get(id);
    }

    @Override
    public ProjectWorkTimeByDigging save(ProjectWorkTimeByDigging log) throws IOException {
        return projectWorkTimeByDiggingDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectWorkTimeByDiggingDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectWorkTimeByDiggingDaoI.delete(ids);
    }

    @Override
    public Page<ProjectWorkTimeByDigging> query() {
        return projectWorkTimeByDiggingDaoI.query();
    }

    @Override
    public Page<ProjectWorkTimeByDigging> query(Specification<ProjectWorkTimeByDigging> spec) {
        return projectWorkTimeByDiggingDaoI.query(spec);
    }

    @Override
    public List<ProjectWorkTimeByDigging> queryAllByParams(Specification<ProjectWorkTimeByDigging> spec) {
        return projectWorkTimeByDiggingDaoI.queryAllByParams(spec);
    }

    @Override
    public Page<ProjectWorkTimeByDigging> query(Specification<ProjectWorkTimeByDigging> spec, Pageable pageable) {
        return projectWorkTimeByDiggingDaoI.query(spec,pageable);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdOrderById(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdAndMaterialIdOrderById(projectId, materialId);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdAndMaterialIdAndTime(projectId,materialId,startTime,endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public void setEndTimeById(Long id, Date endTime) {
        projectWorkTimeByDiggingDaoI.setEndTimeById(id, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdByQuery(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdAndMaterialIdByQuery(projectId, materialId);
    }

    @Override
    public List<Map> getWorkTimeByMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getWorkTimeByMaterialIdAndTime(projectId, materialId, startTime, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date createTime) {
        return projectWorkTimeByDiggingDaoI.getTimeByMaterialIdAndCreateTime(projectId, materialId, createTime);
    }

    @Override
    public void setWorkInfoStatusEnumsById(Long id, WorkInfoStatusEnums workInfoStatusEnums) {
        projectWorkTimeByDiggingDaoI.setWorkInfoStatusEnumsById(id, workInfoStatusEnums);
    }

    @Override
    public void setWorkInfoStatusEnumsAnsStatusById(Long id, WorkInfoStatusEnums workInfoStatusEnums, WorkStatusEnums workStatus) {
        projectWorkTimeByDiggingDaoI.setWorkInfoStatusEnumsAnsStatusById(id, workInfoStatusEnums, workStatus);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdByQuery(Long projectId, Date reportDate) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdByQuery(projectId, reportDate);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdByQueryAndShift(Long projectId, Date reportDate, Integer shift) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdByQueryAndShift(projectId, reportDate, shift);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getTimeByMaterialIdAndCreateTime(projectId, materialId, startTime, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getTimeByMachineIdAndCreateTime(Long projectId, Long machineId, Date createTime) {
        return projectWorkTimeByDiggingDaoI.getTimeByMachineIdAndCreateTime(projectId, machineId, createTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndStartTimeAndShift(Long projectId, Date startTime, Integer shift) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectIdAndStartTimeAndShift(projectId, startTime, shift);
    }

    @Override
    public List<Map> getWorkTimeByProjectIdAndStartTime(Long projectId, Date reportDate) {
        return projectWorkTimeByDiggingDaoI.getWorkTimeByProjectIdAndStartTime(projectId, reportDate);
    }

    @Override
    public Map getAllByProjectIdAndStartTimeAndShiftAndPricingTypeEnums(Long projectId, Date startTime, Date endTime,Integer shift, Integer pricingType, Long machineId) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectIdAndStartTimeAndShiftAndPricingTypeEnums(projectId, startTime, endTime,shift, pricingType, machineId);
    }

    @Override
    public List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getMaterialIdByProjectIdAndStartTime(projectId, startTime, endTime);
    }

    @Override
    public Map getTotalWorkTimeByProjectIdAndDateTime(Long projectId, Date dateTime, Integer shift) {
        return projectWorkTimeByDiggingDaoI.getTotalWorkTimeByProjectIdAndDateTime(projectId, dateTime, shift);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getAllByProjectIdAndStartTimeAndShiftAndWorkStatus(Long projectId, Date dateTime, Integer shift, Integer workStatus) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectIdAndStartTimeAndShiftAndWorkStatus(projectId, dateTime, shift, workStatus);
    }

    @Override
    public List<Map> getMaterialIdByProjectIdAndStartTimeAndPage(Long projectId, Date dateTime, int cur, int pageSize) {
        return projectWorkTimeByDiggingDaoI.getMaterialIdByProjectIdAndStartTimeAndPage(projectId, dateTime, cur, pageSize);
    }

    @Override
    public List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date dateTime) {
        return projectWorkTimeByDiggingDaoI.getMaterialIdByProjectIdAndStartTime(projectId, dateTime);
    }

    @Override
    public List<Map> getTimeByMaterialIdAndStartTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getTimeByMaterialIdAndStartTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getInfoByMaterialIdAndMaterialIdAndStartTime(Long projectId, Date dateTime) {
        return projectWorkTimeByDiggingDaoI.getInfoByMaterialIdAndMaterialIdAndStartTime(projectId, dateTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getAllByProjectId(Long projectId) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getAllByProjectIdAndMaterialCode(Long projectId, String machineCode) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectIdAndMaterialCode(projectId, machineCode);
    }

    @Override
    public List<Map> getTotalTimeByProjectIdAndDate(Long projectId, Date date) {
        return projectWorkTimeByDiggingDaoI.getTotalTimeByProjectIdAndDate(projectId, date);
    }

    @Override
    public List<Map> getTotalTimeAndPricingTypeByProjectIdAndDate(Long projectId, Date date) {
        return projectWorkTimeByDiggingDaoI.getTotalTimeAndPricingTypeByProjectIdAndDate(projectId, date);
    }

    @Override
    public List<Map> getAllByProjectIdAndStartTimeAndEndTimeAndMaterialId(Long projectId, Long machineId, Date startDate, Date endDate) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectIdAndStartTimeAndEndTimeAndMaterialId(projectId, machineId, startDate, endDate);
    }

    @Override
    public List<Map> getDiggingTimeReport(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getDiggingTimeReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getDiggingTimeReportByMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeReportByHistory(Long projectId, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getDiggingTimeReportByHistory(projectId, endTime);
    }

    @Override
    public List<Map> getDiggingTimeInfo(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getDiggingTimeInfo(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeInfoMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getDiggingTimeInfoMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeInfoHistory(Long projectId, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getDiggingTimeInfoHistory(projectId, endTime);
    }

    @Override
    public List<Map> getDiggingInfoByProjectId(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getDiggingInfoByProjectId(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalDiggingTimeReport(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getTotalDiggingTimeReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getTotalDiggingTimeReportByMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalDiggingTimeReportByHistory(Long projectId, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getTotalDiggingTimeReportByHistory(projectId, endTime);
    }

    @Override
    public List<Map> getWorkTimeByPricingType(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectWorkTimeByDiggingDaoI.getWorkTimeByPricingType(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getWorkTimeByPricingTypeMonth(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectWorkTimeByDiggingDaoI.getWorkTimeByPricingTypeMonth(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getWorkTimeByPricingTypeHistory(Long projectId, Date endTime, Integer pricingType) {
        return projectWorkTimeByDiggingDaoI.getWorkTimeByPricingTypeHistory(projectId, endTime, pricingType);
    }

    @Override
    public List<Map> reportDiggingWorkTimeByPlace(Long projectId, Date date) {
        return projectWorkTimeByDiggingDaoI.reportDiggingWorkTimeByPlace(projectId, date);
    }

    @Override
    public List<Map> reportDiggingWorkTimeByMaterial(Long projectId, Date date) {
        return projectWorkTimeByDiggingDaoI.reportDiggingWorkTimeByMaterial(projectId, date);
    }

    @Override
    public List<Map> getAttendanceByTime(Long projectId, Date date) {
        return projectWorkTimeByDiggingDaoI.getAttendanceByTime(projectId, date);
    }

    @Override
    public List<Map> getAttendanceByTimeMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingDaoI.getAttendanceByTimeMonth(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialId(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdAndMaterialId(projectId, materialId);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAdd(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingDaoI.getByProjectIdAndMaterialIdAdd(projectId, materialId);
    }

    @Override
    public Long getAllByProjectIdAndMaterialCodeAndDateIdentificationAndShift(Long projectId, String machineCode, Date date, Integer shift) {
        return projectWorkTimeByDiggingDaoI.getAllByProjectIdAndMaterialCodeAndDateIdentificationAndShift(projectId, machineCode, date, shift);
    }
}
