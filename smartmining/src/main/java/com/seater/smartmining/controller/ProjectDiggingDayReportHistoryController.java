package com.seater.smartmining.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/11 0011 16:37
 */
@RestController
@RequestMapping("/api/projectdiggingdayreporthistory")
public class ProjectDiggingDayReportHistoryController {

    @Autowired
    private ProjectDiggingDayReportHistoryServiceI projectDiggingDayReportHistoryServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectDiggingMachineMaterialServiceI projectDiggingMachineMaterialServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectHourPriceServiceI projectHourPriceServiceI;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    /*@RequestMapping("/report")
    public Result report(HttpServletRequest request, Date reportDate) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            reportDate = DateUtils.getEndDateByNow(reportDate);
            projectDiggingDayReportHistoryServiceI.deleteByProjectIdAndReportDate(projectId, reportDate);
            ProjectDiggingDayReportHistory history = new ProjectDiggingDayReportHistory();
            history.setProjectId(projectId);
            //查询所有物料的单价
            List<ProjectDiggingMachineMaterial> machineMaterialList = projectDiggingMachineMaterialServiceI.getByProjectIdOrderById(projectId);
            //生成物料索引
            Map<Long, Integer> materialIndex = new HashMap<>();
            for (int i = 0; i < machineMaterialList.size(); i++) {
                materialIndex.put(machineMaterialList.get(i).getMaterialId(), i);
            }
            //查询所有挖机的信息
            List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(projectId);
            //生成挖机索引
            Map<String, Integer> machineIndex = new HashMap<>();
            for (int i = 0; i < projectDiggingMachineList.size(); i++) {
                machineIndex.put(projectDiggingMachineList.get(i).getCode(), i);
            }
            //查询所有挖机对应的包时金额详情
            List<ProjectHourPrice> projectHourPriceList = projectHourPriceServiceI.getAllByProjectId(projectId);
            //生成包时金额详情索引
            Map<String, Integer> hourIndex = new HashMap<>();
            for (int i = 0; i < projectHourPriceList.size(); i++) {
                hourIndex.put(projectHourPriceList.get(i).getBrandId().toString() + projectHourPriceList.get(i).getModelId().toString() + projectHourPriceList.get(i).getCarType().getValue(), i);
            }
            //获取所有挖机的工作信息
            List<Map> workInfoList = projectCarWorkInfoServiceI.getTotalCubicAndCountByProjectIdAndDateIdentification(projectId, reportDate);
            //查询所有挖机的工作信息
            List<Map> workTimeList = projectWorkTimeByDiggingServiceI.getTotalTimeAndPricingTypeByProjectIdAndDate(projectId, reportDate);
            //生成工作信息索引
            Map<String, Integer> workTimeIndex = new HashMap<>();
            for (int i = 0; i < workTimeList.size(); i++) {
                String machineCode = workTimeList.get(i).get("material_code").toString();
                Integer pricingType = Integer.valueOf(workTimeList.get(i).get("pricing_type_enums").toString());
                workTimeIndex.put(machineCode + pricingType, i);
            }
            for (int i = 0; i < workInfoList.size(); i++) {
                //挖机编号
                String machineCode = workInfoList.get(i).get("digging_machine_code").toString();
                if (StringUtils.isEmpty(machineCode))
                    continue;
                //计价方式
                Integer pricingType = Integer.valueOf(workInfoList.get(i).get("pricing_type").toString());
                //车数
                Integer count = Integer.valueOf(workInfoList.get(i).get("count").toString());
                //方量
                Long cubic = Long.parseLong(workInfoList.get(i).get("cubic").toString());
                String keyOne = machineCode + pricingType;
                //工作总时间
                Long workTime = 0L;
                Integer timeIndex = workTimeIndex.get(keyOne);
                if (timeIndex != null)
                    workTime = Long.parseLong(workTimeList.get(timeIndex).get("workTime").toString());
                if (pricingType == PricingTypeEnums.Cube.getValue()) {
                    //物料编号
                    Long materialId = Long.parseLong(workInfoList.get(i).get("material_id").toString());
                    //计方车数
                    history.setTotalCountByCubic(history.getTotalCountByCubic() + count);
                    //计方方量
                    history.setTotalCubicByCubic(history.getTotalCubicByCubic() + cubic);
                    Integer index = materialIndex.get(materialId);
                    Long price = 0L;
                    if (index != null)
                        price = machineMaterialList.get(index).getPrice();
                    //计方总金额
                    history.setTotalAmountByCubic(history.getTotalAmount() + cubic * price);
                    //计方总时长
                    history.setTotalTimeByCubic(history.getTotalTimeByCubic() + workTime);
                } else if (pricingType == PricingTypeEnums.Hour.getValue()) {
                    //计时车数
                    history.setTotalCountByTimer(history.getTotalCountByTimer() + count);
                    //计时方量
                    history.setTotalCubicByTimer(history.getTotalCubicByTimer() + cubic);
                    //计时总时长
                    history.setTotalTimeByTimer(history.getTotalTimeByTimer() + workTime);
                    Integer index = machineIndex.get(machineCode);
                    if (index != null) {
                        String keyTwo = projectDiggingMachineList.get(index).getBrandId().toString() + projectDiggingMachineList.get(index).getModelId().toString() + CarType.DiggingMachine.getValue();
                        Integer priceIndex = hourIndex.get(keyTwo);
                        if (priceIndex != null) {
                            //当前挖机计时单价
                            Long price = projectHourPriceList.get(priceIndex).getPrice();
                            //计时总金额
                            history.setTotalAmountByTimer(history.getTotalAmountByTimer() + price * workTime);
                        }
                    }
                }
            }
            //总金额
            history.setTotalAmount(history.getTotalAmountByTimer() + history.getTotalAmountByCubic());
            //累计总方量
            history.setTotalTime(history.getTotalTimeByTimer() + history.getTotalTimeByCubic());
            //累计总车数
            history.setTotalCount(history.getTotalCountByTimer() + history.getTotalCountByCubic());
            //总方量
            history.setTotalCubic(history.getTotalCubicByTimer() + history.getTotalCubicByCubic());
            //加油信息
            Map fillInfoMap = projectCarFillLogServiceI.getAllByProjectIdAndDateAndCarType(projectId, reportDate);
            //总加油量
            Long totalFill = Long.parseLong(fillInfoMap.get("volumn").toString());
            //总加油金额
            Long fillAmount = Long.parseLong(fillInfoMap.get("amount").toString());
            history.setTotalFill(totalFill);
            history.setTotalFillAmount(fillAmount);
            history.setShouldAmount(history.getTotalAmount() - history.getTotalFillAmount());
            //车/小时
            BigDecimal avgCars = new BigDecimal(0);
            Long avgCubic = 0L;
            if (history.getTotalTime() / 3600L != 0) {
                avgCars = new BigDecimal((float) history.getTotalCount() / ((float) history.getTotalTime() / 3600L));
                avgCubic = history.getTotalCubic() / (history.getTotalTime() / 3600L);
            }
            history.setAvgCars(avgCars);
            history.setAvgCubic(avgCubic);
            //平均金额 含油
            Long avgAmountByFill = 0L;
            //平均金额 不含油
            Long avgAmount = 0L;
            if (history.getTotalCount() != 0) {
                avgAmountByFill = history.getTotalAmount() / history.getTotalCount();
                avgAmount = history.getShouldAmount() / history.getTotalCount();
            }
            history.setAvgAmountByFill(avgAmountByFill);
            history.setAvgAmount(avgAmount);
            //油耗比
            BigDecimal oilConsumption = new BigDecimal(0);
            if (history.getTotalAmount() != 0)
                oilConsumption = new BigDecimal((float) history.getTotalFillAmount() / history.getTotalAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
            history.setOilConsumption(oilConsumption);
            history.setCreateDate(new Date());
            history.setReportDate(reportDate);
            projectDiggingDayReportHistoryServiceI.save(history);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }*/

    /*@RequestMapping("/query")
    public Result query(HttpServletRequest request, Date reportDate) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        ProjectDiggingDayReportHistory history = projectDiggingDayReportHistoryServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
        return Result.ok(history);
    }*/
}
