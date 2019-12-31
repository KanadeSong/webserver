package com.seater.smartmining.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.VaildEnums;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.SpringSchedule;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Description 作业排行榜
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/6/21 12:08
 */
@RestController
@RequestMapping("/api/projectWorkInfoRank")
public class ProjectWorkInfoRankController extends BaseController {

    @Autowired
    ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    ProjectDiggingDayReportServiceI projectDiggingDayReportServiceI;
    @Autowired
    ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    ProjectCarServiceI projectCarServiceI;
    @Autowired
    ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    ProjectHourPriceServiceI projectHourPriceServiceI;
    @Autowired
    ProjectDiggingMachineMaterialServiceI projectDiggingMachineMaterialServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SpringSchedule springSchedule;
    @Autowired
    private WorkDateService workDateService;

    /**
     * 挖机排行榜
     *
     * @param request
     * @param startDate
     * @param endDate
     * @return
     */
    @PostMapping("/diggingMachineRank")
    public Object diggingMachineRank(HttpServletRequest request, Date startDate, Date endDate, String type) throws IOException {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Map<String, Date> startDateMap = workDateService.getWorkTime(projectId, startDate);
        Date earlyStart = startDateMap.get("start");
        Map<String, Date> endDateMap = workDateService.getWorkTime(projectId, endDate);
        Date endStart = endDateMap.get("start");
        if(startDate.getTime() < earlyStart.getTime())
            startDate = DateUtils.subtractionOneDay(startDate);
        if(endDate.getTime() < endStart.getTime())
            endDate = DateUtils.subtractionOneDay(endDate);
        startDate = DateUtil.beginOfDay(startDate);
        endDate = DateUtil.endOfDay(endDate);
        String key = ProjectUtils.cacheKeyByStartAndEnd(startDate, endDate);
        String diggingMachineRank = stringRedisTemplate.opsForValue().get(SmartminingConstant.DIGGING_MACHINE_RANK + ":" + key + ":" + projectId);
        if (!ObjectUtils.isEmpty(diggingMachineRank)) {
            return diggingMachineRank;
        }
        return springSchedule.diggingMachineRank(startDate, endDate, type, projectId);

    }

    /**
     * 渣车排行榜
     *
     * @param request
     * @param startDate
     * @param endDate
     * @return
     */
    @PostMapping("/projectCarRank")
    public Object projectCarRank(HttpServletRequest request, Date startDate, Date endDate, String type) throws IOException {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Map<String, Date> startDateMap = workDateService.getWorkTime(projectId, startDate);
        Date earlyStart = startDateMap.get("start");
        Map<String, Date> endDateMap = workDateService.getWorkTime(projectId, endDate);
        Date endStart = endDateMap.get("start");
        if(startDate.getTime() < earlyStart.getTime())
            startDate = DateUtils.subtractionOneDay(startDate);
        if(endDate.getTime() < endStart.getTime())
            endDate = DateUtils.subtractionOneDay(endDate);
        startDate = DateUtil.beginOfDay(startDate);
        endDate = DateUtil.endOfDay(endDate);
        String key = ProjectUtils.cacheKeyByStartAndEnd(startDate, endDate);
        String diggingMachineRank = stringRedisTemplate.opsForValue().get(SmartminingConstant.PROJECT_CAR_RANK + ":" + key + ":" + projectId);
        if (!ObjectUtils.isEmpty(diggingMachineRank)) {
            return diggingMachineRank;
        }
        return springSchedule.projectCarRank(startDate, endDate, type, projectId);
    }

}
