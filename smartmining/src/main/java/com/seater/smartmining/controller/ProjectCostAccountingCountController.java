package com.seater.smartmining.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectCostAccountingCount;
import com.seater.smartmining.service.ProjectCarCostAccountingServiceI;
import com.seater.smartmining.service.ProjectCostAccountingCountServiceI;
import com.seater.smartmining.service.ProjectDiggingCostAccountingServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/27 0027 12:25
 */
@RestController
@RequestMapping("/api/projectcostaccountingcount")
public class ProjectCostAccountingCountController {

    @Autowired
    private ProjectCostAccountingCountServiceI projectCostAccountingCountServiceI;
    @Autowired
    private ProjectCarCostAccountingServiceI projectCarCostAccountingServiceI;
    @Autowired
    private ProjectDiggingCostAccountingServiceI projectDiggingCostAccountingServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/modify")
    public Result modify(ProjectCostAccountingCount count){
        try{
            projectCostAccountingCountServiceI.save(count);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/amount")
    public Result carAmount(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
            }
            else if(choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            }
            else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<Map> resultList = new ArrayList<>();
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        List<Map> carAmountList = new ArrayList<>();
        List<Map> diggingAmountList = new ArrayList<>();
        switch (choose) {
            case 1:
                carAmountList = projectCarCostAccountingServiceI.getHistoryFillAmountAndAmount(projectId, startTime, endTime);
                diggingAmountList = projectDiggingCostAccountingServiceI.getHistoryFillAmountAndAmount(projectId, startTime, endTime);
                break;
            case 2:
                carAmountList = projectCarCostAccountingServiceI.getHistoryFillAmountAndAmountMonth(projectId, startTime, endTime);
                diggingAmountList = projectDiggingCostAccountingServiceI.getHistoryFillAmountAndAmountMonth(projectId, startTime, endTime);
                break;
            case 3:
                carAmountList = projectCarCostAccountingServiceI.getHistoryAmountHistory(projectId, endTime);
                diggingAmountList = projectDiggingCostAccountingServiceI.getHistoryAmountHistory(projectId, endTime);
                break;
        }
        if(choose != 3) {
            //渣车索引
            Map<String, Integer> carIndexMap = new HashMap<>();
            for (int i = 0; i < carAmountList.size(); i++) {
                Date reportDate = null;
                String key = null;
                if (choose == 1) {
                    reportDate = DateUtils.stringFormatDate(carAmountList.get(i).get("report_date").toString(), SmartminingConstant.DATEFORMAT);
                    key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.YEARMONTHDAUFORMAT);
                } else {
                    reportDate = DateUtils.stringFormatDate(carAmountList.get(i).get("report_date").toString(), SmartminingConstant.MONTHDAYFORMAT);
                    key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.MONTHDAYFORMAT);
                }
                carIndexMap.put(key, i);
            }
            //挖机索引
            Map<String, Integer> machineIndexMap = new HashMap<>();
            for (int i = 0; i < diggingAmountList.size(); i++) {
                Date reportDate = null;
                String key = null;
                if (choose == 1) {
                    reportDate = DateUtils.stringFormatDate(diggingAmountList.get(i).get("report_date").toString(), SmartminingConstant.DATEFORMAT);
                    key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.YEARMONTHDAUFORMAT);
                } else {
                    reportDate = DateUtils.stringFormatDate(diggingAmountList.get(i).get("report_date").toString(), SmartminingConstant.MONTHDAYFORMAT);
                    key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.MONTHDAYFORMAT);
                }
                machineIndexMap.put(key, i);
            }
            for(int i = 0; i < dateList.size(); i++){
                Date date = null;
                if (choose == 1) {
                    date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.YEARMONTHDAUFORMAT);
                } else {
                    date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.MONTHDAYFORMAT);
                }
                String key = dateList.get(i);
                Integer carIndex = carIndexMap.get(key);
                Integer machineIndex = machineIndexMap.get(key);
                //渣车费用
                Long amountByCarByMin = carIndex != null ? Long.parseLong(carAmountList.get(carIndex).get("amount").toString()) : 0L;
                //渣车加油费用
                Long fillAmountByCarByMin = carIndex != null ? Long.parseLong(carAmountList.get(carIndex).get("amount_by_fill").toString()) : 0L;
                //挖机费用
                Long amountByDiggingByMin = machineIndex != null ? Long.parseLong(diggingAmountList.get(machineIndex).get("total_amount").toString()) : 0L;;
                //挖机计时加油费用
                Long fillAmountByDiggingByTimerMin = machineIndex != null ? Long.parseLong(diggingAmountList.get(machineIndex).get("amount_by_fill_by_timer").toString()) : 0L;
                //挖机计方加油费用
                Long fillAmountByDiggingByCubicMin = machineIndex != null ? Long.parseLong(diggingAmountList.get(machineIndex).get("amount_by_fill_by_cubic").toString()) : 0L;
                //挖机总加油费用
                Long fillAmountByDiggingByMin = fillAmountByDiggingByTimerMin + fillAmountByDiggingByCubicMin;
                BigDecimal totalAmount = new BigDecimal(((float)amountByCarByMin + amountByDiggingByMin) / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal fillAmount = new BigDecimal((float)(fillAmountByCarByMin + fillAmountByDiggingByMin) / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
                Map map = new HashMap();
                map.put("totalAmount", totalAmount);
                map.put("fillAmount", fillAmount);
                map.put("date", date);
                resultList.add(map);
            }
        }
        return Result.ok(resultList);
    }
}
