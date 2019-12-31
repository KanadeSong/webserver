package com.seater.smartmining.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.ProjectSettlementDetailServiceI;
import com.seater.smartmining.service.ProjectSettlementSummaryServiceI;
import com.seater.smartmining.service.ProjectSettlementTotalServiceI;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 14:22
 */
@RestController
@RequestMapping("/api/projectsettlementdetail")
public class ProjectSettlementDetailController {

    @Autowired
    private ProjectSettlementDetailServiceI projectSettlementDetailServiceI;
    @Autowired
    private ProjectSettlementTotalServiceI projectSettlementTotalServiceI;
    @Autowired
    private ProjectSettlementSummaryServiceI projectSettlementSummaryServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/report")
    @Transactional
    public Result report(HttpServletRequest request, Date reportDate, Long carId) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        try {
            ScheduleService.settlementDetailByCar(projectId, carId, reportDate);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    /**
     * 发布渣车结算表
     *
     * @param id 合计表主键id    @ProjectSettlementTotal.class
     * @return 操作结果
     * @throws IOException
     */
    @Transactional
    @PostMapping("/isPublish")
    public Result isPublish(@RequestParam(required = true) Long id, @RequestParam(required = true) Boolean publishWx) throws IOException {
        ProjectSettlementTotal total = projectSettlementTotalServiceI.get(id);
        total.setPublishWx(publishWx);
        projectSettlementTotalServiceI.save(total);
        return Result.ok("操作成功");
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Date reportDate, Long carId) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        reportDate = DateUtils.getEndDate(reportDate);
        reportDate = DateUtils.createReportDateByMonth(reportDate);
        Map<String, Object> map = new HashMap<>();
        List<ProjectSettlementTotal> totalList = projectSettlementTotalServiceI.getByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
        ProjectSettlementTotal total = null;
        List<ProjectSettlementDetailByIntegration> integrations = new ArrayList<>();
        if (totalList.size() > 0) {
            total = totalList.get(0);
            List<Map> mapList = projectSettlementDetailServiceI.getReportDateByProjectIdAndCarIdAndTotalId(projectId, carId, total.getId());
            if (mapList.size() > 0) {
                for (int i = 0; i < mapList.size(); i++) {
                    Date date = DateUtils.stringFormatDate(mapList.get(i).get("report_date").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                    date = DateUtils.createReportDateByMonth(date);
                    List<ProjectSettlementDetail> detailList = projectSettlementDetailServiceI.getByProjectIdAndTotalId(projectId, total.getId(), date);
                    List<ProjectSettlementSummary> summaryList = projectSettlementSummaryServiceI.getByProjectIdAndTotalId(projectId, total.getId(), date);
                    ProjectSettlementDetailByIntegration integration = new ProjectSettlementDetailByIntegration();
                    integration.setProjectId(detailList.get(0).getProjectId());
                    integration.setCarId(detailList.get(0).getCarId());
                    integration.setTotalId(detailList.get(0).getTotalId());

                    integration.setReportDate(detailList.get(0).getReportDate());
                    List<SettlementDetail> list = new ArrayList<>();
                    List<SettlementSummary> summaries = new ArrayList<>();
                    for (ProjectSettlementDetail detail : detailList) {
                        SettlementDetail settlementDetail = new SettlementDetail();
                        settlementDetail.setDetailId(detail.getId());
                        settlementDetail.setDistance(detail.getDistance());
                        settlementDetail.setAmount(detail.getAmount());
                        settlementDetail.setCarsCount(detail.getCarsCount());
                        settlementDetail.setCubicCount(detail.getCubicCount());
                        settlementDetail.setMaterialId(detail.getMaterialId());
                        settlementDetail.setMaterialName(detail.getMaterialName());
                        settlementDetail.setPrice(detail.getPrice());
                        list.add(settlementDetail);
                    }
                    integration.setDetailList(list);
                    for (ProjectSettlementSummary summary : summaryList) {
                        SettlementSummary settlementSummary = new SettlementSummary();
                        settlementSummary.setSummaryId(summary.getId());
                        settlementSummary.setAmountByElse(summary.getAmountByElse());
                        settlementSummary.setAmountByMeals(summary.getAmountByMeals());
                        settlementSummary.setOilCount(summary.getOilCount());
                        settlementSummary.setAmountByOil(summary.getAmountByOil());
                        settlementSummary.setBalance(summary.getBalance());
                        settlementSummary.setCarsCount(summary.getCarsCount());
                        settlementSummary.setPrice(summary.getPrice());
                        settlementSummary.setDetailId(summary.getDetailId());
                        settlementSummary.setSubsidyAmount(summary.getSubsidyAmount());
                        settlementSummary.setRent(summary.getRent());
                        summaries.add(settlementSummary);
                    }
                    integration.setSummaryList(summaries);
                    integrations.add(integration);
                }
            }
        }
        map.put("total", total);
        map.put("detail", integrations);
        return Result.ok(map);
    }
    
    
    /*@RequestMapping("/save")
    public Result save(HttpServletRequest request, Long detailId, Long summaryId, Long thirtyFive, Long rent, Long amountByMeal, Long subsidyAmount){
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            if(thirtyFive != null && thirtyFive != 0L) {
                ProjectSettlementDetail detail = projectSettlementDetailServiceI.get(detailId);
                detail.setProjectId(projectId);
                detail.setThirtyFive(thirtyFive);
                projectSettlementDetailServiceI.save(detail);
            }
            ProjectSettlementSummary summary = projectSettlementSummaryServiceI.get(summaryId);
            summary.setProjectId(projectId);
            summary.setAmountByElse(rent);
            summary.setAmountByMeals(amountByMeal);
            summary.setSubsidyAmount(subsidyAmount);
            projectSettlementSummaryServiceI.save(summary);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }*/
}
