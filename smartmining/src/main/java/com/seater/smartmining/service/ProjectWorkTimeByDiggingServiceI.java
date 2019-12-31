package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectWorkTimeByDigging;
import com.seater.smartmining.enums.WorkInfoStatusEnums;
import com.seater.smartmining.enums.WorkStatusEnums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/26 0026 13:57
 */
public interface ProjectWorkTimeByDiggingServiceI {
    ProjectWorkTimeByDigging get(Long id) throws IOException;
    ProjectWorkTimeByDigging save(ProjectWorkTimeByDigging log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectWorkTimeByDigging> query();
    Page<ProjectWorkTimeByDigging> query(Specification<ProjectWorkTimeByDigging> spec);

    List<ProjectWorkTimeByDigging> queryAllByParams(Specification<ProjectWorkTimeByDigging> spec);
    Page<ProjectWorkTimeByDigging> query(Specification<ProjectWorkTimeByDigging> spec, Pageable pageable);
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdOrderById(Long projectId, Long materialId);
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime);
    List<ProjectWorkTimeByDigging> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    void setEndTimeById(Long id, Date endTime);
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdByQuery(Long projectId, Long materialId);
    List<Map> getWorkTimeByMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime);
    List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date createTime);
    void setWorkInfoStatusEnumsById(Long id, WorkInfoStatusEnums workInfoStatusEnums);
    void setWorkInfoStatusEnumsAnsStatusById(Long id, WorkInfoStatusEnums workInfoStatusEnums, WorkStatusEnums workStatus);
    List<ProjectWorkTimeByDigging> getByProjectIdByQuery(Long projectId, Date reportDate);
    List<ProjectWorkTimeByDigging> getByProjectIdByQueryAndShift(Long projectId, Date reportDate, Integer shift);
    List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date startTime, Date endTime);
    List<ProjectWorkTimeByDigging> getTimeByMachineIdAndCreateTime(Long projectId, Long machineId, Date createTime);
    List<Map> getAllByProjectIdAndStartTimeAndShift(Long projectId, Date startTime, Integer shift);
    List<Map> getWorkTimeByProjectIdAndStartTime(Long projectId, Date reportDate);
    Map getAllByProjectIdAndStartTimeAndShiftAndPricingTypeEnums(Long projectId, Date startTime, Date endTime,Integer shift, Integer pricingType, Long machineId);
    List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date startTime, Date endTime);
    Map getTotalWorkTimeByProjectIdAndDateTime(Long projectId, Date dateTime, Integer shift);
    List<ProjectWorkTimeByDigging> getAllByProjectIdAndStartTimeAndShiftAndWorkStatus(Long projectId, Date dateTime, Integer shift, Integer workStatus);
    List<Map> getMaterialIdByProjectIdAndStartTimeAndPage(Long projectId, Date dateTime, int cur, int pageSize);
    List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date dateTime);
    List<Map> getTimeByMaterialIdAndStartTime(Long projectId, Date startTime, Date endTime);
    List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<Map> getInfoByMaterialIdAndMaterialIdAndStartTime(Long projectId, Date dateTime);
    List<ProjectWorkTimeByDigging> getAllByProjectId(Long projectId);
    List<ProjectWorkTimeByDigging> getAllByProjectIdAndMaterialCode(Long projectId, String machineCode);
    List<Map> getTotalTimeByProjectIdAndDate(Long projectId, Date date);
    List<Map> getTotalTimeAndPricingTypeByProjectIdAndDate(Long projectId, Date date);
    List<Map> getAllByProjectIdAndStartTimeAndEndTimeAndMaterialId(Long projectId, Long machineId, Date startDate, Date endDate);
    List<Map> getDiggingTimeReport(Long projectId, Date startTime, Date endTime);
    List<Map> getDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime);
    List<Map> getDiggingTimeReportByHistory(Long projectId, Date endTime);
    List<Map> getDiggingTimeInfo(Long projectId, Date startTime, Date endTime);
    List<Map> getDiggingTimeInfoMonth(Long projectId, Date startTime, Date endTime);
    List<Map> getDiggingTimeInfoHistory(Long projectId, Date endTime);
    List<Map> getDiggingInfoByProjectId(Long projectId, Date startTime, Date endTime);
    List<Map> getTotalDiggingTimeReport(Long projectId, Date startTime, Date endTime);
    List<Map> getTotalDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime);
    List<Map> getTotalDiggingTimeReportByHistory(Long projectId, Date endTime);
    List<Map> getWorkTimeByPricingType(Long projectId, Date startTime, Date endTime, Integer pricingType);
    List<Map> getWorkTimeByPricingTypeMonth(Long projectId, Date startTime, Date endTime, Integer pricingType);
    List<Map> getWorkTimeByPricingTypeHistory(Long projectId, Date endTime, Integer pricingType);
    List<Map> reportDiggingWorkTimeByPlace(Long projectId, Date date);
    List<Map> reportDiggingWorkTimeByMaterial(Long projectId, Date date);
    List<Map> getAttendanceByTime(Long projectId, Date date);
    List<Map> getAttendanceByTimeMonth(Long projectId, Date startTime, Date endTime);
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialId(Long projectId, Long materialId);
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAdd(Long projectId, Long materialId);
    Long getAllByProjectIdAndMaterialCodeAndDateIdentificationAndShift(Long projectId, String machineCode, Date date, Integer shift);
}
