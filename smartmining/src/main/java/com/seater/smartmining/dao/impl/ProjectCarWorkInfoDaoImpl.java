package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarWorkInfoDaoI;
import com.seater.smartmining.entity.ProjectCarWorkInfo;
import com.seater.smartmining.entity.repository.ProjectCarWorkInfoRepository;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectCarWorkInfoDaoImpl implements ProjectCarWorkInfoDaoI
{
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarWorkInfoRepository projectCarWorkInfoRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarworkinfo:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarWorkInfo> query(Specification<ProjectCarWorkInfo> spec, Pageable pageable) {
        return projectCarWorkInfoRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectCarWorkInfo> query(Specification<ProjectCarWorkInfo> spec) {
        return projectCarWorkInfoRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarWorkInfo> query(Pageable pageable) {
        return projectCarWorkInfoRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarWorkInfo> query() {
        return projectCarWorkInfoRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectCarWorkInfo get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarWorkInfo.class);
        }
        if(projectCarWorkInfoRepository.existsById(id))
        {
            ProjectCarWorkInfo log = projectCarWorkInfoRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCarWorkInfo save(ProjectCarWorkInfo log) throws IOException {
        ProjectCarWorkInfo log1 = projectCarWorkInfoRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarWorkInfoRepository.deleteById(id);
    }

    @Override
    public List<ProjectCarWorkInfo> getAll() {
        return projectCarWorkInfoRepository.findAll();
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeCheck(Long projectId, Long carId, Date timeChaeck) {
        return projectCarWorkInfoRepository.getByProjectIdAndCarIdAndTimeCheck(projectId, carId, timeChaeck);
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndDiggingMachineIdAndTimeCheck(Long projectId, Long diggingMachineId, Date timeCheck) {
        return projectCarWorkInfoRepository.getByProjectIdAndDiggingMachineIdAndTimeCheck(projectId, diggingMachineId, timeCheck);
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeDischarge(Long projectId, Long carId, Date timeDischarge) {
        return projectCarWorkInfoRepository.getByProjectIdAndCarIdAndTimeCheck(projectId, carId, timeDischarge);
    }

    @Override
    public ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeLoad(Long projectId, Long carId, Date timeLoad) {
        return projectCarWorkInfoRepository.getByProjectIdAndCarIdAndTimeLoad(projectId, carId, timeLoad);
    }

    /*@Override
    public List<Map> getDistanceListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getDistanceListByProjectIdAndTime(projectId, startTime, endTime);
    }*/

    @Override
    public List<Map> getDistanceListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getDistanceListByProjectIdAndTime(projectId, startTime, endTime);
    }

    /*@Override
    public List<Map> getCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCountListByProjectIdAndTime(projectId, startTime, endTime);
    }*/

    @Override
    public List<Map> getCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCountListByProjectIdAndTime(projectId, startTime, endTime);
    }

    /*@Override
    public List<Map> getCarGrandTotalListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarGrandTotalListByProjectIdAndTime(projectId, startTime, endTime);
    }*/

    @Override
    public List<Map> getCarGrandTotalListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarGrandTotalListByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingDayCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoRepository.getDiggingDayCountListByProjectIdAndTime(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getDiggingDayCountListByProjectIdAndTimeGroupByShift(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getDiggingDayCountListByProjectIdAndTimeGroupByShift(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingDayCountListByProjectIdAndTimeAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId) {
        return projectCarWorkInfoRepository.getDiggingDayCountListByProjectIdAndTimeAndMachineId(projectId, startTime, endTime, machineId);
    }

    @Override
    public List<Map> getMaterialDetailByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getMaterialDetailByProjectIdAndMachineIdAndTime(projectId,machineId,startTime,endTime);
    }

    @Override
    public List<Map> getMaterialDetailByProjectIdAndMachineIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getMaterialDetailByProjectIdAndMachineIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getProjectCarWorkInfoByCreateDate(Date reportDate) {
        return projectCarWorkInfoRepository.getProjectCarWorkInfoByCreateDate(reportDate);
    }

    @Override
    public List<Map> getSettlementDetailByProjectIdAndTimeAndCarId(Long projectId, Date startTime, Date endTime, Long carId) {
        return projectCarWorkInfoRepository.getSettlementDetailByProjectIdAndTimeAndCarId(projectId, startTime, endTime ,carId);
    }

    @Override
    public List<Map> getCubicDetailByProjectIdAndDiggingMachineIdAndTime(Long projectId, Long machineId, List<String> dateList, Integer pricingType) {
        return projectCarWorkInfoRepository.getCubicDetailByProjectIdAndDiggingMachineIdAndTime(projectId, machineId, dateList, pricingType);
    }

    @Override
    public List<Map> getElseTotalByProjectIdAndDiggingMachineIdAndTime(Long projectId, Long machineId, List<String> dateList, Integer pricingType) {
        return projectCarWorkInfoRepository.getElseTotalByProjectIdAndDiggingMachineIdAndTime(projectId, machineId, dateList, pricingType);
    }

    @Override
    public List<Map> getDetailTotalByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoRepository.getDetailTotalByProjectIdAndMachineIdAndTime(projectId, machineId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getByCubicDetailOrderByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getByCubicDetailOrderByProjectIdAndCarIdAndTime(projectId, carId, startTime, endTime);
    }

    @Override
    public List<Map> getMachineIdByProjectIdAndPageAndTime(Long projectId, Integer current, Integer pageSize, Date time) {
        return projectCarWorkInfoRepository.getMachineIdByProjectIdAndPageAndTime(projectId, current, pageSize, time);
    }

    @Override
    public List<Map> getAllSettlementByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getAllSettlementByProjectIdAndCarIdAndTime(projectId, carId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date beginDate, Date endDate) {
        return projectCarWorkInfoRepository.getCarsCountByProjectIdAndCarIdAndTime(projectId, carId, beginDate, endDate);
    }

    @Override
    public List<Map> getSumCubicByTime(Long projectId, Long machineId, Date date) {
        return projectCarWorkInfoRepository.getSumCubicByTime(projectId, machineId, date);
    }

    @Override
    public List<Map> getDateIdentificationByMachineId(Long projectId, Long machineId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getDateIdentificationByMachineId(projectId, machineId, startTime, endTime);
    }

    @Override
    public List<Map> getMachineIdListByDate(Long projectId, Date time) {
        return projectCarWorkInfoRepository.getMachineIdListByDate(projectId, time);
    }

    @Override
    public List<Map> getAppDiggingInfoByProjectIdAndDate(Long projectId, Date date) {
        return projectCarWorkInfoRepository.getAppDiggingInfoByProjectIdAndDate(projectId, date);
    }

    @Override
    public Map getCarsCountByProjectIdAndDateIdentificationAndPass(Long projectId, Date date, Integer pass, Integer shift) {
        return projectCarWorkInfoRepository.getCarsCountByProjectIdAndDateIdentificationAndPass(projectId, date, pass, shift);
    }

    @Override
    public List<Map> getCarsCountByProjectIdAndDateIdentification(Long projectId, Date date, Integer shift) {
        return projectCarWorkInfoRepository.getCarsCountByProjectIdAndDateIdentification(projectId, date, shift);
    }

    @Override
    public Map getHistoryInfoByTime(Long projectId, Date startTime) {
        return projectCarWorkInfoRepository.getHistoryInfoByTime(projectId, startTime);
    }

    @Override
    public List<Map> getUnpassInfoByProjectIdAndDate(Long projectId, Date date) {
        return projectCarWorkInfoRepository.getUnpassInfoByProjectIdAndDate(projectId, date);
    }

    @Override
    public List<ProjectCarWorkInfo> getCarsCountUnPassByProjectIdAndDate(Long projectId, Date date, Integer pass, Integer shift) {
        return projectCarWorkInfoRepository.getCarsCountUnPassByProjectIdAndDate(projectId, date, pass, shift);
    }

    @Override
    public List<Map> countByProjectIdAndDateIdentification(Long projectId, Date date) {
        return projectCarWorkInfoRepository.countByProjectIdAndDateIdentification(projectId, date);
    }

    @Override
    public Integer countByProjectIdAndDateIdentificationAndMaterialId(Long projectId, Date date, Long materialId) {
        return projectCarWorkInfoRepository.countByProjectIdAndDateIdentificationAndMaterialId(projectId, date, materialId);
    }

    @Override
    public List<Map> getTotalCubicAndCountByProjectIdAndDateIdentification(Long projectId, Date date) {
        return projectCarWorkInfoRepository.getTotalCubicAndCountByProjectIdAndDateIdentification(projectId, date);
    }

    @Override
    public void savAll(List<ProjectCarWorkInfo> projectCarWorkInfos) {
        projectCarWorkInfoRepository.saveAll(projectCarWorkInfos);
    }

    @Override
    public List<ProjectCarWorkInfo> queryAllByParams(Specification<ProjectCarWorkInfo> specification) {
        return projectCarWorkInfoRepository.findAll(specification);
    }

    @Override
    public List<Map> getCountByProjectIdAndShiftAndDate(Long projectId, Integer shift, Date date) {
        return projectCarWorkInfoRepository.getCountByProjectIdAndShiftAndDate(projectId, shift, date);
    }

    @Override
    public List<Map> getByProjectId(Long projectId) {
        return projectCarWorkInfoRepository.getByProjectId(projectId);
    }

    @Override
    public List<Map> getDiggingWorkReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getDiggingWorkReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingWorkReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getDiggingWorkReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingWorkReportHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoRepository.getDiggingWorkReportHistory(projectId, endTime);
    }

    @Override
    public List<Map> getQualificationReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getQualificationReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getQualificationReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationReportHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoRepository.getQualificationReportHistory(projectId, endTime);
    }

    @Override
    public List<Map> findByProjectIdAndStatus(Long projectId, Date startDate, Date endDate, Integer status) {
        return projectCarWorkInfoRepository.findByProjectIdAndStatus(projectId, startDate, endDate, status);
    }

    @Override
    public List<Map> getCarAttendanceReport(Long projectId, Date date) {
        return projectCarWorkInfoRepository.getCarAttendanceReport(projectId, date);
    }

    @Override
    public List<Map> getCarAttendanceReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarAttendanceReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarAttendanceDateReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarAttendanceDateReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarAttendanceDateReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarAttendanceDateReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationCarReport(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getQualificationCarReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getQualificationCarReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getQualificationCarReportMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarCubicInfo(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarCubicInfo(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarCubicInfoMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarCubicInfoMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getFinishCarCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getFinishCarCountByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getReportInfoGroupBySlagSite(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByDate(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarsCountByDate(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByDateMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCarsCountByDateMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarsCountByDateHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoRepository.getCarsCountByDateHistory(projectId, endTime);
    }

    @Override
    public List<Map> getTotalCountByTimer(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoRepository.getTotalCountByTimer(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getTotalCountByTimerMonth(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectCarWorkInfoRepository.getTotalCountByTimerMonth(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getTotalCountByTimerHistory(Long projectId, Date endTime, Integer pricingType) {
        return projectCarWorkInfoRepository.getTotalCountByTimerHistory(projectId, endTime, pricingType);
    }

    @Override
    public List<Map> getTotalCount(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getTotalCount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalCountMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getTotalCountMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalCountHistory(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getTotalCountHistory(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndCarCodeAndDateIdentification(Long projectId, String carCode, Date date) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndCarCodeAndDateIdentification(projectId, carCode, date);
    }

    @Override
    public List<Map> getByProjectIdAndBetweenTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getByProjectIdAndBetweenTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getByProjectIdAndBetweenTimeHistory(Long projectId, Date endTime) {
        return projectCarWorkInfoRepository.getByProjectIdAndBetweenTimeHistory(projectId, endTime);
    }

    @Override
    public Long getCarsCountByProjectIdAndDateIdentificationAndCarCode(Long projectId, String carCode, Date date, Integer shift) {
        return projectCarWorkInfoRepository.getCarsCountByProjectIdAndDateIdentificationAndCarCode(projectId, carCode, date, shift);
    }

    @Override
    public List<Map> getByProjectIdAndTime(Long projectId, Date date) {
        return projectCarWorkInfoRepository.getByProjectIdAndTime(projectId, date);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndDateTime(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<Map> getWorkInfoListByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getWorkInfoListByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDistinctByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getDistinctByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public Integer getCoalCountByTime(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getCoalCountByTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getOnDutyCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime, Integer shift) {
        return projectCarWorkInfoRepository.getOnDutyCountByProjectIdAndTime(projectId, startTime, endTime, shift);
    }

    @Override
    public List<Map> getAllByProjectIdAndDateIdentificationAndShiftAndStatus(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndDateIdentificationAndShiftAndStatus(projectId, date, shift, status);
    }

    @Override
    public List<Map> getAllByProjectIdAndDateIdentificationAndShiftAndStatusGroupByErrorCode(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndDateIdentificationAndShiftAndStatusGroupByErrorCode(projectId, date, shift, status);
    }

    @Override
    public List<Map> getMergeCodeByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoRepository.getMergeCodeByProjectIdAndDateIdentificationAndShift(projectId, date, shift, status);
    }

    @Override
    public void deleteAllByProjectIdAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        projectCarWorkInfoRepository.deleteAllByProjectIdAndTimeDischarge(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndTimeLoadHalf(Long projectId, Date startTime, Date endTime, Integer status, String carCode) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndTimeLoadHalf(projectId, startTime, endTime, status, carCode);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShiftAndStatusByError(Long projectId, Date date, Integer shift, Integer status) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndDateIdentificationAndShiftAndStatusByError(projectId, date, shift, status);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndTimeDischargeAndStatus(Long projectId, Date startTime, Date endTime) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndTimeDischargeAndStatus(projectId, startTime, endTime);
    }

    @Override
    public ProjectCarWorkInfo getAllByProjectIdAndCarCodeAndMaxTimeDischarge(Long projectId, String carCode) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode);
    }

    @Override
    public ProjectCarWorkInfo getAllByProjectIdAndCarCodeAndMaxTimeDischarge(Long projectId, String carCode, Date timeDischarge) {
        return projectCarWorkInfoRepository.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode, timeDischarge);
    }

    @Override
    public List<Map> getTotalCountByProjectIdAndDateIdentificationAndShiftAndStatusAndCarCode(Long projectId, Date date, Integer shift, Integer status, String carCode) {
        return projectCarWorkInfoRepository.getTotalCountByProjectIdAndDateIdentificationAndShiftAndStatusAndCarCode(projectId, date, shift, status, carCode);
    }

    @Override
    public List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShiftAndRemark() {
        return projectCarWorkInfoRepository.getAllByProjectIdAndDateIdentificationAndShiftAndRemark();
    }
}
