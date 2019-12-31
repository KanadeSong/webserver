package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.api.AutoApiUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/12 0012 16:43
 */
@RestController
@RequestMapping("/api/projectdiggingdayreport")
public class ProjectDiggingDayReportController {

    @Autowired
    private ProjectDiggingDayReportServiceI projectDiggingDayReportServiceI;
    @Autowired
    private ProjectDiggingDayReportTotalServiceI projectDiggingDayReportTotalServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private DeductionDiggingServiceI deductionDiggingServiceI;
    @Autowired
    private ExcelReportService excelReportService;
    @Autowired
    private ProjectDiggingDayReportHistoryServiceI projectDiggingDayReportHistoryServiceI;
    @Autowired
    private ProjectHourPriceServiceI projectHourPriceServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    private ProjectDiggingMachineMaterialServiceI projectDiggingMachineMaterialServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/save")
    public Result save(ProjectDiggingDayReport report){
        try {
            projectDiggingDayReportServiceI.setDeductionTimeByDayAndDeductionTimeByNightOrderById(report.getId(), report.getDeductionTimeByDay(), report.getDeductionTimeByNight());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/report")
    @Transactional
    public Result report(HttpServletRequest request, @RequestParam Date reportDate){
        //获取到项目编号
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        try {
            ScheduleService.scheduleDiggingReport(projectId, reportDate, null);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }


    @RequestMapping("/query")
    public Result query(HttpServletRequest request ,@RequestParam Date reportDate){
        //获取到项目编号
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<ProjectDiggingDayReportTotal> dayReportTotalList = projectDiggingDayReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
        ProjectDiggingDayReportTotal total = null;
        List<ProjectDiggingDayReport> projectDiggingDayReports = null;
        if(dayReportTotalList.size() > 0 ){
            total = dayReportTotalList.get(0);
            projectDiggingDayReports = projectDiggingDayReportServiceI.getByTotalId(total.getId());
        }
        ProjectDiggingDayReportHistory history = projectDiggingDayReportHistoryServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
        Map<String,Object> map = new HashMap<>();
        map.put("total", total);
        map.put("detail",projectDiggingDayReports);
        map.put("history", history);
        return Result.ok(map);
    }

    /**
     * 扣除时间
     * @param request
     * @param id
     * @param subtotalTimerByNight  扣除时间
     * @param shift  1 - 白班   2 - 晚班
     * @return
     */
    @Transactional
    @RequestMapping("/deduction")
    public Result save(HttpServletRequest request, Long id, Long subtotalTimerByNight, Integer shift){
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            //获取到当前报表对象
            ProjectDiggingDayReport report = projectDiggingDayReportServiceI.get(id);
            //获取到当前报表合计对象
            ProjectDiggingDayReportTotal total = projectDiggingDayReportTotalServiceI.get(report.getTotalId());
            //扣除时间对象
            DeductionDigging deductionDigging = deductionDiggingServiceI.getAllByProjectIdAndMachineIdAndReportDate(projectId, report.getMachineId(), report.getReportDate());
            if(deductionDigging == null){
                deductionDigging = new DeductionDigging();
                //白班计时小计
                deductionDigging.setTimeByDay(report.getSubtotalTimerByDay());
                //晚班计时小计
                deductionDigging.setTimeByNight(report.getSubtotalTimerByNight());
                //计时总工时
                deductionDigging.setTotalTimeByTimer(report.getTotalTimeByTimer());
                //总工时
                deductionDigging.setTotalTime(report.getTotalWorkTimer());
                //合计计时总工时
                deductionDigging.setTotalTimeByTimerTotal(total.getTotalTimeByTimer());
                //合计总工时
                deductionDigging.setTotalTimeTotal(total.getTotalWorkTimer());
                //累计计时总工时
                deductionDigging.setGrandTotalTimeByTimer(report.getGrandTimeByTimer());
                //累计总工时
                deductionDigging.setGrandTotalTime(report.getTotalWorkTimer());
                //累计合计计时总工时
                deductionDigging.setGrandTotalTimeByTimerTotal(total.getGrandTimeByTimer());
                //累计合计总工时
                deductionDigging.setGrandTotalTimeTotal(total.getCountTimer());
                //计时总金额
                deductionDigging.setAmountByTimer(report.getAmountByTimer());
                //总金额
                deductionDigging.setTotalAmount(report.getTotalAmount());
                //累计计时总金额
                deductionDigging.setTotalAmountByTimer(report.getGrandAmountByTimer());
                //累计总金额
                deductionDigging.setGrandTotalAmount(report.getGrandWorkAmount());
                //合计计时总金额
                deductionDigging.setTotalAmountByTotal(total.getAmountByTimer());
                //合计总金额
                deductionDigging.setTotalAmountByTotal(total.getTotalAmount());
                //累计合计计时总金额
                deductionDigging.setGrandAmountByTimer(total.getGrandAmountByTimer());
                //累计合计总金额
                deductionDigging.setGrandTotalAmount(total.getGrandWorkAmount());
            }
            //扣除金额
            Long decAmount = new Double(((double)subtotalTimerByNight / 3600L) * report.getPriceByTimer()).longValue();
            if(shift == 1){
                report.setDeductionTimeByDay(new BigDecimal((float)subtotalTimerByNight / 3600L).setScale(2, BigDecimal.ROUND_HALF_UP));
                deductionDigging.setShifts(ShiftsEnums.DAYSHIFT);
                report.setSubtotalTimerByDay(deductionDigging.getTimeByDay().subtract(report.getDeductionTimeByDay()));
            }else{
                report.setDeductionTimeByNight(new BigDecimal((float)subtotalTimerByNight / 3600L).setScale(2, BigDecimal.ROUND_HALF_UP));
                deductionDigging.setShifts(ShiftsEnums.BLACKSHIFT);
                report.setSubtotalTimerByNight(deductionDigging.getTimeByNight().subtract(report.getDeductionTimeByNight()));
            }
            //计时工时
            report.setTotalTimeByTimer(deductionDigging.getTotalTimeByTimer().subtract(report.getDeductionTimeByDay()));
            //总工时
            report.setTotalWorkTimer(deductionDigging.getTotalTime().subtract(report.getDeductionTimeByDay()));
            //累计计时工时
            report.setGrandTimeByTimer(deductionDigging.getGrandTotalTimeByTimer().subtract(report.getDeductionTimeByDay()));
            //累计总工时
            report.setCountTimer(deductionDigging.getGrandTotalTime().subtract(report.getDeductionTimeByDay()));
            //合计计时工时
            total.setTotalTimeByTimer(deductionDigging.getTotalTimeByTimerTotal().subtract(report.getDeductionTimeByDay()));
            //合计总工时
            total.setTotalTimeByTimer(deductionDigging.getTotalTimeByTimerTotal().subtract(report.getDeductionTimeByDay()));
            //合计累计计时工时
            total.setGrandTimeByTimer(deductionDigging.getGrandTotalTimeByTimerTotal().subtract(report.getDeductionTimeByDay()));
            //合计累计总工时
            total.setCountTimer(deductionDigging.getGrandTotalTime().subtract(report.getDeductionTimeByDay()));
            //计时总金额
            report.setAmountByTimer(deductionDigging.getAmountByTimer() - decAmount);
            //总金额
            report.setTotalAmount(deductionDigging.getTotalAmount() - decAmount);
            //累计计时总金额
            report.setGrandAmountByTimer(deductionDigging.getGrandAmountByTimer() - decAmount);
            //累计总金额
            report.setGrandWorkAmount(deductionDigging.getGrandTotalAmount() - decAmount);
            //合计计时金额
            total.setAmountByTimer(deductionDigging.getTotalAmountByTimer() - decAmount);
            //合计总金额
            total.setTotalAmount(deductionDigging.getTotalAmount() - decAmount);
            //累计合计计时金额
            total.setGrandAmountByTimer(deductionDigging.getGrandTotalAmountByTimer()- decAmount);
            //累计合计总金额
            total.setGrandWorkAmount(deductionDigging.getGrandTotalAmountByTotal() - decAmount);
            deductionDigging.setMachineId(report.getMachineId());
            deductionDigging.setMachineCode(report.getMachineCode());
            deductionDigging.setDeductionTime(new BigDecimal((float)subtotalTimerByNight / 3600L).setScale(2, BigDecimal.ROUND_HALF_UP));
            deductionDigging.setProjectId(projectId);
            deductionDigging.setReportDate(report.getReportDate());
            deductionDiggingServiceI.save(deductionDigging);
            projectDiggingDayReportServiceI.save(report);
            projectDiggingDayReportTotalServiceI.save(total);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/download")
    public void downLoad(HttpServletRequest request, HttpServletResponse response, Date reportDate){
        try{
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            List<ProjectDiggingDayReportTotal> dayReportTotalList = projectDiggingDayReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            if(dayReportTotalList.size() > 0 ){
                ProjectDiggingDayReportTotal total = dayReportTotalList.get(0);
                List<ProjectDiggingDayReport> projectDiggingDayReports = projectDiggingDayReportServiceI.getByTotalId(total.getId());
                String path = excelReportService.createMachineDayReport(request, total, projectDiggingDayReports,reportDate);
                excelReportService.downLoadFile(response, request, path, reportDate);
                FileUtils.delFile(path);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @RequestMapping("/tempReportByDay")
    public Result tempReportByDay(HttpServletRequest request, @RequestParam Date startDate, @RequestParam Date endDate) throws IOException {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        startDate = DateUtils.createReportDateByMonth(startDate);
        endDate = DateUtils.getEndDateByNow(endDate);
        //获取到扣除时间的总集合
        List<DeductionDigging> deductionDiggings = deductionDiggingServiceI.getAllByProjectIdAndTime(projectId, startDate, endDate);
        //创建白班扣除时间索引
        Map<Long, BigDecimal> deductionIndexByDay = new HashMap<>();
        //创建晚班扣除时间索引
        Map<Long, BigDecimal> deductionIndexByNight = new HashMap<>();
        for (DeductionDigging digging : deductionDiggings) {
            if (digging.getShifts().getAlias() == 1) {
                deductionIndexByDay.put(digging.getMachineId(), digging.getDeductionTime());
            } else {
                deductionIndexByNight.put(digging.getMachineId(), digging.getDeductionTime());
            }
        }
        //获取到所有挖机指定时间的工作时间
        List<Map> workTimeList = projectWorkTimeByDiggingServiceI.getAllByProjectIdAndTime(projectId, startDate, endDate);
        Map<Long, Integer> machineListIndex = new HashMap<>();
        for(int i = 0; i < workTimeList.size(); i++){
            Long machineId = Long.parseLong(workTimeList.get(i).get("material_id").toString());
            machineListIndex.put(machineId, i);
        }
        //获取指定时间内所有挖机的总加油信息
        List<Map> fillInfoList = projectCarFillLogServiceI.getDiggingTotalFillByProjectIdAndTimeGroupByCar(projectId, startDate, endDate);
        //创建加油索引
        Map<String, Integer> fillIndexMap = new HashMap();
        for (int i = 0; i < fillInfoList.size(); i++) {
            Integer pricingType = Integer.valueOf(fillInfoList.get(i).get("pricing_type_enums").toString());
            Long machineId = Long.parseLong(fillInfoList.get(i).get("car_id").toString());
            Integer index = machineListIndex.get(machineId);
            if(index == null && machineId != 0) {
                Map map = new HashMap();
                map.put("material_id", machineId);
                workTimeList.add(map);
            }
            fillIndexMap.put(machineId + "fill" + pricingType, i);
        }
        //根据时间查询挖机对应的计时工作信息
        List<Map> countListByHour = projectCarWorkInfoServiceI.getDiggingDayCountListByProjectIdAndTime(projectId, startDate, endDate, 1);
        //创建计时挖机工作信息索引
        Map<Long, Integer> countIndexByHour = new HashMap<>();
        for (int i = 0; i < countListByHour.size(); i++) {
            Long id = countListByHour.get(i).get("digging_machine_id") != null ? Long.parseLong(countListByHour.get(i).get("digging_machine_id").toString()) : 0L;
            countIndexByHour.put(id, i);
        }
        //根据时间查询挖机对应的计方工作信息
        List<Map> countListByCubic = projectCarWorkInfoServiceI.getDiggingDayCountListByProjectIdAndTime(projectId, startDate, endDate, 2);
        //创建计方挖机工作信息索引
        Map<Long, Integer> countIndexByCubic = new HashMap<>();
        for (int i = 0; i < countListByCubic.size(); i++) {
            Long machineId = countListByCubic.get(i).get("digging_machine_id") != null ? Long.parseLong(countListByCubic.get(i).get("digging_machine_id").toString()) : 0L;
            countIndexByCubic.put(machineId, i);
        }
        //获取所有挖机包方详情
        List<Map> detailList = projectCarWorkInfoServiceI.getMaterialDetailByProjectIdAndMachineIdAndTime(projectId, startDate, endDate);
        //创建包方详情索引
        Map<String, Integer> detailIndexCubic = new HashMap<>();
        for (int i = 0; i < detailList.size(); i++) {
            Long machineId = Long.parseLong(detailList.get(i).get("digging_machine_id").toString());
            Long materialId = Long.parseLong(detailList.get(i).get("material_id").toString());
            detailIndexCubic.put(machineId.toString() + materialId, i);
        }
        //挖机当天工作信息
        List<Map> diggingInfoList = projectWorkTimeByDiggingServiceI.getDiggingInfoByProjectId(projectId, startDate, endDate);
        Map<String, Integer> diggingWorkIndex = new HashMap<>();
        for (int i = 0; i < diggingInfoList.size(); i++) {
            Integer shifts = Integer.valueOf(diggingInfoList.get(i).get("shift").toString());
            Integer pricingType = Integer.valueOf(diggingInfoList.get(i).get("pricing_type_enums").toString());
            Long machineId = Long.parseLong(diggingInfoList.get(i).get("material_id").toString());
            diggingWorkIndex.put(machineId + "shift" + shifts + "type" + pricingType, i);
        }
        List<ProjectDiggingMachineMaterial> diggingMachineMaterialList = projectDiggingMachineMaterialServiceI.getByProjectIdOrderById(projectId);
        Map<Long, Integer> diggingMachineMaterialIndexMap = new HashMap<>();
        for(int i = 0; i < diggingMachineMaterialList.size(); i++){
            diggingMachineMaterialIndexMap.put(diggingMachineMaterialList.get(i).getMaterialId(), i);
        }
        //获取所有物料集合
        List<ProjectMaterial> materialList = projectMaterialServiceI.getByProjectIdOrderById(projectId);
        List<Map> detailTotal = new ArrayList<>();
        for (ProjectMaterial material : materialList) {
            Map map = new HashMap();
            map.put("id", material.getId());
            map.put("name", material.getName());
            map.put("count", 0L);
            map.put("amount", 0L);
            map.put("cubic", 0L);
            //detail.add(map);
            detailTotal.add(map);
        }
        ProjectDiggingTempDayReportTotal total = new ProjectDiggingTempDayReportTotal();
        List<ProjectDiggingTempDayReport> reportList = new ArrayList<>();
        for(int i = 0; i < workTimeList.size(); i++){
            Long machineId = Long.parseLong(workTimeList.get(i).get("material_id").toString());
            //挖机对象
            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
            if (machine == null)
                throw new SmartminingProjectException("挖机不存在，挖机ID：" + machineId);
            //计时金额对象
            ProjectHourPrice projectHourPrice = projectHourPriceServiceI.getByProjectIdAndBrandIdAndModelIdAndCarType(projectId, machine.getBrandId(), machine.getModelId(), 2);
            ProjectDiggingTempDayReport report = new ProjectDiggingTempDayReport();
            report.setMachineCode(machine.getCode());
            report.setMachineId(machineId);
            report.setPriceByTimer(new BigDecimal((float) projectHourPrice.getPrice() / 100L).setScale(2, BigDecimal.ROUND_HALF_DOWN));
            //计时工作信息索引 方量+车数
            Integer workInfoIndexByTimer = countIndexByHour.get(machineId);
            if(workInfoIndexByTimer != null){
                Integer count = Integer.valueOf(countListByHour.get(workInfoIndexByTimer).get("count").toString());
                Long cubic = Long.parseLong(countListByHour.get(workInfoIndexByTimer).get("cubic").toString());
                report.setCarCountByTimer(count);
                report.setCubicByTimer(new BigDecimal((float) cubic / 1000000L).setScale(2, BigDecimal.ROUND_HALF_DOWN));
            }
            //计时早班工作时间
            String earlyKeyByTimer = machineId + "shift" + Shift.Early.getAlias() + "type" + PricingTypeEnums.Hour.getValue();
            Integer earlyTimeIndexByTimer = diggingWorkIndex.get(earlyKeyByTimer);
            if(earlyTimeIndexByTimer != null) {
                Long earlyTimeByTimer = Long.parseLong(diggingInfoList.get(earlyTimeIndexByTimer).get("workTime").toString());
                BigDecimal time = new BigDecimal((float) earlyTimeByTimer / 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                report.setEarlyWorkTimeByTimer(time);
            }
            //计时晚班工作时间
            String nightKeyByTimer = machineId + "shift" + Shift.Night.getAlias() + "type" + PricingTypeEnums.Hour.getValue();
            Integer nightTimeIndexByTimer = diggingWorkIndex.get(nightKeyByTimer);
            if(nightTimeIndexByTimer != null){
                Long nightTimeByTimer = Long.parseLong(diggingInfoList.get(nightTimeIndexByTimer).get("workTime").toString());
                BigDecimal time = new BigDecimal((float) nightTimeByTimer / 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                report.setNightWorkTimeByTimer(time);
            }
            report.setWorkTimeByTimer(report.getWorkTimeByTimer().add(report.getWorkTimeByCubic()));
            //计时金额
            BigDecimal amountByTimer = report.getWorkTimeByTimer().multiply(new BigDecimal((float)projectHourPrice.getPrice() / 100).setScale(2, BigDecimal.ROUND_HALF_DOWN)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            report.setAmountByTimer(amountByTimer);
            //计时加油金额
            Integer fillIndexByTimer = fillIndexMap.get(machineId + "fill" + PricingTypeEnums.Hour.getValue());
            //加油金额
            Long fillAmountByTimer = fillIndexByTimer != null ? Long.parseLong(fillInfoList.get(fillIndexByTimer).get("totalAmount").toString()) : 0L;
            Long fillCountByTimer = fillIndexByTimer != null ? Long.parseLong(fillInfoList.get(fillIndexByTimer).get("totalFill").toString()) : 0L;
            //计方加油金额
            Integer fillIndexByCubic = fillIndexMap.get(machineId + "fill" + PricingTypeEnums.Cube.getValue());
            Long fillAmountByCubic = fillIndexByCubic != null ? Long.parseLong(fillInfoList.get(fillIndexByCubic).get("totalAmount").toString()) : 0L;
            Long fillCountByCubic = fillIndexByCubic != null ? Long.parseLong(fillInfoList.get(fillIndexByCubic).get("totalFill").toString()) : 0L;
            BigDecimal fillAmount = new BigDecimal((float)(fillAmountByTimer + fillAmountByCubic) / 100).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal fillCount = new BigDecimal((float)(fillCountByTimer + fillCountByCubic) / 1000).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            report.setFillCount(fillCount);
            report.setAmountByFill(fillAmount);
            //计方工作信息索引 方量+车数
            Integer workInfoIndexByCubic = countIndexByCubic.get(machineId);
            if(workInfoIndexByCubic != null){
                Integer count = Integer.valueOf(countListByCubic.get(workInfoIndexByCubic).get("count").toString());
                Long cubic = Long.parseLong(countListByCubic.get(workInfoIndexByCubic).get("cubic").toString());
                report.setCarCountByCubic(count);
                report.setCubicByCubic(new BigDecimal((float) cubic / 1000000L).setScale(2, BigDecimal.ROUND_HALF_DOWN));
            }
            //计方早班工作时间
            String earlyKeyByCubic = machineId + "shift" + Shift.Early.getAlias() + "type" + PricingTypeEnums.Cube.getValue();
            Integer earlyTimeIndexByCubic = diggingWorkIndex.get(earlyKeyByCubic);
            if(earlyTimeIndexByCubic != null){
                Long earlyTimeByCubic = Long.parseLong(diggingInfoList.get(earlyTimeIndexByCubic).get("workTime").toString());
                BigDecimal time = new BigDecimal((float) earlyTimeByCubic/ 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                report.setEarlyWorkTimeByCubic(time);
            }
            //计方晚班工作时间
            String nightKeyByCubic = machineId + "shift" + Shift.Night.getAlias() + "type" + PricingTypeEnums.Cube.getValue();
            Integer nightTimeIndexByCubic = diggingWorkIndex.get(nightKeyByCubic);
            if(nightTimeIndexByCubic != null){
                Long nightTimeByCubic = Long.parseLong(diggingInfoList.get(nightTimeIndexByCubic).get("workTime").toString());
                BigDecimal time = new BigDecimal((float) nightTimeByCubic / 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                report.setNightWorkTimeByCubic(time);
            }
            report.setWorkTimeByCubic(report.getEarlyWorkTimeByCubic().add(report.getNightWorkTimeByCubic()));
            report.setWorkTime(report.getWorkTimeByTimer().add(report.getWorkTimeByCubic()));

            List<Map> detail = new ArrayList<>();
            for(int j = 0; j < materialList.size(); j++){
                Long materialId = Long.parseLong(materialList.get(j).getId().toString());
                String materialName = materialList.get(j).getName().toString();
                String key = machineId.toString() + materialId;
                Integer detailIndex = detailIndexCubic.get(key);
                Map totalMap = detailTotal.get(j);
                Integer countAll = Integer.valueOf(totalMap.get("count").toString());
                Long cubicAll = Long.parseLong(totalMap.get("cubic").toString());
                BigDecimal amountAll = new BigDecimal(totalMap.get("amount").toString());
                Integer count = 0;
                Long cubic = 0L;
                BigDecimal amount = BigDecimal.ZERO;
                Map map = new HashMap();
                map.put("id", materialId);
                map.put("name", materialName);
                totalMap.put("id", materialId);
                totalMap.put("name", materialName);
                if(detailIndex != null){
                    count = Integer.valueOf(detailList.get(detailIndex).get("count").toString());
                    cubic = Long.parseLong(detailList.get(detailIndex).get("cubic").toString());
                    Integer machineMaterialIndex = diggingMachineMaterialIndexMap.get(materialId);
                    if(machineMaterialIndex != null){
                        Long price = diggingMachineMaterialList.get(machineMaterialIndex).getPrice();
                        amount = new BigDecimal((float) cubic / 1000000L).multiply(new BigDecimal((float) price / 100L)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                        report.setAmountByCubic(report.getAmountByCubic().add(amount));
                    }
                }
                map.put("count", count);
                map.put("cubic", cubic);
                map.put("amount", amount);
                totalMap.put("count", countAll + count);
                totalMap.put("cubic", cubicAll + cubic);
                totalMap.put("amount", amount.add(amountAll));
                detail.add(map);
            }
            report.setAmount(report.getAmountByTimer().add(report.getAmountByCubic()));
            report.setDetailJson(JSON.toJSONString(detail));
            report.setShouldPayAmount(report.getAmount().subtract(report.getAmountByFill()));
            BigDecimal avgFillCount = report.getWorkTime().compareTo(BigDecimal.ZERO) != 0 ? report.getFillCount().divide(report.getWorkTime(), 2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            report.setAvgFillCount(avgFillCount);
            BigDecimal avgCarsCount = report.getWorkTime().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(report.getCarCountByTimer() + report.getCarCountByCubic()).divide(report.getWorkTime(), 2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            report.setAvgCarsCount(avgCarsCount);
            BigDecimal avgAmount = (report.getCarCountByTimer() + report.getCarCountByCubic()) != 0 ? report.getShouldPayAmount().divide(new BigDecimal(report.getCarCountByTimer() + report.getCarCountByCubic()), 2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            report.setAvgAmount(avgAmount);
            BigDecimal oilConsumption = report.getAmount().compareTo(BigDecimal.ZERO) != 0 ? report.getAmountByFill().divide(report.getAmount(), 4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            report.setOilConsumption(oilConsumption);
            reportList.add(report);
            total.setAmount(total.getAmount().add(report.getAmount()));
            total.setAmountByCubic(total.getAmountByCubic().add(report.getAmountByCubic()));
            total.setAmountByTimer(total.getAmountByTimer().add(report.getAmountByTimer()));
            total.setAmountByFill(total.getAmountByFill().add(report.getAmountByFill()));
            total.setCarCountByCubic(total.getCarCountByCubic() + report.getCarCountByCubic());
            total.setCarCountByTimer(total.getCarCountByTimer() + report.getCarCountByTimer());
            total.setFillCount(total.getFillCount().add(report.getFillCount()));
            total.setWorkTime(total.getWorkTime().add(report.getWorkTime()));
            total.setWorkTimeByTimer(total.getWorkTimeByTimer().add(report.getWorkTimeByTimer()));
            total.setWorkTimeByCubic(total.getWorkTimeByCubic().add(report.getWorkTimeByCubic()));
            total.setCarCountByTimer(total.getCarCountByTimer() + report.getCarCountByTimer());
            total.setCarCountByCubic(total.getCarCountByCubic() + report.getCarCountByCubic());
            total.setCubicByTimer(total.getCubicByTimer().add(report.getCubicByTimer()));
            total.setCubicByCubic(total.getAmountByCubic().add(report.getCubicByCubic()));
            total.setShouldPayAmount(total.getShouldPayAmount().add(report.getShouldPayAmount()));
            BigDecimal avgFillCountTotal = total.getWorkTime().compareTo(BigDecimal.ZERO) != 0 ? total.getFillCount().divide(total.getWorkTime(), 2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            total.setAvgFillCount(avgFillCountTotal);
            BigDecimal avgCarsCountTotal = total.getWorkTime().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(total.getCarCountByTimer() + total.getCarCountByCubic()).divide(total.getWorkTime(), 2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            total.setAvgCarsCount(avgCarsCountTotal);
            BigDecimal avgAmountTotal = (total.getCarCountByTimer() + total.getCarCountByCubic()) != 0 ? total.getShouldPayAmount().divide(new BigDecimal(total.getCarCountByTimer() + total.getCarCountByCubic()), 2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            total.setAvgAmount(avgAmountTotal);
            BigDecimal oilConsumptionTotal = total.getAmount().compareTo(BigDecimal.ZERO) != 0 ? total.getAmountByFill().divide(total.getAmount(), 4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            total.setOilConsumption(oilConsumptionTotal);
        }
        total.setDetailJson(JSON.toJSONString(detailTotal));
        Map map = new HashMap();
        map.put("total", total);
        map.put("datail", reportList);
        return Result.ok(map);
    }
}
