package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarWorkInfoDaoI;
import com.seater.smartmining.entity.ProjectCarWorkInfo;
import com.seater.smartmining.entity.ProjectScheduleDetail;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.ProjectCarStatus;
import com.seater.smartmining.service.ProjectCarWorkInfoServiceI;
import com.seater.smartmining.service.ProjectScheduleDetailServiceI;
import com.seater.smartmining.utils.schedule.AutoScheduleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProjectCarWorkInfoServiceImpl implements ProjectCarWorkInfoServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarWorkInfoDaoI projectCarWorkInfoDaoI;
    @Autowired
    ProjectScheduleDetailServiceI projectScheduleDetailServiceI;

    @Override
    public ProjectCarWorkInfo get(Long id) throws IOException {
        return projectCarWorkInfoDaoI.get(id);
    }

    @Override
    public ProjectCarWorkInfo save(ProjectCarWorkInfo log) throws IOException{
        ProjectCarWorkInfo r = projectCarWorkInfoDaoI.save(log);
        //projectScheduleDetailServiceI.initByWorkInfo(r);
        return r;
    }

    @Override
    public void delete(Long id) {
        projectCarWorkInfoDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarWorkInfoDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarWorkInfo> query(Pageable pageable) {
        return projectCarWorkInfoDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarWorkInfo> query() {
        return projectCarWorkInfoDaoI.query();
    }

    @Override
    public Page<ProjectCarWorkInfo> query(Specification<ProjectCarWorkInfo> spec) {
        return projectCarWorkInfoDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarWorkInfo> query(Specification<ProjectCarWorkInfo> spec, Pageable pageable) {
        return projectCarWorkInfoDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarWorkInfo> getAll() {
        return projectCarWorkInfoDaoI.getAll();
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeCheck(Long projectId, Long carId, Date timeChaeck) {
        return projectCarWorkInfoDaoI.getByProjectIdAndCarIdAndTimeCheck(projectId, carId, timeChaeck);
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndDiggingMachineIdAndTimeCheck(Long projectId, Long diggingMachineId, Date timeCheck) {
        return projectCarWorkInfoDaoI.getByProjectIdAndDiggingMachineIdAndTimeCheck(projectId, diggingMachineId, timeCheck);
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeDischarge(Long projectId, Long carId, Date timeDischarge) {
        return projectCarWorkInfoDaoI.getByProjectIdAndCarIdAndTimeCheck(projectId, carId, timeDischarge);
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeLoad(Long projectId, Long carId, Date timeLoad) {
        return projectCarWorkInfoDaoI.getByProjectIdAndCarIdAndTimeLoad(projectId, carId, timeLoad);
    }

    /*@Override
    public List<Map> getDistanceListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getDistanceListByProjectIdAndTime(projectId, startTime, endTime);
    }*/

    @Override
    public List<Map> getDistanceListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getDistanceListByProjectIdAndTime(projectId, startTime, endTime);
    }

    /*@Override
    public List<Map> getCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCountListByProjectIdAndTime(projectId, startTime, endTime);
    }*/

    @Override
    public List<Map> getCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCountListByProjectIdAndTime(projectId, startTime, endTime);
    }

    /*@Override
    public List<Map> getCarGrandTotalListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarGrandTotalListByProjectIdAndTime(projectId, startTime, endTime);
    }*/

    @Override
    public List<Map> getCarGrandTotalListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarGrandTotalListByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingDayCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoDaoI.getDiggingDayCountListByProjectIdAndTime(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getDiggingDayCountListByProjectIdAndTimeGroupByShift(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getDiggingDayCountListByProjectIdAndTimeGroupByShift(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getMaterialDetailByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getMaterialDetailByProjectIdAndMachineIdAndTime(projectId,machineId,startTime,endTime);
    }

    @Override
    public List<Map> getMaterialDetailByProjectIdAndMachineIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getMaterialDetailByProjectIdAndMachineIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getProjectCarWorkInfoByCreateDate(Date reportDate) {
        return projectCarWorkInfoDaoI.getProjectCarWorkInfoByCreateDate(reportDate);
    }

    @Override
    public List<Map> getSettlementDetailByProjectIdAndTimeAndCarId(Long projectId, Date startTime, Date endTime, Long carId) {
        return projectCarWorkInfoDaoI.getSettlementDetailByProjectIdAndTimeAndCarId(projectId, startTime, endTime, carId);
    }

    @Override
    public List<Map> getCubicDetailByProjectIdAndDiggingMachineIdAndTime(Long projectId, Long machineId, List<String> dateList, Integer pricingType) {
        return projectCarWorkInfoDaoI.getCubicDetailByProjectIdAndDiggingMachineIdAndTime(projectId, machineId, dateList, pricingType);
    }

    @Override
    public List<Map> getElseTotalByProjectIdAndDiggingMachineIdAndTime(Long projectId, Long machineId, List<String> dateList, Integer pricingType) {
        return projectCarWorkInfoDaoI.getElseTotalByProjectIdAndDiggingMachineIdAndTime(projectId, machineId, dateList, pricingType);
    }

    @Override
    public List<Map> getDetailTotalByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoDaoI.getDetailTotalByProjectIdAndMachineIdAndTime(projectId, machineId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getByCubicDetailOrderByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getByCubicDetailOrderByProjectIdAndCarIdAndTime(projectId, carId, startTime, endTime);
    }

    @Override
    public List<Map> getMachineIdByProjectIdAndPageAndTime(Long projectId, Integer current, Integer pageSize, Date time) {
        Integer currentPage = (current - 1) * pageSize;
        return projectCarWorkInfoDaoI.getMachineIdByProjectIdAndPageAndTime(projectId, currentPage, pageSize, time);
    }

    @Override
    public List<Map> getAllSettlementByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getAllSettlementByProjectIdAndCarIdAndTime(projectId, carId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date beginDate, Date endDate) {
        return projectCarWorkInfoDaoI.getCarsCountByProjectIdAndCarIdAndTime(projectId, carId, beginDate, endDate);
    }

    @Override
    public List<Map> getSumCubicByTime(Long projectId, Long machineId, Date date) {
        return projectCarWorkInfoDaoI.getSumCubicByTime(projectId, machineId, date);
    }

    @Override
    public List<Map> getDateIdentificationByMachineId(Long projectId, Long machineId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getDateIdentificationByMachineId(projectId, machineId, startTime, endTime);
    }

    @Override
    public List<Map> getMachineIdListByDate(Long projectId, Date time) {
        return projectCarWorkInfoDaoI.getMachineIdListByDate(projectId, time);
    }

    @Override
    public List<Map> getAppDiggingInfoByProjectIdAndDate(Long projectId, Date date) {
        return projectCarWorkInfoDaoI.getAppDiggingInfoByProjectIdAndDate(projectId, date);
    }

    @Override
    public Map getCarsCountByProjectIdAndDateIdentificationAndPass(Long projectId, Date date, Integer pass, Integer shift) {
        return projectCarWorkInfoDaoI.getCarsCountByProjectIdAndDateIdentificationAndPass(projectId, date, pass, shift);
    }

    @Override
    public List<Map> getCarsCountByProjectIdAndDateIdentification(Long projectId, Date date, Integer shift) {
        return projectCarWorkInfoDaoI.getCarsCountByProjectIdAndDateIdentification(projectId, date, shift);
    }

    @Override
    public List<Map> getDiggingDayCountListByProjectIdAndTimeAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId) {
        return projectCarWorkInfoDaoI.getDiggingDayCountListByProjectIdAndTimeAndMachineId(projectId, startTime, endTime, machineId);
    }

    @Override
    public Map getHistoryInfoByTime(Long projectId, Date startTime) {
        return projectCarWorkInfoDaoI.getHistoryInfoByTime(projectId, startTime);
    }

    @Override
    public List<Map> getUnpassInfoByProjectIdAndDate(Long projectId, Date date) {
        return projectCarWorkInfoDaoI.getUnpassInfoByProjectIdAndDate(projectId, date);
    }

    @Override
    public List<ProjectCarWorkInfo> getCarsCountUnPassByProjectIdAndDate(Long projectId, Date date, Integer pass, Integer shift) {
        return projectCarWorkInfoDaoI.getCarsCountUnPassByProjectIdAndDate(projectId, date, pass, shift);
    }

    @Override
    public List<Map> countByProjectIdAndDateIdentification(Long projectId, Date date) {
        return projectCarWorkInfoDaoI.countByProjectIdAndDateIdentification(projectId, date);
    }

    @Override
    public Integer countByProjectIdAndDateIdentificationAndMaterialId(Long projectId, Date date, Long materialId) {
        return projectCarWorkInfoDaoI.countByProjectIdAndDateIdentificationAndMaterialId(projectId, date, materialId);
    }

    @Override
    public List<Map> getTotalCubicAndCountByProjectIdAndDateIdentification(Long projectId, Date date) {
        return projectCarWorkInfoDaoI.getTotalCubicAndCountByProjectIdAndDateIdentification(projectId, date);
    }

    @Override
    public void savAll(List<ProjectCarWorkInfo> projectCarWorkInfos) {
        projectCarWorkInfoDaoI.savAll(projectCarWorkInfos);
    }

    @Override
    public List<ProjectCarWorkInfo> queryAllByParams(Specification<ProjectCarWorkInfo> specification) {
        return projectCarWorkInfoDaoI.queryAllByParams(specification);
    }

    @Override
    public List<Map> getCountByProjectIdAndShiftAndDate(Long projectId, Integer shift, Date date) {
        return projectCarWorkInfoDaoI.getCountByProjectIdAndShiftAndDate(projectId, shift, date);
    }

    @Override
    public List<Map> getByProjectId(Long projectId) {
        return projectCarWorkInfoDaoI.getByProjectId(projectId);
    }

    @Override
    public List<Map> getDiggingWorkReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getDiggingWorkReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingWorkReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getDiggingWorkReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingWorkReportHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoDaoI.getDiggingWorkReportHistory(projectId, endTime);
    }

    @Override
    public List<Map> getQualificationReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getQualificationReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getQualificationReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationReportHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoDaoI.getDiggingWorkReportHistory(projectId, endTime);
    }

    @Override
    public List<Map> findByProjectIdAndStatus(Long projectId, Date startDate, Date endDate, Integer status) {
        return projectCarWorkInfoDaoI.findByProjectIdAndStatus(projectId, startDate, endDate, status);
    }

    @Override
    public List<Map> getCarAttendanceReport(Long projectId, Date date) {
        return projectCarWorkInfoDaoI.getCarAttendanceReport(projectId, date);
    }

    @Override
    public List<Map> getCarAttendanceReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarAttendanceReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarAttendanceDateReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarAttendanceDateReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarAttendanceDateReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarAttendanceDateReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationCarReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getQualificationCarReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationCarReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getQualificationCarReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarCubicInfo(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarCubicInfo(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarCubicInfoMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarCubicInfoMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getFinishCarCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getFinishCarCountByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getReportInfoGroupBySlagSite(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByDate(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarsCountByDate(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByDateMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCarsCountByDateMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByDateHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoDaoI.getCarsCountByDateHistory(projectId, endTime);
    }

    @Override
    public List<Map> getTotalCountByTimer(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoDaoI.getTotalCountByTimer(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getTotalCountByTimerMonth(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoDaoI.getTotalCountByTimerMonth(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getTotalCountByTimerHistory(Long projectId, Date endTime, Integer pricingType) {
        return projectCarWorkInfoDaoI.getTotalCountByTimerHistory(projectId, endTime, pricingType);
    }

    @Override
    public List<Map> getTotalCount(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getTotalCount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalCountMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getTotalCountMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalCountHistory(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getTotalCountHistory(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndCarCodeAndDateIdentification(Long projectId, String carCode, Date date) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndCarCodeAndDateIdentification(projectId, carCode, date);
    }

    @Override
    public List<Map> getByProjectIdAndBetweenTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getByProjectIdAndBetweenTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getByProjectIdAndBetweenTimeHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoDaoI.getByProjectIdAndBetweenTimeHistory(projectId, endTime);
    }

    @Override
    public Long getCarsCountByProjectIdAndDateIdentificationAndCarCode(Long projectId, String carCode, Date date, Integer shift) {
        return projectCarWorkInfoDaoI.getCarsCountByProjectIdAndDateIdentificationAndCarCode(projectId, carCode, date, shift);
    }

    @Override
    public List<Map> getByProjectIdAndTime(Long projectId, Date date) {
        return projectCarWorkInfoDaoI.getByProjectIdAndTime(projectId, date);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndDateTime(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<Map> getWorkInfoListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getWorkInfoListByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDistinctByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getDistinctByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public Integer getCoalCountByTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getCoalCountByTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getOnDutyCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime, Integer shift) {
        return projectCarWorkInfoDaoI.getOnDutyCountByProjectIdAndTime(projectId, startTime, endTime, shift);
    }

    @Override
    public List<Map> getAllByProjectIdAndDateIdentificationAndShiftAndStatus(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndDateIdentificationAndShiftAndStatus(projectId, date, shift, status);
    }

    @Override
    public List<Map> getAllByProjectIdAndDateIdentificationAndShiftAndStatusGroupByErrorCode(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndDateIdentificationAndShiftAndStatusGroupByErrorCode(projectId, date, shift, status);
    }

    @Override
    public List<Map> getMergeCodeByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoDaoI.getMergeCodeByProjectIdAndDateIdentificationAndShift(projectId, date, shift, status);
    }

    @Override
    public void deleteAllByProjectIdAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        projectCarWorkInfoDaoI.deleteAllByProjectIdAndTimeDischarge(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndTimeLoadHalf(Long projectId, Date startTime, Date endTime, Integer status, String carCode) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndTimeLoadHalf(projectId, startTime, endTime, status, carCode);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShiftAndStatusByError(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndDateIdentificationAndShiftAndStatusByError(projectId, date, shift, status);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndTimeDischargeAndStatus(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndTimeDischargeAndStatus(projectId, startTime, endTime);
    }

    @Override
    public ProjectCarWorkInfo getAllByProjectIdAndCarCodeAndMaxTimeDischarge(Long projectId, String carCode) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode);
    }

    @Override
    public ProjectCarWorkInfo getAllByProjectIdAndCarCodeAndMaxTimeDischarge(Long projectId, String carCode, Date timeDischarge) {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode, timeDischarge);
    }

    @Override
    public List<Map> getTotalCountByProjectIdAndDateIdentificationAndShiftAndStatusAndCarCode(Long projectId, Date date, Integer shift, Integer status, String carCode) {
        return projectCarWorkInfoDaoI.getTotalCountByProjectIdAndDateIdentificationAndShiftAndStatusAndCarCode(projectId, date, shift, status, carCode);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShiftAndRemark() {
        return projectCarWorkInfoDaoI.getAllByProjectIdAndDateIdentificationAndShiftAndRemark();
    }
}

