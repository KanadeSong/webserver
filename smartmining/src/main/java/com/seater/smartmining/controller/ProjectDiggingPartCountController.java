package com.seater.smartmining.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.entity.ProjectDiggingPartCountAmount;
import com.seater.smartmining.entity.ProjectDiggingPartCountGrand;
import com.seater.smartmining.entity.ProjectDiggingPartCountTotal;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.ProjectDiggingPartCountAmountServiceI;
import com.seater.smartmining.service.ProjectDiggingPartCountGrandServiceI;
import com.seater.smartmining.service.ProjectDiggingPartCountServiceI;
import com.seater.smartmining.service.ProjectDiggingPartCountTotalServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.metadata.ListenableMetadataStore;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/1 0001 14:27
 */
@RestController
@RequestMapping("/api/projectdiggingpartcount")
public class ProjectDiggingPartCountController {

    @Autowired
    private ProjectDiggingPartCountServiceI projectDiggingPartCountServiceI;
    @Autowired
    private ProjectDiggingPartCountTotalServiceI projectDiggingPartCountTotalServiceI;
    @Autowired
    private ProjectDiggingPartCountGrandServiceI projectDiggingPartCountGrandServiceI;
    @Autowired
    private ProjectDiggingPartCountAmountServiceI projectDiggingPartCountAmountServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping(value = "/save", produces = "application/json")
    @Transactional
    public Result save(HttpServletRequest request, @RequestBody List<ProjectDiggingPartCount> projectDiggingPartCountList) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ProjectDiggingPartCountTotal total = projectDiggingPartCountTotalServiceI.get(projectDiggingPartCountList.get(0).getTotalId());
            total.setBalance(0L);
            total.setSubsidyAmount(0L);
            total.setAmountByMeals(0L);
            total.setRent(0L);
            ProjectDiggingPartCountGrand grand = projectDiggingPartCountGrandServiceI.getAllByProjectIdAndTotalId(projectId, total.getId());
            for (ProjectDiggingPartCount count : projectDiggingPartCountList) {
                ProjectDiggingPartCountAmount countAmount = projectDiggingPartCountAmountServiceI.getAllByProjectIdAndCountId(projectId, count.getId());
                if (countAmount == null) {
                    countAmount = new ProjectDiggingPartCountAmount();
                    countAmount.setCountId(count.getId());
                    countAmount.setProjectId(projectId);
                    countAmount.setAmountByMails(count.getAmountByMeals());     //伙食费
                    countAmount.setRend(count.getRent());           //房租费
                    countAmount.setSubsidyAmount(count.getSubsidyAmount());             //补贴
                    countAmount.setBalance(count.getBalance());         //余额
                    countAmount.setBalanceTotal(total.getBalance());
                    countAmount.setBalanceGrand(grand.getBalance());
                    countAmount.setGrandAmountByMails(grand.getAmountByMeals());
                    countAmount.setGrandRent(grand.getRent());
                    countAmount.setGrandSubsidyAmount(grand.getSubsidyAmount());
                } else {
                    count.setBalance(countAmount.getBalance());
                    total.setBalance(countAmount.getBalanceTotal());
                    grand.setBalance(countAmount.getBalanceGrand());
                }
                Long balance = count.getBalance() - count.getAmountByMeals() - count.getRent() + count.getSubsidyAmount();
                count.setBalance(balance);
                total.setRent(total.getRent() + count.getRent());
                total.setAmountByMeals(total.getAmountByMeals() + count.getAmountByMeals());
                total.setSubsidyAmount(total.getSubsidyAmount() + count.getSubsidyAmount());
                total.setBalance(total.getBalance() - count.getAmountByMeals() - count.getRent() + count.getSubsidyAmount());

                grand.setRent(countAmount.getGrandRent() + count.getRent());
                grand.setAmountByMeals(countAmount.getGrandAmountByMails() + count.getAmountByMeals());
                grand.setSubsidyAmount(countAmount.getGrandSubsidyAmount() + count.getSubsidyAmount());
                grand.setBalance(grand.getBalance() - count.getAmountByMeals() - count.getRent() + count.getSubsidyAmount());
                projectDiggingPartCountServiceI.save(count);
                projectDiggingPartCountAmountServiceI.save(countAmount);
            }
            projectDiggingPartCountTotalServiceI.save(total);
            projectDiggingPartCountGrandServiceI.save(grand);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @RequestMapping("/report")
    @Transactional
    public Result report(HttpServletRequest request, Date reportDate, Long machineId) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        try {
            ScheduleService.schedulePartCountByDigging(projectId, reportDate, machineId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }


    /**
     * 发布渣车结算表
     *
     * @param id 合计表主键id    @ProjectDiggingPartCountTotal.class
     * @return 操作结果
     * @throws IOException
     */
    @Transactional
    @PostMapping("/isPublish")
    public Result isPublish(@RequestParam(required = true) Long id, @RequestParam(required = true) Boolean publishWx) throws IOException {
        ProjectDiggingPartCountTotal total = projectDiggingPartCountTotalServiceI.get(id);
        total.setPublishWx(publishWx);
        projectDiggingPartCountTotalServiceI.save(total);
        return Result.ok("操作成功");
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Date reportDate, Long machineId) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        reportDate = DateUtils.getEndDate(reportDate);
        reportDate = DateUtils.createReportDateByMonth(reportDate);
        List<ProjectDiggingPartCountTotal> totalList = projectDiggingPartCountTotalServiceI.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
        List<ProjectDiggingPartCountGrand> grandList = projectDiggingPartCountGrandServiceI.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
        List<ProjectDiggingPartCount> countList = null;
        if (totalList.size() > 0) {
            countList = projectDiggingPartCountServiceI.getByProjectIdAndTotalIdAndMachineId(projectId, totalList.get(0).getId(), machineId);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", totalList);
        result.put("grand", grandList);
        result.put("count", countList);
        return Result.ok(result);
    }
}
