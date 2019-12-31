package com.seater.smartmining.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.ProjectDayReportHistory;
import com.seater.smartmining.service.ProjectCarFillLogServiceI;
import com.seater.smartmining.service.ProjectCarServiceI;
import com.seater.smartmining.service.ProjectCarWorkInfoServiceI;
import com.seater.smartmining.service.ProjectDayReportHistoryServiceI;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/11 0011 14:15
 */
@RestController
@RequestMapping("/api/projectdayreporthistory")
public class ProjectDayReportHistoryController {

    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDayReportHistoryServiceI projectDayReportHistoryServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    /*@RequestMapping("/report")
    public Result report(HttpServletRequest request, @RequestParam Date reportDate){
        try{
            reportDate = DateUtils.getEndDateByNow(reportDate);
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            projectDayReportHistoryServiceI.deleteByProjectIdAndReportDate(projectId, reportDate);
            Map workInfoMap = projectCarWorkInfoServiceI.getHistoryInfoByTime(projectId, reportDate);
            //总数量
            Integer count = Integer.valueOf(workInfoMap.get("count").toString());
            //总方量
            Long cubic = Long.parseLong(workInfoMap.get("cubic").toString());
            //总金额
            Long amount = Long.parseLong(workInfoMap.get("amount").toString());
            //总里程
            Long distance = Long.parseLong(workInfoMap.get("distance").toString());
            Map fillInfoMap = projectCarFillLogServiceI.getAllByProjectIdAndDate(projectId, reportDate);
            //加油量
            Long volumn = Long.parseLong(fillInfoMap.get("volumn").toString());
            //加油金额
            Long fillAmount = Long.parseLong(fillInfoMap.get("amount").toString());
            //运输成本 分/立方
            Long cost = cubic != null && cubic != 0 ? amount / (cubic / 1000000L) : 0L;
            //油耗
            BigDecimal oilConsumption = new BigDecimal(0);
            if(amount != null && amount != 0){
                oilConsumption = new BigDecimal((float)fillAmount / amount).setScale(4, BigDecimal.ROUND_HALF_UP);
            }
            //应付金额
            Long shouldAmount = amount - fillAmount;
            //平均用油 毫升/车
            Long avgOil = count != null && count != 0 ? volumn / count : 0L;
            //毛利 分/车
            Long grossProfit = count != null && count != 0 ? shouldAmount / count : 0L;
            //注册总车数
            Integer totalCount = projectCarServiceI.getCountByProjectId(projectId);
            //总天数
            List<Map> daysList = projectCarWorkInfoServiceI.countByProjectIdAndDateIdentification(projectId, reportDate);
            Integer days = daysList.size();
            //平均次数(趟/天)
            BigDecimal avgCarByTime = new BigDecimal(0);
            if(days != null && days != 0 && totalCount != null && totalCount != 0)
                avgCarByTime = new BigDecimal(count / days / totalCount).setScale(2, BigDecimal.ROUND_HALF_UP);
            Integer countByMaterial = projectCarWorkInfoServiceI.countByProjectIdAndDateIdentificationAndMaterialId(projectId, reportDate, 2L);
            Long avgDistance = count != null && count != 0 ? distance / count : 0L;
            ProjectDayReportHistory history = new ProjectDayReportHistory();
            history.setProjectId(projectId);
            history.setCost(cost);
            history.setFinishCubic(cubic);
            history.setTotalAmount(amount);
            history.setFinishCars(count);
            history.setFillCount(volumn);
            history.setFillAmount(fillAmount);
            history.setShouldPay(shouldAmount);
            history.setAvgDistance(avgDistance);
            history.setAvgCarByTime(avgCarByTime);
            history.setAvgOil(avgOil);
            history.setCarsCountByCoal(countByMaterial);
            history.setGrossProfit(grossProfit);
            history.setOilConsumption(oilConsumption);
            history.setDistance(distance);
            history.setCreateTime(new Date());
            history.setReportDate(reportDate);
            projectDayReportHistoryServiceI.save(history);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }*/

    /*@RequestMapping("/query")
    public Result query(HttpServletRequest request, Date reportDate){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        ProjectDayReportHistory history = projectDayReportHistoryServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
        return Result.ok(history);
    }*/
}
