package com.seater.smartmining.controller;
import com.alibaba.fastjson.JSON;
import com.seater.helpers.DateEditor;
import com.seater.helpers.PropertyEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.*;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/28 0028 18:24
 */
@RestController
@RequestMapping("/api/projectdiggingmonthreport")
public class  ProjectDiggingMonthReportController {

    @Autowired
    private ProjectDiggingMonthReportServiceI projectDiggingMonthReportServiceI;
    @Autowired
    private ExcelReportService excelReportService;
    @Autowired
    private ProjectDiggingDayReportServiceI projectDiggingDayReportServiceI;
    @Autowired
    private ProjectDigginggMonthReportTotalServiceI projectDigginggMonthReportTotalServiceI;
    @Autowired
    private DeductionDiggingByMonthServiceI deductionDiggingByMonthServiceI;
    @Autowired
    private ProjectDiggingDayReportTotalServiceI projectDiggingDayReportTotalServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    /**
     * 挖机月报表统计
     * @param reportDate 统计日期 yyyy-MM
     * @return
     */
    @RequestMapping("/report")
    @Transactional
    public Result monthReportForMaterial(HttpServletRequest request, @RequestParam Date reportDate){
        //获取到项目编号
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        try {
            /*Project project = projectServiceI.get(projectId);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reportDate);
            calendar.set(Calendar.MONTH, project.getReportDay());
            reportDate = calendar.getTime();*/
            ScheduleService.scheduleDiggingMonthReport(projectId, reportDate, null);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, @RequestParam Date reportDate){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Map<String, Object> map = new HashMap<>();
        try {
            Date startTime = DateUtils.getStartDate(reportDate);
            Date endTime = DateUtils.getEndDate(reportDate);
            //生成查询日期
            reportDate = DateUtils.getEndDate(reportDate);
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            //获取月报表合计信息
            List<ProjectDiggingMonthReportTotal> projectDiggingMonthReportTotals = projectDigginggMonthReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            ProjectDiggingMonthReportTotal total = null;
            List<ProjectDiggingMonthReport> projectDiggingMonthReportList = null;
            if (projectDiggingMonthReportTotals.size() > 0) {
                total = projectDiggingMonthReportTotals.get(0);
                projectDiggingMonthReportList = projectDiggingMonthReportServiceI.getByTotalId(total.getId());
            }
            map.put("total", total);
            map.put("detail", projectDiggingMonthReportList);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(map);
    }

    @RequestMapping("/download")
    public Result downloadMonthReport(HttpServletRequest request, HttpServletResponse response, @RequestParam Date reportDate){
        try {
            //获取到项目编号
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            reportDate = DateUtils.getEndDate(reportDate);
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            List<ProjectDiggingMonthReportTotal> totalList = projectDigginggMonthReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            if (totalList.size() > 0) {
                ProjectDiggingMonthReportTotal total = totalList.get(0);
                List<ProjectDiggingMonthReport> reportList = projectDiggingMonthReportServiceI.getByTotalId(total.getId());
                //获取到生成的excel报表的路径
                String path = excelReportService.createDiggingMonthReport(request, total, reportList, reportDate);
                excelReportService.downLoadFile(response, request, path, reportDate);
                FileUtils.delFile(path);
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/save")
    public Result save(HttpServletRequest request ,@RequestBody List<ProjectDiggingMonthReport> monthReport){
        try {
            //项目编号
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            //获取到统计时间
            ProjectDiggingMonthReport report = projectDiggingMonthReportServiceI.get(monthReport.get(0).getId());
            Date reportDate = report.getReportDate();
            //获取所有的月报表扣除金额和补贴信息
            List<DeductionDiggingByMonth> monthList = deductionDiggingByMonthServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
            //生成扣除和补贴金额的索引
            Map<Long, Integer> deductionIndexMap = new HashMap<>();
            int i = 0;
            for(DeductionDiggingByMonth month : monthList){
                deductionIndexMap.put(month.getMachineId(), i);
                i++;
            }
            //获取所有的月报表信息
            List<ProjectDiggingMonthReport> reportList = projectDiggingMonthReportServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            //获取月报表合计信息
            List<ProjectDiggingMonthReportTotal> reportTotalList = projectDigginggMonthReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            ProjectDiggingMonthReportTotal total = reportTotalList.size() > 0 ? reportTotalList.get(0) : null;
            //生成月报表信息的索引
            Map<Long, Integer> reportIndexMap = new HashMap<>();
            i = 0;
            for(ProjectDiggingMonthReport projectDiggingMonthReport : reportList){
                reportIndexMap.put(projectDiggingMonthReport.getMachineId(), i);
                i ++;
            }
            List<DeductionDiggingByMonth> saveDeductionList = new ArrayList<>();
            for(ProjectDiggingMonthReport projectDiggingMonthReport : monthReport){
                //获取索引
                Long machineId = projectDiggingMonthReport.getMachineId();
                Integer index = deductionIndexMap.get(machineId) != null ? deductionIndexMap.get(machineId) : -1;
                DeductionDiggingByMonth deductionDiggingByMonth = null;
                Integer reportIndex = reportIndexMap.get(machineId);
                ProjectDiggingMonthReport diggingMonthReport = reportList.get(reportIndex);
                if(index != -1 && monthList.get(index) != null){
                    deductionDiggingByMonth = monthList.get(index);
                }else{
                    deductionDiggingByMonth = new DeductionDiggingByMonth();
                    deductionDiggingByMonth.setMachineId(diggingMonthReport.getMachineId());
                    deductionDiggingByMonth.setMachineCode(diggingMonthReport.getMachineCode());
                    deductionDiggingByMonth.setAmountByTotal(diggingMonthReport.getWorkTotalAmount());
                    deductionDiggingByMonth.setProjectId(projectId);
                    deductionDiggingByMonth.setMonthReportId(diggingMonthReport.getId());
                    deductionDiggingByMonth.setReportDate(reportDate);
                    deductionDiggingByMonth.setAmountByTotalByTotal(total.getWorkTotalAmount());
                }
                diggingMonthReport.setDeduction(projectDiggingMonthReport.getDeduction());
                diggingMonthReport.setSubsidyAmount(projectDiggingMonthReport.getSubsidyAmount());
                diggingMonthReport.setWorkTotalAmount(deductionDiggingByMonth.getAmountByTotal());
                diggingMonthReport.setSettlementAmount(diggingMonthReport.getWorkTotalAmount() + projectDiggingMonthReport.getSubsidyAmount());
                diggingMonthReport.setShouldPayAmount(diggingMonthReport.getSettlementAmount() - projectDiggingMonthReport.getDeduction());
                total.setDeduction(total.getDeduction() + projectDiggingMonthReport.getDeduction());
                total.setSubsidyAmount(total.getSubsidyAmount() + projectDiggingMonthReport.getSubsidyAmount());
                total.setSettlementAmount(total.getSettlementAmount() + projectDiggingMonthReport.getSubsidyAmount());
                total.setShouldPayAmount(total.getSettlementAmount() - projectDiggingMonthReport.getDeduction());
                deductionDiggingByMonth.setAmountByDeduction(projectDiggingMonthReport.getDeduction());
                deductionDiggingByMonth.setAmountBySubsidyAmount(projectDiggingMonthReport.getSubsidyAmount());
                saveDeductionList.add(deductionDiggingByMonth);
            }
            deductionDiggingByMonthServiceI.saveAll(saveDeductionList);
            projectDiggingMonthReportServiceI.saveAll(reportList);
            projectDigginggMonthReportTotalServiceI.save(total);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    public static void main(String[] args){
        ProjectDiggingMonthReport projectDiggingMonthReport = new ProjectDiggingMonthReport();
        projectDiggingMonthReport.setId(1583L);
        projectDiggingMonthReport.setTotalId(291L);
        projectDiggingMonthReport.setDeduction(2000L);
        projectDiggingMonthReport.setSubsidyAmount(5000L);
        List<ProjectDiggingMonthReport> reportList = new ArrayList<>();
        reportList.add(projectDiggingMonthReport);
        String json = JSON.toJSONString(reportList);
        System.out.println(json);
    }
}
