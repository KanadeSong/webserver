package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectMonthReport;
import com.seater.smartmining.entity.ProjectMonthReportTotal;
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
 * @Date 2019/2/19 0019 11:01
 */
@RestController
@RequestMapping("/api/projectmonthreport")
public class ProjectMonthReportController {

    @Autowired
    private ProjectMonthReportServiceI projectMonthReportServiceI;
    @Autowired
    private ProjectMonthReportTotalServiceI projectMonthReportTotalServiceI;
    @Autowired
    private ExcelReportService excelReportService;
    @Autowired
    private ProjectServiceI projectServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/report")
    @Transactional
    public Result report(HttpServletRequest request, Date reportDate) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            /*Project project = projectServiceI.get(projectId);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reportDate);
            calendar.set(Calendar.MONTH, project.getReportDay());
            reportDate = calendar.getTime();*/
            ScheduleService.scheduleCarMonthReport(projectId, reportDate, null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Date reportDate) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        reportDate = DateUtils.getEndDate(reportDate);
        reportDate = DateUtils.createReportDateByMonth(reportDate);
        List<ProjectMonthReportTotal> totalList = projectMonthReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
        Map<String, Object> response = new HashMap<>();
        ProjectMonthReportTotal total = null;
        List<ProjectMonthReport> monthReportList = null;
        if (totalList.size() > 0) {
            total = totalList.get(0);
            monthReportList = projectMonthReportServiceI.getByTotalId(total.getId());
        }
        response.put("detail", monthReportList);
        response.put("total", total);
        return Result.ok(response);
    }

    @RequestMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam Date reportDate) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            reportDate = DateUtils.getEndDate(reportDate);
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            List<ProjectMonthReportTotal> totalList = projectMonthReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            if (totalList.size() > 0) {
                ProjectMonthReportTotal total = totalList.get(0);
                List<ProjectMonthReport> reportList = projectMonthReportServiceI.getByTotalId(total.getId());
                String path = excelReportService.createCarMonthReport(request, total, reportList, reportDate);
                excelReportService.downLoadFile(response, request, path, reportDate);
                FileUtils.delFile(path);
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/save")
    public Result save(@RequestBody List<ProjectMonthReport> monthReport){
        try {
            Long deductionT = 0L;
            Long subsidyAmountT = 0L;
            Long totalId = monthReport.get(0).getTotalId();
            for(ProjectMonthReport report : monthReport) {
                projectMonthReportServiceI.setDeductionAndSubsidyAmount(report.getId(), report.getDeduction(), report.getSubsidyAmount());
                deductionT = deductionT + report.getDeduction();
                subsidyAmountT = subsidyAmountT + report.getDeduction();
            }
            projectMonthReportTotalServiceI.setDeductionAndSubsidyAmount(totalId, deductionT, subsidyAmountT);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }
}
