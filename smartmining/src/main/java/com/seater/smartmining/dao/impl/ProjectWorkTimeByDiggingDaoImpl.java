package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectWorkTimeByDiggingDaoI;
import com.seater.smartmining.entity.ProjectSchedule;
import com.seater.smartmining.entity.ProjectWorkTimeByDigging;
import com.seater.smartmining.entity.repository.ProjectWorkTimeByDiggingRespository;
import com.seater.smartmining.enums.WorkInfoStatusEnums;
import com.seater.smartmining.enums.WorkStatusEnums;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/26 0026 13:41
 */
@Component
public class ProjectWorkTimeByDiggingDaoImpl implements ProjectWorkTimeByDiggingDaoI
{

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectWorkTimeByDiggingRespository projectWorkTimeByDiggingRespository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingmachineworkinfo:";
    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}
    @Override
    public ProjectWorkTimeByDigging get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectWorkTimeByDigging.class);
        }
        if(projectWorkTimeByDiggingRespository.existsById(id)){
            ProjectWorkTimeByDigging log = projectWorkTimeByDiggingRespository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectWorkTimeByDigging save(ProjectWorkTimeByDigging log) throws IOException {
        ProjectWorkTimeByDigging log1 = projectWorkTimeByDiggingRespository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectWorkTimeByDiggingRespository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectWorkTimeByDigging> query() {
        return null;
    }

    @Override
    public Page<ProjectWorkTimeByDigging> query(Specification<ProjectWorkTimeByDigging> spec) {
        return projectWorkTimeByDiggingRespository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public List<ProjectWorkTimeByDigging> queryAllByParams(Specification<ProjectWorkTimeByDigging> spec) {
        return projectWorkTimeByDiggingRespository.findAll(spec);
    }

    @Override
    public Page<ProjectWorkTimeByDigging> query(Specification<ProjectWorkTimeByDigging> spec, Pageable pageable) {
        return projectWorkTimeByDiggingRespository.findAll(spec,pageable);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdOrderById(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingRespository.getByProjectIdAndMaterialIdOrderById(projectId, materialId);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getByProjectIdAndMaterialIdAndTime(projectId, materialId, startTime, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public void setEndTimeById(Long id, Date endTime) {
        projectWorkTimeByDiggingRespository.setEndTimeById(id, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdByQuery(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingRespository.getByProjectIdAndMaterialIdByQuery(projectId, materialId);
    }

    @Override
    public List<Map> getWorkTimeByMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getWorkTimeByMaterialIdAndTime(projectId, materialId, startTime, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date createTime) {
        return projectWorkTimeByDiggingRespository.getTimeByMaterialIdAndCreateTime(projectId, materialId, createTime);
    }

    @Override
    public void setWorkInfoStatusEnumsById(Long id, WorkInfoStatusEnums workInfoStatusEnums) {
        projectWorkTimeByDiggingRespository.setWorkInfoStatusEnumsById(id, workInfoStatusEnums);
    }

    @Override
    public void setWorkInfoStatusEnumsAnsStatusById(Long id, WorkInfoStatusEnums workInfoStatusEnums, WorkStatusEnums workStatus) {
        projectWorkTimeByDiggingRespository.setWorkInfoStatusEnumsAnsStatusById(id, workInfoStatusEnums, workStatus);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdByQuery(Long projectId, Date reportDate) {
        return projectWorkTimeByDiggingRespository.getByProjectIdByQuery(projectId, reportDate);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdByQueryAndShift(Long projectId, Date reportDate, Integer shift) {
        return projectWorkTimeByDiggingRespository.getByProjectIdByQueryAndShift(projectId, reportDate, shift);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getTimeByMaterialIdAndCreateTime(projectId, materialId, startTime, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getTimeByMachineIdAndCreateTime(Long projectId, Long machineId, Date createTime) {
        return projectWorkTimeByDiggingRespository.getTimeByMachineIdAndCreateTime(projectId, machineId, createTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndStartTimeAndShift(Long projectId, Date startTime, Integer shift) {
        return projectWorkTimeByDiggingRespository.getAllByProjectIdAndStartTimeAndShift(projectId, startTime, shift);
    }

    @Override
    public List<Map> getWorkTimeByProjectIdAndStartTime(Long projectId, Date reportDate) {
        return projectWorkTimeByDiggingRespository.getWorkTimeByProjectIdAndStartTime(projectId, reportDate);
    }

    @Override
    public Map getAllByProjectIdAndStartTimeAndShiftAndPricingTypeEnums(Long projectId, Date startTime, Date endTime,Integer shift, Integer pricingType, Long machineId) {
        return projectWorkTimeByDiggingRespository.getAllByProjectIdAndStartTimeAndShiftAndPricingTypeEnums(projectId, startTime, endTime,shift, pricingType, machineId);
    }

    @Override
    public List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getMaterialIdByProjectIdAndStartTime(projectId, startTime, endTime);
    }

    @Override
    public Map getTotalWorkTimeByProjectIdAndDateTime(Long projectId, Date dateTime, Integer shift) {
        return projectWorkTimeByDiggingRespository.getTotalWorkTimeByProjectIdAndDateTimeAndShift(projectId, dateTime, shift);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getAllByProjectIdAndStartTimeAndShiftAndWorkStatus(Long projectId, Date dateTime, Integer shift, Integer workStatus) {
        return projectWorkTimeByDiggingRespository.getAllByProjectIdAndStartTimeAndShiftAndWorkStatus(projectId, dateTime, shift, workStatus);
    }

    @Override
    public List<Map> getMaterialIdByProjectIdAndStartTimeAndPage(Long projectId, Date dateTime, int cur, int pageSize) {
        return projectWorkTimeByDiggingRespository.getMaterialIdByProjectIdAndStartTimeAndPage(projectId, dateTime, cur, pageSize);
    }

    @Override
    public List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date dateTime) {
        return projectWorkTimeByDiggingRespository.getMaterialIdByProjectIdAndStartTime(projectId, dateTime);
    }

    @Override
    public List<Map> getTimeByMaterialIdAndStartTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getTimeByMaterialIdAndStartTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getInfoByMaterialIdAndMaterialIdAndStartTime(Long projectId, Date dateTime) {
        return projectWorkTimeByDiggingRespository.getInfoByMaterialIdAndMaterialIdAndStartTime(projectId, dateTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getAllByProjectId(Long projectId) {
        return projectWorkTimeByDiggingRespository.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getAllByProjectIdAndMaterialCode(Long projectId, String machineCode) {
        return projectWorkTimeByDiggingRespository.getAllByProjectIdAndMaterialCode(projectId, machineCode);
    }

    @Override
    public List<Map> getTotalTimeByProjectIdAndDate(Long projectId, Date date) {
        return projectWorkTimeByDiggingRespository.getTotalTimeByProjectIdAndDate(projectId, date);
    }

    @Override
    public List<Map> getTotalTimeAndPricingTypeByProjectIdAndDate(Long projectId, Date date) {
        return projectWorkTimeByDiggingRespository.getTotalTimeAndPricingTypeByProjectIdAndDate(projectId, date);
    }

    @Override
    public List<Map> getAllByProjectIdAndStartTimeAndEndTimeAndMaterialId(Long projectId, Long machineId, Date startDate, Date endDate) {
        return projectWorkTimeByDiggingRespository.getAllByProjectIdAndStartTimeAndEndTimeAndMaterialId(projectId, machineId, startDate, endDate);
    }

    @Override
    public List<Map> getDiggingTimeReport(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getDiggingTimeReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getDiggingTimeReportByMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeReportByHistory(Long projectId, Date endTime) {
        return projectWorkTimeByDiggingRespository.getDiggingTimeReportByHistory(projectId, endTime);
    }

    @Override
    public List<Map> getDiggingTimeInfo(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getDiggingTimeInfo(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeInfoMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getDiggingTimeInfoMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTimeInfoHistory(Long projectId, Date endTime) {
        return projectWorkTimeByDiggingRespository.getDiggingTimeInfoHistory(projectId, endTime);
    }

    @Override
    public List<Map> getDiggingInfoByProjectId(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getDiggingInfoByProjectId(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalDiggingTimeReport(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getTotalDiggingTimeReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getTotalDiggingTimeReportByMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalDiggingTimeReportByHistory(Long projectId, Date endTime) {
        return projectWorkTimeByDiggingRespository.getTotalDiggingTimeReportByHistory(projectId, endTime);
    }

    @Override
    public List<Map> getWorkTimeByPricingType(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectWorkTimeByDiggingRespository.getWorkTimeByPricingType(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getWorkTimeByPricingTypeMonth(Long projectId, Date startTime, Date endTime, Integer pricingType) {
        return projectWorkTimeByDiggingRespository.getWorkTimeByPricingTypeMonth(projectId, startTime, endTime, pricingType);
    }

    @Override
    public List<Map> getWorkTimeByPricingTypeHistory(Long projectId, Date endTime, Integer pricingType) {
        return projectWorkTimeByDiggingRespository.getWorkTimeByPricingTypeHistory(projectId, endTime, pricingType);
    }

    @Override
    public List<Map> reportDiggingWorkTimeByPlace(Long projectId, Date date) {
        return projectWorkTimeByDiggingRespository.reportDiggingWorkTimeByPlace(projectId, date);
    }

    @Override
    public List<Map> reportDiggingWorkTimeByMaterial(Long projectId, Date date) {
        return projectWorkTimeByDiggingRespository.reportDiggingWorkTimeByMaterial(projectId, date);
    }

    @Override
    public List<Map> getAttendanceByTime(Long projectId, Date date) {
        return projectWorkTimeByDiggingRespository.getAttendanceByTime(projectId, date);
    }

    @Override
    public List<Map> getAttendanceByTimeMonth(Long projectId, Date startTime, Date endTime) {
        return projectWorkTimeByDiggingRespository.getAttendanceByTimeMonth(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialId(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingRespository.getByProjectIdAndMaterialId(projectId, materialId);
    }

    @Override
    public List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAdd(Long projectId, Long materialId) {
        return projectWorkTimeByDiggingRespository.getByProjectIdAndMaterialIdAdd(projectId, materialId);
    }

    @Override
    public Long getAllByProjectIdAndMaterialCodeAndDateIdentificationAndShift(Long projectId, String machineCode, Date date, Integer shift) {
        return projectWorkTimeByDiggingRespository.getAllByProjectIdAndMaterialCodeAndDateIdentificationAndShift(projectId, machineCode, date, shift);
    }
}
