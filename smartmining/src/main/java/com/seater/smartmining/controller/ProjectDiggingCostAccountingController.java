package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.StatisticsTypeEnums;
import com.seater.smartmining.report.ReportService;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/21 0021 9:15
 */
@RestController
@RequestMapping("/api/projectdiggingcostaccounting")
public class ProjectDiggingCostAccountingController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private ProjectCarCostAccountingServiceI projectCarCostAccountingServiceI;
    @Autowired
    private ProjectDiggingCostAccountingServiceI projectDiggingCostAccountingServiceI;
    @Autowired
    private ProjectCostAccountingCountServiceI projectCostAccountingCountServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/costAccount")
    public Result costAccount(HttpServletRequest request, Date reportDate) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            projectCarCostAccountingServiceI.deleteByProjectIdAndReportDate(projectId, reportDate);
            projectDiggingCostAccountingServiceI.deleteByProjectIdAndReportDate(projectId, reportDate);
            projectCostAccountingCountServiceI.deleteByProjectIdAndReportDate(projectId, reportDate);
            reportService.costAccountingTotal(projectId, reportDate);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Date reportDate) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        reportDate = DateUtils.createReportDateByMonth(reportDate);
        Map<String, Object> result = new HashMap<>();
        List<ProjectCostAccountingCount> countList = projectCostAccountingCountServiceI.getByProjectIdAndReportDate(projectId, reportDate);
        List<ProjectCarCostAccounting> carList = projectCarCostAccountingServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
        List<ProjectDiggingCostAccounting> diggingList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
        result.put("count", countList);
        result.put("car", carList);
        result.put("digging", diggingList);
        return Result.ok(result);
    }

    @RequestMapping("/queryTemp")
    public Result queryTemp(HttpServletRequest request, @RequestParam Date startTime, @RequestParam Date endTime) {
        Long projectId = CommonUtil.getProjectId(request);
        Map diggingTempMap = projectDiggingCostAccountingServiceI.getAllByProjectIdAndReportDate(projectId, startTime, endTime);
        ProjectDiggingCostAccounting diggingCostAccounting = new ProjectDiggingCostAccounting();
        //计时挖机车数
        Long totalCountByTimer = Long.parseLong(diggingTempMap.get("totalCountByTimer").toString());
        //计时挖机方量
        Long totalCubicByTimer = Long.parseLong(diggingTempMap.get("totalCubicByTimer").toString());
        //计时挖机台时
        BigDecimal workTimeByTimer = new BigDecimal(diggingTempMap.get("workTimeByTimer").toString());
        //计时挖机金额
        Long amountByTimer = Long.parseLong(diggingTempMap.get("amountByTimer").toString());
        //计时挖机油量
        Long fillCountByTimer = Long.parseLong(diggingTempMap.get("fillCountByTimer").toString());
        //计时挖机用油金额
        Long amountByFillByTimer = Long.parseLong(diggingTempMap.get("amountByFillByTimer").toString());
        //计时总金额
        Long totalAmountByTimer = Long.parseLong(diggingTempMap.get("totalAmountByTimer").toString());
        //单勾台时
        BigDecimal workTimeBySingleHook = new BigDecimal(diggingTempMap.get("workTimeBySingleHook").toString());
        //单勾金额
        Long amountBySingleHook = Long.parseLong(diggingTempMap.get("amountBySingleHook").toString());
        //单勾油量
        Long fillCountBySingleHook = Long.parseLong(diggingTempMap.get("fillCountBySingleHook").toString());
        //单勾用油金额
        Long amountByFillBySingleHook = Long.parseLong(diggingTempMap.get("amountByFillBySingleHook").toString());
        //单勾总金额
        Long totalAmountBySingleHook = Long.parseLong(diggingTempMap.get("totalAmountBySingleHook").toString());
        //炮锤台时
        BigDecimal workTimeByGunHammer = new BigDecimal(diggingTempMap.get("workTimeByGunHammer").toString());
        //炮锤金额
        Long amountByGunHammer = Long.parseLong(diggingTempMap.get("amountByGunHammer").toString());
        //炮锤用油量
        Long fillCountByGunHammer = Long.parseLong(diggingTempMap.get("fillCountByGunHammer").toString());
        //炮锤用油金额
        Long amountByFillByGunHammer = Long.parseLong(diggingTempMap.get("amountByFillByGunHammer").toString());
        //炮锤总金额
        Long totalAmountByGunHammer = Long.parseLong(diggingTempMap.get("totalAmountByGunHammer").toString());
        //计方台时
        BigDecimal workTimeByCubic = new BigDecimal(diggingTempMap.get("workTimeByCubic").toString());
        //计方车数
        Long totalCountByCubic = Long.parseLong(diggingTempMap.get("totalCountByCubic").toString());
        //计方方量
        Long totalCubicByCubic = Long.parseLong(diggingTempMap.get("totalCubicByCubic").toString());
        //计方油量
        Long fillCountByCubic = Long.parseLong(diggingTempMap.get("fillCountByCubic").toString());
        //计方用油金额
        Long amountByFillByCubic = Long.parseLong(diggingTempMap.get("amountByFillByCubic").toString());
        //计方总金额
        Long totalAmountByCubic = Long.parseLong(diggingTempMap.get("totalAmountByCubic").toString());
        //总金额
        Long totalAmount = Long.parseLong(diggingTempMap.get("totalAmount").toString());
        //总用油量
        Long fillCountByTotal = Long.parseLong(diggingTempMap.get("fillCountByTotal").toString());
        //总用油金额
        Long amountByFillByTotal = Long.parseLong(diggingTempMap.get("amountByFillByTotal").toString());
        //计时平均油耗
        BigDecimal avgUseFillByTimeByTimer = workTimeByTimer.compareTo(BigDecimal.ZERO) != 0 ? BigDecimal.valueOf(fillCountByTimer).divide(workTimeByTimer, 0, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
        //计时平均装车数
        BigDecimal avgCarsByTimeByTimer = workTimeByTimer.compareTo(BigDecimal.ZERO) != 0 ? BigDecimal.valueOf(totalCountByTimer).divide(workTimeByTimer, 4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        //单勾平均油耗
        BigDecimal avgUseFillByTimeBySingleHook = workTimeBySingleHook.compareTo(BigDecimal.ZERO) != 0 ? BigDecimal.valueOf(fillCountBySingleHook).divide(workTimeBySingleHook, 0, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
        //单勾单位成本
        //Long unitCostBySingleHook =
        //炮锤平均油耗
        BigDecimal avgUseFillByTimeByGunHammer = workTimeByGunHammer.compareTo(BigDecimal.ZERO) != 0 ? BigDecimal.valueOf(fillCountByGunHammer).divide(workTimeByGunHammer, 0, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
        //炮锤单位成本
        //Long unitCostByGunHammer = 0L;
        //包方平均油耗
        BigDecimal avgUseFillByTimeByCubic = workTimeByCubic.compareTo(BigDecimal.ZERO) != 0 ? BigDecimal.valueOf(fillCountByCubic).divide(workTimeByCubic, 0, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
        //包方平均装车数
        BigDecimal avgCarsByTimeByCubic = workTimeByCubic.compareTo(BigDecimal.ZERO) != 0 ? BigDecimal.valueOf(totalCountByCubic).divide(workTimeByCubic, 4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        //包方毛利润 (除油)  分/车
        Long grossProfitByCubic = totalCountByCubic != 0 ? (totalAmountByCubic - amountByFillByCubic) / totalCountByCubic : 0L;
        //总单位成本
        Long unitCostByTotal = (totalCubicByTimer + totalCubicByCubic) != 0 ? totalAmount / (totalCubicByTimer + totalCubicByCubic) : 0L;
        //总油耗比
        BigDecimal oilConsumption = totalAmount != 0 ? new BigDecimal((float)amountByFillByTotal / totalAmount).setScale(4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        diggingCostAccounting.setOilConsumption(oilConsumption);
        diggingCostAccounting.setProjectId(projectId);
        diggingCostAccounting.setTotalCountByTimer(totalCountByTimer);
        diggingCostAccounting.setTotalCubicByTimer(totalCubicByTimer);
        diggingCostAccounting.setWorkTimeByTimer(workTimeByTimer);
        diggingCostAccounting.setAmountByTimer(amountByTimer);
        diggingCostAccounting.setFillCountByTimer(fillCountByTimer);
        diggingCostAccounting.setAmountByFillByTimer(amountByFillByTimer);
        diggingCostAccounting.setTotalAmountByTimer(totalAmountByTimer);
        diggingCostAccounting.setWorkTimeBySingleHook(workTimeBySingleHook);
        diggingCostAccounting.setAmountBySingleHook(amountBySingleHook);
        diggingCostAccounting.setFillCountBySingleHook(fillCountBySingleHook);
        diggingCostAccounting.setAmountByFillBySingleHook(amountByFillBySingleHook);
        diggingCostAccounting.setTotalAmountBySingleHook(totalAmountBySingleHook);
        diggingCostAccounting.setWorkTimeByGunHammer(workTimeByGunHammer);
        diggingCostAccounting.setAmountByGunHammer(amountByGunHammer);
        diggingCostAccounting.setFillCountByGunHammer(fillCountByGunHammer);
        diggingCostAccounting.setAmountByFillByGunHammer(amountByFillByGunHammer);
        diggingCostAccounting.setTotalAmountByGunHammer(totalAmountByGunHammer);
        diggingCostAccounting.setWorkTimeByCubic(workTimeByCubic);
        diggingCostAccounting.setTotalCountByCubic(totalCountByCubic);
        diggingCostAccounting.setTotalCubicByCubic(totalCubicByCubic);
        diggingCostAccounting.setFillCountByCubic(fillCountByCubic);
        diggingCostAccounting.setAmountByFillByCubic(amountByFillByCubic);
        diggingCostAccounting.setTotalAmountByCubic(totalAmountByCubic);
        diggingCostAccounting.setAvgUseFillByTimeByTimer(avgUseFillByTimeByTimer.longValue());
        diggingCostAccounting.setAvgCarsByTimeByTimer(avgCarsByTimeByTimer);
        diggingCostAccounting.setAvgUseFillByTimeBySingleHook(avgUseFillByTimeBySingleHook.longValue());
        diggingCostAccounting.setAvgUseFillByTimeByGunHammer(avgUseFillByTimeByGunHammer.longValue());
        diggingCostAccounting.setAvgUseFillByTimeByCubic(avgUseFillByTimeByCubic.longValue());
        diggingCostAccounting.setAvgCarsByTimeByCubic(avgCarsByTimeByCubic);
        diggingCostAccounting.setGrossProfitByCubic(grossProfitByCubic);
        diggingCostAccounting.setTotalAmount(totalAmount);
        diggingCostAccounting.setUnitCostByTotal(unitCostByTotal);
        diggingCostAccounting.setFillCountByTotal(fillCountByTotal);
        diggingCostAccounting.setAmountByFillByTotal(amountByFillByTotal);
        Map carTempMap = projectCarCostAccountingServiceI.getAllByProjectIdAndReportDate(projectId, startTime, endTime);
        ProjectCarCostAccounting carCostAccounting = new ProjectCarCostAccounting();
        //渣车总车数
        Long totalCountByCar = Long.parseLong(carTempMap.get("totalCount").toString());
        //渣车总方量
        Long totalCubicByCar = Long.parseLong(carTempMap.get("totalCubic").toString());
        //渣车总用油
        Long fillCountByCar = Long.parseLong(carTempMap.get("fillCount").toString());
        //渣车总用油金额
        Long amountByFillByCar = Long.parseLong(carTempMap.get("amountByFill").toString());
        //渣车总排渣金额
        Long amountByCar = Long.parseLong(carTempMap.get("amount").toString());
        //油耗比
        BigDecimal oilConsumptionByCar = amountByCar != 0 ? new BigDecimal((float)amountByFillByCar / amountByCar).setScale(4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        //单位成本
        Long costBySingleHookByCar = totalCubicByCar != 0 ? amountByCar / totalCubicByCar : 0L;
        //毛利润
        Long grossProfitByCubicByCar = totalCountByCar != 0 ? (amountByCar - amountByFillByCar) / totalCountByCar : 0L;
        //平均用油
        Long avgUseFillByCar = totalCountByCar != 0 ? fillCountByCar / totalCountByCar : 0L;
        carCostAccounting.setProjectId(projectId);
        carCostAccounting.setTotalCount(totalCountByCar);
        carCostAccounting.setTotalCubic(totalCubicByCar);
        carCostAccounting.setFillCount(fillCountByCar);
        carCostAccounting.setAmountByFill(amountByFillByCar);
        carCostAccounting.setAmount(amountByCar);
        carCostAccounting.setOilConsumption(oilConsumptionByCar);
        carCostAccounting.setCostBySingleHook(costBySingleHookByCar);
        carCostAccounting.setGrossProfitByCubic(grossProfitByCubicByCar);
        carCostAccounting.setAvgUseFillByCar(avgUseFillByCar);
        Map map = new HashMap();
        map.put("digging", diggingCostAccounting);
        map.put("car", carCostAccounting);
        return Result.ok(map);
    }
}
