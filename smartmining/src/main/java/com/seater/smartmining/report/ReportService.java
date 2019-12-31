package com.seater.smartmining.report;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.enums.StatisticsTypeEnums;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/21 0021 16:52
 */
@Service
public class ReportService {

    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private ProjectDiggingCostAccountingServiceI projectDiggingCostAccountingServiceI;
    @Autowired
    private ProjectDiggingDayReportServiceI projectDiggingDayReportServiceI;
    @Autowired
    private  ProjectCarCostAccountingServiceI projectCarCostAccountingServiceI;
    @Autowired
    private ProjectDayReportPartCarServiceI projectDayReportPartCarServiceI;
    @Autowired
    private ProjectCostAccountingCountServiceI projectCostAccountingCountServiceI;
    @Autowired
    private ProjectAppStatisticsByCarServiceI projectAppStatisticsByCarServiceI;
    @Autowired
    private ProjectAppStatisticsByMachineServiceI projectAppStatisticsByMachineServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     *  获取到成本分析数据
     * @param projectId 项目编号
     * @param reportDate 统计日期
     * @param diggingInfoMap 挖机数据集合
     * @param carInfoMap     渣车数据集合
     * @param statisticsType 分析类型
     * @throws IOException
     */
    public void getCostAccountingByDigging(Long projectId, Date reportDate, Map diggingInfoMap, Map carInfoMap,StatisticsTypeEnums statisticsType) throws IOException {

        //创建成本分析当日小计对象
        ProjectDiggingCostAccounting accountingByDigging = new ProjectDiggingCostAccounting();
        accountingByDigging.setProjectId(projectId);
        accountingByDigging.setReportDate(reportDate);
        accountingByDigging.setStatisticsType(statisticsType);
        if(diggingInfoMap.get("total_count_by_timer") != null)
            accountingByDigging.setTotalCountByTimer(((BigDecimal) diggingInfoMap.get("total_count_by_timer")).longValue());
        if(diggingInfoMap.get("cubic_count_by_timer") != null)
            accountingByDigging.setTotalCubicByTimer(((BigDecimal) diggingInfoMap.get("cubic_count_by_timer")).longValue());
        if(diggingInfoMap.get("total_time_by_timer") != null)
            accountingByDigging.setWorkTimeByTimer(((BigDecimal) diggingInfoMap.get("total_time_by_timer")));
        if(diggingInfoMap.get("amount_by_timer") != null)
            accountingByDigging.setAmountByTimer(((BigDecimal) diggingInfoMap.get("amount_by_timer")).longValue());
        //List<ProjectWorkTimeByDigging> timeByDiggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndTime(projectId, earlyStart, nightEnd);
        List<Map> fillList = new ArrayList<>();
        if(statisticsType.compareTo(StatisticsTypeEnums.DAYCOUNT) == 0){
            Map<String, Date> result = workDateService.getWorkTime(projectId, reportDate);
            //获取到当天的白班上班时间和晚班下班时间
            Date earlyStart = result.get("start");
            Date nightEnd = result.get("end");
            fillList = projectCarFillLogServiceI.getDiggingTotalFillByProjectIdAndTime(projectId, earlyStart, nightEnd);
        }else if(statisticsType.compareTo(StatisticsTypeEnums.MONTHCOUNT) == 0){
            Date start = DateUtils.getStartDate(reportDate);
            Date end = DateUtils.getEndDate(reportDate);
            Map<String, Date> startResult = workDateService.getWorkTime(projectId, start);
            Map<String, Date> endResult = workDateService.getWorkTime(projectId, end);
            start = startResult.get("start");
            end = endResult.get("end");
            fillList = projectCarFillLogServiceI.getDiggingTotalFillByProjectIdAndTime(projectId, start, end);
        }else{
            fillList = projectCarFillLogServiceI.getHistoryDiggingTotalFillByProjectId(projectId);
        }
        //计时当天油量
        Long fillCountByTimerByDay = 0L;
        //计时当天加油总金额
        Long fillAmountByTimerByDay = 0L;
        //计方当天油量
        Long fillCountByCubicByDay = 0L;
        //计方当天加油总金额
        Long fillAmountByCubicByDay = 0L;
        for(int i = 0; i < fillList.size(); i++){
            Integer pricingType = fillList.get(i).get("pricing_type_enums") != null ? Integer.valueOf(fillList.get(i).get("pricing_type_enums").toString()) : 0;
            if(pricingType == PricingTypeEnums.Hour.getValue()){
                if(fillList.get(i).get("totalFill") != null)
                    fillCountByTimerByDay = fillCountByTimerByDay + ((BigDecimal) fillList.get(i).get("totalFill")).longValue();
                if(fillList.get(i).get("totalAmount") != null)
                    fillAmountByTimerByDay = fillAmountByTimerByDay + ((BigDecimal) fillList.get(i).get("totalAmount")).longValue();
            }else if(pricingType == PricingTypeEnums.Cube.getValue()){
                if(fillList.get(i).get("totalFill") != null)
                    fillCountByCubicByDay = fillCountByCubicByDay + ((BigDecimal) fillList.get(i).get("totalFill")).longValue();
                if(fillList.get(i).get("totalAmount") != null)
                    fillAmountByCubicByDay = fillAmountByCubicByDay + ((BigDecimal) fillList.get(i).get("totalAmount")).longValue();
            }
        }
        /*for (ProjectWorkTimeByDigging digging : timeByDiggingList) {
            List<Map> fillListByDay = projectCarFillLogServiceI.getDiggingTotalFillByProjectIdAndTime(projectId, digging.getStartTime(), digging.getEndTime());
            if (fillListByDay.size() > 0) {
                if (digging.getPricingTypeEnums() == PricingTypeEnums.Hour) {
                    if(fillListByDay.get(0).get("totalFill") != null)
                        fillCountByTimerByDay = fillCountByTimerByDay + ((BigDecimal) fillListByDay.get(0).get("totalFill")).longValue();
                    if(fillListByDay.get(0).get("totalAmount") != null)
                        fillAmountByTimerByDay = fillAmountByTimerByDay + ((BigDecimal) fillListByDay.get(0).get("totalAmount")).longValue();
                }else if(digging.getPricingTypeEnums() == PricingTypeEnums.Cube){
                    if(fillListByDay.get(0).get("totalFill") != null)
                        fillCountByCubicByDay = fillCountByCubicByDay + ((BigDecimal) fillListByDay.get(0).get("totalFill")).longValue();
                    if(fillListByDay.get(0).get("totalAmount") != null)
                        fillAmountByCubicByDay = fillAmountByCubicByDay + ((BigDecimal) fillListByDay.get(0).get("totalAmount")).longValue();
                }
            }
        }*/
        accountingByDigging.setFillCountByTimer(fillCountByTimerByDay);
        accountingByDigging.setAmountByFillByTimer(fillAmountByTimerByDay);
        accountingByDigging.setTotalAmountByTimer(accountingByDigging.getAmountByTimer() + fillAmountByTimerByDay);
        if(diggingInfoMap.get("total_time_by_cubic") != null)
            accountingByDigging.setWorkTimeByCubic(((BigDecimal) diggingInfoMap.get("total_time_by_cubic")));
        if(diggingInfoMap.get("car_total_count_by_cubic") != null)
            accountingByDigging.setTotalCountByCubic(((BigDecimal) diggingInfoMap.get("car_total_count_by_cubic")).longValue());
        if(diggingInfoMap.get("total_count_by_cubic") != null)
            accountingByDigging.setTotalCubicByCubic(((BigDecimal) diggingInfoMap.get("total_count_by_cubic")).longValue());
        accountingByDigging.setFillCountByCubic(fillCountByCubicByDay);
        accountingByDigging.setAmountByFillByCubic(fillAmountByCubicByDay);
        if(diggingInfoMap.get("total_amount_by_cubic") != null)
            accountingByDigging.setTotalAmountByCubic(((BigDecimal) diggingInfoMap.get("total_amount_by_cubic")).longValue());
        BigDecimal avgUseFillByTimeByTimer = new BigDecimal(0);
        if(accountingByDigging.getWorkTimeByTimer().compareTo(BigDecimal.ZERO) != 0)
            avgUseFillByTimeByTimer = new BigDecimal(accountingByDigging.getFillCountByTimer()).divide(accountingByDigging.getWorkTimeByTimer(), 2, BigDecimal.ROUND_HALF_UP);
        accountingByDigging.setAvgUseFillByTimeByTimer(avgUseFillByTimeByTimer.longValue());
        BigDecimal avgCarsByTimeByTimer = new BigDecimal(0);
        if(accountingByDigging.getWorkTimeByTimer().compareTo(BigDecimal.ZERO) != 0){
                avgCarsByTimeByTimer = new BigDecimal(accountingByDigging.getTotalCountByTimer()).divide(accountingByDigging.getWorkTimeByTimer(), 2 ,BigDecimal.ROUND_HALF_UP);
        }
        accountingByDigging.setAvgCarsByTimeByTimer(avgCarsByTimeByTimer);
        Long avgUseFillByTimeByCubic = 0L;
        if(accountingByDigging.getWorkTimeByCubic().compareTo(BigDecimal.ZERO) != 0)
            avgUseFillByTimeByCubic = new BigDecimal(accountingByDigging.getFillCountByCubic()).divide(accountingByDigging.getWorkTimeByCubic(), 2, BigDecimal.ROUND_HALF_UP).longValue();
        accountingByDigging.setAvgUseFillByTimeByCubic(avgUseFillByTimeByCubic);
        BigDecimal avgCarsByTimeByCubic = new BigDecimal(0);
        if(accountingByDigging.getWorkTimeByCubic().compareTo(BigDecimal.ZERO) != 0){
            avgCarsByTimeByCubic = new BigDecimal(accountingByDigging.getTotalCountByCubic()).divide(accountingByDigging.getWorkTimeByCubic(), 2, BigDecimal.ROUND_HALF_UP);
        }
        accountingByDigging.setAvgCarsByTimeByCubic(avgCarsByTimeByCubic);
        Long grossProfitByCubic = 0L;
        if(accountingByDigging.getTotalCountByCubic() != 0)
            grossProfitByCubic = (accountingByDigging.getTotalAmountByCubic() - accountingByDigging.getAmountByFillByCubic()) / accountingByDigging.getTotalCountByCubic();
        accountingByDigging.setGrossProfitByCubic(grossProfitByCubic);
        accountingByDigging.setTotalAmount(accountingByDigging.getTotalAmountByTimer() + accountingByDigging.getAmountByFillBySingleHook() + accountingByDigging.getAmountByFillByGunHammer() + accountingByDigging.getTotalAmountByCubic());
        accountingByDigging.setFillCountByTotal(accountingByDigging.getFillCountByTimer() + accountingByDigging.getFillCountBySingleHook() + accountingByDigging.getFillCountByGunHammer() + accountingByDigging.getFillCountByCubic());
        accountingByDigging.setAmountByFillByTotal(accountingByDigging.getAmountByFillByTimer() + accountingByDigging.getAmountByFillBySingleHook() + accountingByDigging.getAmountByFillByGunHammer() + accountingByDigging.getAmountByFillByCubic());
        //油耗
        BigDecimal oilConsumptionByDigging = new BigDecimal(0);
        if(accountingByDigging.getTotalAmount() != null && accountingByDigging.getTotalAmount() != 0)
            oilConsumptionByDigging = new BigDecimal((float)accountingByDigging.getAmountByFillByTotal() / accountingByDigging.getTotalAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
        accountingByDigging.setOilConsumption(oilConsumptionByDigging);

        ProjectCarCostAccounting accountingByCar = new ProjectCarCostAccounting();
        accountingByCar.setProjectId(projectId);
        accountingByCar.setReportDate(reportDate);
        accountingByCar.setStatisticsType(statisticsType);
        if(carInfoMap.get("total_count") != null)
            accountingByCar.setTotalCount(((BigDecimal) carInfoMap.get("total_count")).longValue());
        if(carInfoMap.get("total_cubic") != null)
            accountingByCar.setTotalCubic(((BigDecimal) carInfoMap.get("total_cubic")).longValue());
        if(carInfoMap.get("total_fill") != null)
            accountingByCar.setFillCount(((BigDecimal) carInfoMap.get("total_fill")).longValue());
        if(carInfoMap.get("total_amount_fill") != null)
            accountingByCar.setAmountByFill(((BigDecimal) carInfoMap.get("total_amount_fill")).longValue());
        if(carInfoMap.get("total_amount") != null)
            accountingByCar.setAmount(((BigDecimal) carInfoMap.get("total_amount")).longValue());
        BigDecimal oilConsumption = new BigDecimal(0);
        if(accountingByCar.getAmount() != null && accountingByCar.getAmount() != 0)
            oilConsumption = new BigDecimal((float)accountingByCar.getAmountByFill() / accountingByCar.getAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
        accountingByCar.setOilConsumption(oilConsumption);
        Long costBySingleHook = 0L;
        if(accountingByCar.getTotalCubic() != null && accountingByCar.getTotalCubic() != 0)
            costBySingleHook = accountingByCar.getAmount() / (accountingByCar.getTotalCubic() / 1000000);
        accountingByCar.setCostBySingleHook(costBySingleHook);
        Long grossProfitByCubicByCar = 0L;
        if(accountingByCar.getTotalCount() != null && accountingByCar.getTotalCount() != 0)
            grossProfitByCubicByCar = (accountingByCar.getAmount() - accountingByCar.getAmountByFill()) / accountingByCar.getTotalCount();
        accountingByCar.setGrossProfitByCubic(grossProfitByCubicByCar);
        Long avgUseFillByCar = 0L;
        if(accountingByCar.getTotalCount() != null  && accountingByCar.getTotalCount() != 0)
            avgUseFillByCar = accountingByCar.getFillCount() / accountingByCar.getTotalCount();
        accountingByCar.setAvgUseFillByCar(avgUseFillByCar);
        Long unitCostByTotal = 0L;
        if(accountingByCar.getTotalCubic()!= null && accountingByCar.getTotalCubic() / 1000000 != 0)
            unitCostByTotal = accountingByDigging.getTotalAmount() / (accountingByCar.getTotalCubic() / 1000000);
        ProjectCostAccountingCount accountingCount = new ProjectCostAccountingCount();
        accountingCount.setProjectId(projectId);
        accountingCount.setReportDate(reportDate);
        accountingCount.setStatisticsType(statisticsType);
        accountingByDigging.setUnitCostByTotal(unitCostByTotal);
        accountingCount.setTotalAmount(accountingByDigging.getTotalAmount() + accountingByCar.getAmount());
        Long costBySingleHookByTotal = 0L;
        if(accountingByCar.getTotalCubic() != null && accountingByCar.getTotalCubic() / 1000000 != 0)
            costBySingleHookByTotal = accountingCount.getTotalAmount() / (accountingByCar.getTotalCubic() / 1000000);
        accountingCount.setCostBySingleHook(costBySingleHookByTotal);
        projectCostAccountingCountServiceI.save(accountingCount);
        projectDiggingCostAccountingServiceI.save(accountingByDigging);
        projectCarCostAccountingServiceI.save(accountingByCar);
    }

    public void costAccountingTotal(Long projectId, Date reportDate) throws IOException {
        Map dayInfoMapByDigging = projectDiggingDayReportServiceI.getTotalInfoByProjectIdAndTime(projectId, reportDate);
        Map grandInfoMapByDigging = projectDiggingDayReportServiceI.getGrandInfoByProjectIdAndTime(projectId, reportDate);
        Map historyInfoMapByDigging = projectDiggingDayReportServiceI.getHistoryInfoByProjectId(projectId);
        Map dayInfoMapByCar = projectDayReportPartCarServiceI.getTotalInfoByProjectIdAndReportDate(projectId, reportDate);
        Map grandInfoMapByCar = projectDayReportPartCarServiceI.getGrandInfoByProjectIdAndReportDate(projectId, reportDate);
        Map historyInfoMapByCar = projectDayReportPartCarServiceI.getHistoryInfoByProjectId(projectId);
        getCostAccountingByDigging(projectId, reportDate, dayInfoMapByDigging, dayInfoMapByCar, StatisticsTypeEnums.DAYCOUNT);
        getCostAccountingByDigging(projectId, reportDate, grandInfoMapByDigging, grandInfoMapByCar, StatisticsTypeEnums.MONTHCOUNT);
        getCostAccountingByDigging(projectId, reportDate, historyInfoMapByDigging, historyInfoMapByCar, StatisticsTypeEnums.HISTORYCOUNT);
    }

    /*public void appInit() throws IOException {
        Date reportDate = new Date();
        List<Project> projectList = projectServiceI.getAll();
        for (Project project : projectList) {
            Map<String, Date> dateMap = workDateService.getWorkTime(project.getId(), reportDate);
            Date startTime = dateMap.get("start");
            Date nightStartTime = dateMap.get("nightStart");
            if(reportDate.getTime() < startTime.getTime())
                nightStartTime = DateUtils.subtractionOneDay(nightStartTime);
            Date dateIdentification = DateUtils.createReportDateByMonth(nightStartTime);
            //projectAppStatisticsByCarServiceI.deleteByCreateDate(nightStartTime, project.getId());
            //projectAppStatisticsByMachineServiceI.deleteByCreateDate(nightStartTime, project.getId());
            //所有渣车的工作信息 根据编号和班次分组
            List<Map> projectCarWorkInfoList = projectCarWorkInfoServiceI.getAppDiggingInfoByProjectIdAndDate(project.getId(), nightStartTime);
            for (int i = 0; i < projectCarWorkInfoList.size(); i++) {
                ProjectAppStatisticsByCar appByCar = new ProjectAppStatisticsByCar();
                Long cubic = Long.parseLong(projectCarWorkInfoList.get(i).get("cubic").toString());
                Integer count = Integer.valueOf(projectCarWorkInfoList.get(i).get("count").toString());
                String carCode = projectCarWorkInfoList.get(i).get("car_code").toString();
                Integer value = Integer.valueOf(projectCarWorkInfoList.get(i).get("shift").toString());
                ShiftsEnums shift = ShiftsEnums.converShift(value);
                appByCar.setCarCode(carCode);
                appByCar.setCarCount(count);
                appByCar.setCubic(cubic);
                appByCar.setProjectId(project.getId());
                appByCar.setShift(shift);
                appByCar.setCreateDate(new Date());
                appByCar.setDateIdentification(dateIdentification);
                stringRedisTemplate.opsForValue().set(SmartminingConstant.APP_CAR_INFO + carCode + project.getId(), JSON.toJSONString(appByCar));
            }
            //所有挖机的工作信息 根据编号和班次分组
            List<Map> projectWorkTimeByDiggingList = projectWorkTimeByDiggingServiceI.getTotalTimeByProjectIdAndDate(project.getId(), nightStartTime);
            for (int i = 0; i < projectWorkTimeByDiggingList.size(); i++) {
                ProjectAppStatisticsByMachine appByMachine = new ProjectAppStatisticsByMachine();
                String machineCode = projectWorkTimeByDiggingList.get(i).get("material_code").toString();
                Integer value = Integer.valueOf(projectWorkTimeByDiggingList.get(i).get("shift").toString());
                ShiftsEnums shift = ShiftsEnums.converShift(value);
                Long workTime = Long.parseLong(projectWorkTimeByDiggingList.get(i).get("workTime").toString());
                appByMachine.setMachineCode(machineCode);
                appByMachine.setShifts(shift);
                appByMachine.setProjectId(project.getId());
                appByMachine.setWorkTime(workTime);
                appByMachine.setCreateDate(new Date());
                appByMachine.setDateIdentification(dateIdentification);
                stringRedisTemplate.opsForValue().set(SmartminingConstant.APP_DIGGING_MACHINE_INFO + machineCode + project.getId(), JSON.toJSONString(appByMachine));
            }
        }
    }*/

    public void appInit() throws IOException {
        Date reportDate = new Date();
        List<Project> projectList = projectServiceI.getAll();
        for (Project project : projectList) {
            Map<String, Date> dateMap = workDateService.getWorkTime(project.getId(), reportDate);
            Date startTime = dateMap.get("start");
            Date nightStartTime = dateMap.get("nightStart");
            if(reportDate.getTime() < startTime.getTime()){
                nightStartTime = DateUtils.subtractionOneDay(nightStartTime);
            }
            projectAppStatisticsByCarServiceI.deleteByCreateDate(nightStartTime, project.getId());
            projectAppStatisticsByMachineServiceI.deleteByCreateDate(nightStartTime, project.getId());
            //所有渣车的工作信息 根据编号和班次分组
            List<Map> projectCarWorkInfoList = projectCarWorkInfoServiceI.getAppDiggingInfoByProjectIdAndDate(project.getId(), nightStartTime);
            List<ProjectAppStatisticsByCar> saveListByCar = new ArrayList<>();
            for (int i = 0; i < projectCarWorkInfoList.size(); i++) {
                ProjectAppStatisticsByCar appByCar = new ProjectAppStatisticsByCar();
                Long cubic = Long.parseLong(projectCarWorkInfoList.get(i).get("cubic").toString());
                Integer count = Integer.valueOf(projectCarWorkInfoList.get(i).get("count").toString());
                String carCode = projectCarWorkInfoList.get(i).get("car_code").toString();
                Integer value = Integer.valueOf(projectCarWorkInfoList.get(i).get("shift").toString());
                ShiftsEnums shift = ShiftsEnums.converShift(value);
                appByCar.setCarCode(carCode);
                appByCar.setCarCount(count);
                appByCar.setCubic(cubic);
                appByCar.setProjectId(project.getId());
                appByCar.setShift(shift);
                appByCar.setCreateDate(nightStartTime);
                saveListByCar.add(appByCar);
                //projectAppStatisticsByCarServiceI.save(appByCar);
            }
            projectAppStatisticsByCarServiceI.batchSave(saveListByCar);
            //所有挖机的工作信息 根据编号和班次分组
            List<Map> projectWorkTimeByDiggingList = projectWorkTimeByDiggingServiceI.getTotalTimeByProjectIdAndDate(project.getId(), nightStartTime);
            List<ProjectAppStatisticsByMachine> saveListByMachine = new ArrayList<>();
            for (int i = 0; i < projectWorkTimeByDiggingList.size(); i++) {
                ProjectAppStatisticsByMachine appByMachine = new ProjectAppStatisticsByMachine();
                String machineCode = projectWorkTimeByDiggingList.get(i).get("material_code").toString();
                Integer value = Integer.valueOf(projectWorkTimeByDiggingList.get(i).get("shift").toString());
                ShiftsEnums shift = ShiftsEnums.converShift(value);
                Long workTime = Long.parseLong(projectWorkTimeByDiggingList.get(i).get("workTime").toString());
                appByMachine.setMachineCode(machineCode);
                appByMachine.setShifts(shift);
                appByMachine.setProjectId(project.getId());
                appByMachine.setWorkTime(workTime);
                appByMachine.setCreateDate(nightStartTime);
                saveListByMachine.add(appByMachine);
                //projectAppStatisticsByMachineServiceI.save(appByMachine);
            }
            projectAppStatisticsByMachineServiceI.batchSave(saveListByMachine);
        }
    }
}
