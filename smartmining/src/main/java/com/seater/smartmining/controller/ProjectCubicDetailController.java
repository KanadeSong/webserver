package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.ProjectCubicDetailElseServiceI;
import com.seater.smartmining.service.ProjectCubicDetailServiceI;
import com.seater.smartmining.service.ProjectCubicDetailTotalServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/4 0004 16:22
 */
@RestController
@RequestMapping("/api/projectcubicdetail")
public class ProjectCubicDetailController {

    @Autowired
    private ProjectCubicDetailTotalServiceI projectCubicDetailTotalServiceI;

    @Autowired
    private ProjectCubicDetailServiceI projectCubicDetailServiceI;

    @Autowired
    private ProjectCubicDetailElseServiceI projectCubicDetailElseServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/report")
    @Transactional
    public Result report(HttpServletRequest request, Long machineId, Date reportDate){
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ScheduleService.scheduleCubicDetail(projectId, machineId, reportDate);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Long machineId, Date reportDate){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        reportDate = DateUtils.getEndDate(reportDate);
        reportDate = DateUtils.createReportDateByMonth(reportDate);
        List<ProjectCubicDetailTotal> totalList = projectCubicDetailTotalServiceI.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
        Map<String,Object> result = new HashMap<>();
        ProjectCubicDetailTotal total = null;
        List<CubicDetailByIntegration> integrationList = new ArrayList<>();
        if(totalList.size() > 0){
            total = totalList.get(0);
            List<Map> mapList = projectCubicDetailServiceI.getReportDateByProjectIdAndCarIdAndTotalId(projectId, machineId, total.getId());
            if(mapList.size() > 0){
                for(int i =0;i<mapList.size();i++){
                    Date date = DateUtils.stringFormatDate(mapList.get(i).get("report_date").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                    date = DateUtils.createReportDateByMonth(date);
                    List<ProjectCubicDetail> detailList = projectCubicDetailServiceI.getAllByProjectIdAndTotalId(projectId, total.getId(), date);
                    List<ProjectCubicDetailElse> elseList = projectCubicDetailElseServiceI.getAllByProjectIdAndTotalId(projectId, total.getId(), date);
                    CubicDetailByIntegration integration = new CubicDetailByIntegration();
                    integration.setReportDate(date);
                    integration.setProjectId(projectId);
                    integration.setMachineId(machineId);
                    List<CubicDetail> cubicDetailList = new ArrayList<>();
                    List<CubicDetailElse> cubicDetailElseList = new ArrayList<>();
                    for(ProjectCubicDetail detail : detailList){
                        CubicDetail cubicDetail = new CubicDetail();
                        cubicDetail.setCarId(detail.getCarId());
                        cubicDetail.setCarCode(detail.getCarCode());
                        cubicDetail.setAmountByShould(detail.getAmountByShould());
                        cubicDetail.setCars(detail.getCars());
                        cubicDetail.setCapacity(detail.getCapacity());
                        cubicDetail.setCubics(detail.getCubics());
                        cubicDetail.setMaterialId(detail.getMaterialId());
                        cubicDetail.setMaterialName(detail.getMaterialName());
                        cubicDetailList.add(cubicDetail);
                    }
                    integration.setDetailList(cubicDetailList);
                    for(ProjectCubicDetailElse detailElse : elseList){
                        CubicDetailElse cubicDetailElse = new CubicDetailElse();
                        cubicDetailElse.setAmount(detailElse.getAmount());
                        cubicDetailElse.setAmountByOil(detailElse.getAmountByOil());
                        cubicDetailElse.setCarsByTemp(detailElse.getCarsByTemp());
                        cubicDetailElse.setCarsByTotal(detailElse.getCarsByTotal());
                        cubicDetailElse.setOilCount(detailElse.getOilCount());
                        cubicDetailElse.setPrice(detailElse.getPrice());
                        cubicDetailElse.setCubicByTemp(detailElse.getCubicByTemp());
                        cubicDetailElse.setCubicByTotal(detailElse.getCubicByTotal());
                        cubicDetailElseList.add(cubicDetailElse);
                    }
                    integration.setDetailElseList(cubicDetailElseList);
                    integrationList.add(integration);
                }
            }

        }
        result.put("total", total);
        result.put("detail", integrationList);
        return Result.ok(result);
    }
}
