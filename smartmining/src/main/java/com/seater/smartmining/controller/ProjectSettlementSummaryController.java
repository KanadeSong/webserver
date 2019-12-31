package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/7 0007 16:35
 */
@RestController
@RequestMapping("/api/projectsettlementsummary")
public class ProjectSettlementSummaryController {

    @Autowired
    private ProjectSettlementSummaryServiceI projectSettlementSummaryServiceI;
    @Autowired
    private DeductionBySettlementSummaryServiceI deductionBySettlementSummaryServiceI;
    @Autowired
    private ProjectSettlementTotalServiceI projectSettlementTotalServiceI;
    @Autowired
    private ProjectMonthReportServiceI projectMonthReportServiceI;
    @Autowired
    private ProjectMonthReportTotalServiceI projectMonthReportTotalServiceI;

    /*@RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectSettlementSummary summary){
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            summary.setProjectId(projectId);
            projectSettlementSummaryServiceI.save(summary);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }*/

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, @RequestBody List<ProjectSettlementSummary> summaryList) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Long totalId = summaryList.get(0).getTotalId();
            Long carId = summaryList.get(0).getCarId();
            Date reportDate = summaryList.get(0).getReportDate();
            //月报表对象
            //ProjectMonthReport projectMonthReport = projectMonthReportServiceI.getAllByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
            //月报表合计对象
            //ProjectMonthReportTotal projectMonthReportTotal = projectMonthReportTotalServiceI.get(projectMonthReport.getTotalId());
            ProjectSettlementTotal total = projectSettlementTotalServiceI.get(totalId);
            total.setSubsidyAmount(0L);
            total.setAmountByMeals(0L);
            total.setAmountByElse(0L);
            total.setRent(0L);
            total.setBalance(0L);
            total.setCarsCount(0L);
            total.setAmountByElse(0L);
            for (ProjectSettlementSummary summary : summaryList) {
                DeductionBySettlementSummary deduction = deductionBySettlementSummaryServiceI.getAllByProjectIdAndCarIdAndReportDate(projectId, summary.getCarId(), summary.getReportDate());
                total.setRent(summary.getRent() + total.getRent());
                total.setAmountByElse(summary.getAmountByElse() + total.getAmountByElse());
                total.setAmountByMeals(summary.getAmountByMeals() + total.getAmountByMeals());
                total.setSubsidyAmount(summary.getSubsidyAmount() + total.getSubsidyAmount());
                total.setAmountByElse(summary.getAmountByElse() + total.getAmountByElse());
                if (deduction != null) {
                    summary.setBalance(deduction.getShouldPay() + (summary.getAmountByElse() * 3500L) + summary.getSubsidyAmount() - summary.getRent() - summary.getAmountByMeals() - summary.getAmountByElse());
                    summary.setCarsCount(deduction.getCarsCount() + summary.getCarsCount());
                } else {
                    deduction = new DeductionBySettlementSummary();
                    deduction.setCarId(summary.getCarId());
                    deduction.setAmountByMeal(summary.getAmountByMeals());
                    deduction.setThiryFive(summary.getAmountByElse());
                    deduction.setAmountBySubsidyAmount(summary.getSubsidyAmount());
                    deduction.setProjectId(projectId);
                    deduction.setRent(summary.getRent());
                    deduction.setAmountByElse(summary.getAmountByElse());
                    deduction.setShouldPay(summary.getBalance());
                    deduction.setReportDate(summary.getReportDate());
                    deduction.setThiryFive(summary.getAmountByElse());
                    deduction.setCarsCount(summary.getCarsCount());
                    deduction.setCreateDate(summary.getCreateDate());
                    summary.setBalance(deduction.getShouldPay() + (summary.getAmountByElse() * 3500L) + deduction.getAmountBySubsidyAmount() - summary.getRent() - summary.getAmountByMeals() - summary.getAmountByElse());
                }
                //月报表对应的补贴
                /*projectMonthReport.setSubsidyAmount(projectMonthReport.getSubsidyAmount() + summary.getSubsidyAmount());
                //月报表对应的扣款
                projectMonthReport.setDeduction(projectMonthReport.getDeduction() + summary.getRent() + summary.getAmountByElse() + summary.getAmountByMeals());
                //月报表对应的应付金额
                projectMonthReport.setShouldPayAmount(projectMonthReport.getShouldPayAmount() + summary.getSubsidyAmount() - (summary.getRent() + summary.getAmountByElse() + summary.getAmountByMeals()));*/
                total.setCarsCount(summary.getCarsCount() + total.getCarsCount());
                total.setBalance(summary.getBalance() + total.getBalance());
                projectSettlementSummaryServiceI.save(summary);
                deductionBySettlementSummaryServiceI.save(deduction);
            }
            /*projectMonthReportTotal.setSubsidyAmount(projectMonthReportTotal.getSubsidyAmount() + projectMonthReport.getSubsidyAmount());
            projectMonthReportTotal.setDeduction(projectMonthReportTotal.getDeduction() + projectMonthReport.getDeduction());
            projectMonthReportTotal.setShouldPayAmount(projectMonthReportTotal.getShouldPayAmount() + projectMonthReportTotal.getSubsidyAmount() - projectMonthReportTotal.getDeduction());*/
            projectSettlementTotalServiceI.save(total);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    public static void main(String[] args) {
        List<ProjectSettlementSummary> summaryList = new ArrayList<>();
        ProjectSettlementSummary summary = new ProjectSettlementSummary();
        summary.setId(3411L);
        summary.setCarsCount(14L);
        summary.setBalance(7691600L);
        summary.setRent(5000L);
        summary.setSubsidyAmount(6000L);
        summary.setAmountByMeals(3000L);
        summary.setAmountByElse(5L);
        summary.setProjectId(1L);
        summary.setCreateDate(DateUtils.stringFormatDate("2019-03-31 00:00:00", SmartminingConstant.DATEFORMAT));
        summary.setReportDate(DateUtils.stringFormatDate("2019-03-31 00:00:00", SmartminingConstant.DATEFORMAT));
        summary.setPrice(650L);
        summary.setAmountByOil(0L);
        summary.setOilCount(0L);
        summary.setTotalId(519L);
        summary.setDetailId(4658L);
        summaryList.add(summary);
        String request = JSON.toJSONString(summaryList);
        System.out.println("请求的json为：" + request);
    }
}
