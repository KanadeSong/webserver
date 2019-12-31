package com.seater.smartmining.schedule;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.ScheduleResponse;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.*;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.utils.api.AutoApiUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.string.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.tomcat.jni.Mmap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Slf4j
public class ScheduleService {

    /**
     * 渣车月报
     *
     * @param projectId
     * @param reportDate
     * @param cron       调度时间
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void scheduleCarMonthReport(Long projectId, Date reportDate, String cron) throws IOException {
        Date startTime = DateUtils.getStartDate(reportDate);
        String startStr = DateUtils.formatDateByPattern(startTime, SmartminingConstant.DATEFORMAT);
        //获取到当月的最后一天
        Date end = DateUtils.getEndDate(reportDate);
        String endStr = DateUtils.formatDateByPattern(end, SmartminingConstant.DATEFORMAT);
        end = DateUtils.createReportDateByMonth(end);
        ProjectDayReport projectDayReport = null;
        List<ProjectDayReport> projectDayReportList = AutoApiUtils.returnDayReport().getByProjectIdAndCreateDate(projectId, end);
        if (projectDayReportList.size() > 0) {
            projectDayReport = projectDayReportList.get(0);
        }
        //生成报表统计日期
        Date reportDay = DateUtils.createReportDateByMonth(end);
        AutoApiUtils.returnProjectMonthReport().deleteByProjectIdAndReportDate(projectId, reportDay);
        AutoApiUtils.returnProjectMonthReportTotal().deleteByProjectIdAndReportDate(projectId, reportDay);
        ProjectMonthReportTotal total = new ProjectMonthReportTotal();
        total.setProjectId(projectId);
        total.setReportDate(reportDay);
        total = AutoApiUtils.returnProjectMonthReportTotal().save(total);
        List<Map> listMap = AutoApiUtils.returnDayReportCar().getMonthReportByProjectIdAndReportDate(projectId, startStr, endStr);
        if (listMap != null && listMap.size() > 0) {
            for (int i = 0; i < listMap.size(); i++) {
                ProjectMonthReport monthReport = new ProjectMonthReport();
                monthReport.setTotalId(total.getId());
                monthReport.setProjectId(projectId);
                monthReport.setReportDate(reportDay);
                //车辆主键编号
                Long carId = ((BigInteger) listMap.get(i).get("car_id")).longValue();
                monthReport.setCarId(carId);
                //车辆编号
                String code = listMap.get(i).get("car_code").toString();
                monthReport.setCode(code);
                //车主编号
                String carOwnerName = listMap.get(i).get("car_owner_name").toString();
                monthReport.setCarOwnerName(carOwnerName);
                //总车数
                Integer totalCount = Integer.valueOf(listMap.get(i).get("total_count").toString());
                monthReport.setTotalCount(totalCount);
                total.setTotalCount(total.getTotalCount() + totalCount);
                //总方量
                Long totalCubic = ((BigDecimal) listMap.get(i).get("total_cubic")).longValue();
                monthReport.setTotalCubic(totalCubic);
                total.setTotalCubic(total.getTotalCubic() + totalCubic);
                //总金额
                Long totalAmount = ((BigDecimal) listMap.get(i).get("total_amount")).longValue();
                monthReport.setTotalAmount(totalAmount);
                total.setTotalAmount(total.getTotalAmount() + totalAmount);
                total.setSubsidyAmount(total.getSubsidyAmount() + monthReport.getSubsidyAmount());
                //总加油量
                Long totalFill = ((BigDecimal) listMap.get(i).get("total_fill")).longValue();
                monthReport.setTotalFill(totalFill);
                total.setTotalFill(total.getTotalFill() + totalFill);
                //总加油金额
                Long totalAmountByFill = ((BigDecimal) listMap.get(i).get("total_amount_fill")).longValue();
                monthReport.setTotalAmountByFill(totalAmountByFill);
                total.setTotalAmountByFill(total.getTotalAmountByFill() + totalAmountByFill);
                total.setDeduction(total.getDeduction() + monthReport.getDeduction());
                //应付金额
                Long shouldPayAmount = totalAmount + monthReport.getSubsidyAmount() - totalAmountByFill - monthReport.getDeduction();
                monthReport.setShouldPayAmount(shouldPayAmount);
                total.setShouldPayAmount(total.getShouldPayAmount() + shouldPayAmount);
                //里程数
                Long distance = ((BigDecimal) listMap.get(i).get("mileage")).longValue();
                monthReport.setDistance(distance);
                total.setDistance(total.getDistance() + monthReport.getDistance());
                //平均价格含油
                Long avgAmountByFill = 0L;
                if (totalCount != 0) {
                    avgAmountByFill = totalAmount / totalCount;
                }
                monthReport.setAvgAmountByFill(avgAmountByFill);
                //平均价格不含油
                Long avgAmount = 0L;
                if (totalCount != 0) {
                    avgAmount = shouldPayAmount / totalCount;
                }
                monthReport.setAvgAmount(avgAmount);
                BigDecimal oilConsumption = new BigDecimal(0);
                if (totalAmount != 0) {
                    oilConsumption = new BigDecimal((float) totalAmountByFill / totalAmount).setScale(4, BigDecimal.ROUND_HALF_UP);
                }
                monthReport.setOilConsumption(oilConsumption);
                //油量 毫升/车
                Long avgFill = 0L;
                if (totalCount != 0) {
                    avgFill = totalFill / totalCount;
                }
                monthReport.setAvgFill(avgFill);
                AutoApiUtils.returnProjectMonthReport().save(monthReport);
                /*settlementDetailByCar(projectId, carId, reportDate);*/
            }
            if (projectDayReport != null) {
                total.setTotalCount(projectDayReport.get_grandTotalCount());
                total.setTotalCubic(projectDayReport.get_grandTotalCubic());
                total.setTotalAmount(projectDayReport.get_grandTotalAmount());
            }
            Long avgAmount = 0L;
            Long avgAmountByFill = 0L;
            Long avgFill = 0L;
            BigDecimal avgCubics = new BigDecimal(0);
            if (total.getTotalCount() != 0) {
                avgAmount = total.getShouldPayAmount() / total.getTotalCount();
                avgAmountByFill = total.getTotalAmount() / total.getTotalCount();
                avgFill = total.getTotalFill() / total.getTotalCount();
                avgCubics = new BigDecimal((float) (total.getTotalCubic() / 1000000) / total.getTotalCount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            total.setAvgAmount(avgAmount);
            total.setAvgAmountByFill(avgAmountByFill);
            total.setAvgFill(avgFill);
            BigDecimal oilConsumption = new BigDecimal(0);
            if (total.getTotalAmount() != 0)
                oilConsumption = new BigDecimal((float) total.getTotalAmountByFill() / total.getTotalAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
            total.setOilConsumption(oilConsumption);

            Integer onDutyCount = AutoApiUtils.returnDayReportCar().getMonthCarCountByProjectIdAndReportDate(projectId, startTime, end).size();
            total.setOnDutyCount(onDutyCount);
            BigDecimal unitCost = new BigDecimal(0);
            if (total.getTotalCubic() != 0) {
                unitCost = new BigDecimal((float) total.getTotalAmount() / (total.getTotalCubic() / 1000000)).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            total.setUnitCost(unitCost);
            total.setAvgCubics(avgCubics);
            BigDecimal grandAvgCountsPerCarPerDay = total.getOnDutyCount() != 0 ? new BigDecimal((float) total.getTotalCount() / total.getOnDutyCount()).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            total.setGrandAvgCountsPerCarPerDay(grandAvgCountsPerCarPerDay);
            AutoApiUtils.returnProjectMonthReportTotal().save(total);
        }
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setCreateDate(new Date());
            log.setProjectId(projectId);
            log.setScheduleEnum(ScheduleEnum.CarMonthReport);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    /**
     * 挖机月报
     *
     * @param projectId
     * @param reportDate
     * @param cron       执行时间
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void scheduleDiggingMonthReport(Long projectId, Date reportDate, String cron) throws IOException {
        //获取当月第一天的工作时间
        Map<String, Date> result = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
        Date startDay = result.get("startDay");
        Date endDay = DateUtils.getEndDate(reportDate);
        Map<String, Date> result1 = AutoApiUtils.returnWorkDate().getWorkTime(projectId, endDay);
        Date end = result1.get("end");
        //获取到汇总日报表后的数据集合
        List<Map> monthReportList = AutoApiUtils.returnProjectDiggingDayReport().getMonthReportByProjectIdAndReportDate(projectId, startDay, end);
        //报表统计日期
        Date reportDay = DateUtils.createReportDateByMonth(endDay);

        AutoApiUtils.returnProjectDiggingMonthReport().deleteByProjectIdAndReportDate(projectId, reportDay);
        AutoApiUtils.returnProjectDigginggMonthReportTotal().deleteByProjectIdAndReportDate(projectId, reportDay);

        ProjectDiggingMonthReportTotal total = new ProjectDiggingMonthReportTotal();
        total.setProjectId(projectId);
        total.setReportDate(reportDay);
        total = AutoApiUtils.returnProjectDigginggMonthReportTotal().save(total);
        //获取扣款和补贴金额
        List<DeductionDiggingByMonth> deductionDiggingByMonthList = AutoApiUtils.returnDeductionDiggingByMonth().getAllByProjectIdAndReportDate(projectId, reportDay);
        //创建扣款和补贴金额索引
        Map<Long, Integer> deductionMapIndex = new HashMap<>();
        int j = 0;
        for (DeductionDiggingByMonth deductionDiggingByMonth : deductionDiggingByMonthList) {
            deductionMapIndex.put(deductionDiggingByMonth.getMonthReportId(), j);
            j++;
        }
        for (int i = 0; i < monthReportList.size(); i++) {
            //创建报表对象
            ProjectDiggingMonthReport monthReport = new ProjectDiggingMonthReport();
            monthReport.setTotalId(total.getId());
            monthReport.setProjectId(projectId);
            monthReport.setReportDate(reportDay);
            //获取挖机编号
            Long diggingMachineId = ((BigInteger) monthReportList.get(i).get("machine_id")).longValue();
            monthReport.setMachineId(diggingMachineId);
            //获取到扣款和补贴金额
            Integer index = deductionMapIndex.get(diggingMachineId);
            if (index != null) {
                Long deductionAmount = deductionDiggingByMonthList.get(index) != null ? deductionDiggingByMonthList.get(index).getAmountByDeduction() : 0L;
                Long subsidyAmount = deductionDiggingByMonthList.get(index) != null ? deductionDiggingByMonthList.get(index).getAmountBySubsidyAmount() : 0L;
                //设置月报表的扣款和补贴金额
                monthReport.setDeduction(deductionAmount);
                monthReport.setSubsidyAmount(subsidyAmount);
            }
            String diggingCode = monthReportList.get(i).get("machine_code").toString();
            monthReport.setMachineCode(diggingCode);
            //获取挖机名称
            String machineName = monthReportList.get(i).get("machine_name").toString();
            monthReport.setMachineName(machineName);
            Long ownerId = Long.parseLong(monthReportList.get(i).get("owner_id").toString());
            monthReport.setOwnerId(ownerId);
            //获取机主名称
            String workerName = monthReportList.get(i).get("owner_name").toString();
            monthReport.setWorkerName(workerName);
            //获取工作总工时
            BigDecimal totalWorkTime = (BigDecimal) monthReportList.get(i).get("total_work_timer");
            monthReport.setGranWorkCountTime(totalWorkTime);
            total.setGranWorkCountTime(total.getGranWorkCountTime().add(totalWorkTime));
            //获取计时总工时
            BigDecimal granWorkTimeByTimer = (BigDecimal) monthReportList.get(i).get("total_time_by_timer");
            monthReport.setGrandWorkTimeByTimer(granWorkTimeByTimer);
            total.setGrandWorkTimeByTimer(total.getGrandWorkTimeByTimer().add(granWorkTimeByTimer));
            //获取计时单价
            Long price = ((BigInteger) monthReportList.get(i).get("price_by_timer")).longValue();
            monthReport.setSinglePrice(price);
            total.setSinglePrice(price);
            //获取计时总金额
            Long grandTimerAmount = ((BigDecimal) monthReportList.get(i).get("amount_by_timer")).longValue();
            monthReport.setGrandTimerAmount(grandTimerAmount);
            total.setGrandTimerAmount(total.getGrandTimerAmount() + grandTimerAmount);
            //获取计方总工时
            BigDecimal granWorkTimeByCubic = (BigDecimal) monthReportList.get(i).get("total_time_by_cubic");
            monthReport.setGranWorkTimeByCubic(granWorkTimeByCubic);
            total.setGranWorkTimeByCubic(total.getGranWorkTimeByCubic().add(granWorkTimeByCubic));
            //获取计方总车数
            Long carCount = ((BigDecimal) monthReportList.get(i).get("car_total_count_by_cubic")).longValue();
            monthReport.setTotalCount(carCount);
            total.setTotalCount(total.getTotalCount() + carCount);
            //获取总方量数
            Long grandTotalCubic = ((BigDecimal) monthReportList.get(i).get("total_count_by_cubic")).longValue();
            monthReport.setGrandTotalCubic(grandTotalCubic);
            total.setGrandTotalCubic(total.getGrandTotalCubic() + grandTotalCubic);
            //获取包方总金额
            Long grandCubeAmout = ((BigDecimal) monthReportList.get(i).get("total_amount_by_cubic")).longValue();
            monthReport.setGrandCubeAmout(grandCubeAmout);
            total.setGrandCubeAmout(total.getGrandCubeAmout() + grandCubeAmout);
            //获取当月计时用油量
            Long grandTotalFillByTimer = Long.parseLong(monthReportList.get(i).get("total_grand_fill_by_timer").toString());
            monthReport.setGrandTotalFillByTimer(grandTotalFillByTimer);
            total.setGrandTotalFillByTimer(total.getGrandUsingFillByTimer() + grandTotalFillByTimer);
            //获取当月计方用油量
            Long grandTotalFillByCubic = Long.parseLong(monthReportList.get(i).get("total_grand_fill_by_cubic").toString());
            monthReport.setGrandTotalFillByCubic(grandTotalFillByCubic);
            total.setGrandUsingFillByCubic(total.getGrandUsingFillByCubic() + grandTotalFillByCubic);
            //获取用油量
            Long grandTotalFill = ((BigDecimal) monthReportList.get(i).get("total_grand_fill")).longValue();
            monthReport.setGrandTotalFill(grandTotalFill);
            total.setGrandTotalFill(total.getGrandTotalFill() + grandTotalFill);
            //获取当月计时用油金额
            Long totalAmountByFillByTimer = Long.parseLong(monthReportList.get(i).get("total_amount_by_fill_by_timer").toString());
            monthReport.setGrandUsingFillByTimer(totalAmountByFillByTimer);
            total.setGrandUsingFillByTimer(total.getGrandUsingFillByTimer() + totalAmountByFillByTimer);
            //获取当月计方用油金额
            Long totalAmountByFillByCubic = Long.parseLong(monthReportList.get(i).get("total_amount_by_fill_by_cubic").toString());
            monthReport.setGrandUsingFillByCubic(totalAmountByFillByCubic);
            total.setGrandUsingFillByCubic(total.getGrandUsingFillByCubic() + totalAmountByFillByCubic);
            //获取用油金额
            Long grandUsingFill = ((BigDecimal) monthReportList.get(i).get("total_amount_by_fill")).longValue();
            monthReport.setGrandUsingFill(grandUsingFill);
            total.setGrandUsingFill(total.getGrandUsingFill() + grandUsingFill);
            //获取包方结余金额
            Long payAmount = grandCubeAmout - grandUsingFill;
            monthReport.setPayAmount(payAmount);
            total.setPayAmount(total.getPayAmount() + payAmount);
            //获取工作总金额
            Long workTotalAmount = ((BigDecimal) monthReportList.get(i).get("total_amount")).longValue();
            monthReport.setWorkTotalAmount(workTotalAmount);
            total.setWorkTotalAmount(total.getWorkTotalAmount() + workTotalAmount);
            //todo 包月暂无法获取数据 默认用0表示
            Long monthAmount = 0L;
            monthReport.setMonthAmount(monthAmount);
            total.setMonthAmount(total.getMonthAmount() + monthReport.getMonthAmount());
            //合计扣款
            total.setDeduction(total.getDeduction() + monthReport.getDeduction());
            //合计补贴
            total.setSubsidyAmount(total.getSubsidyAmount() + monthReport.getSubsidyAmount());
            //设置结算总金额
            Long settlementAmount = payAmount + grandTimerAmount + monthAmount + monthReport.getSubsidyAmount();
            monthReport.setSettlementAmount(settlementAmount);
            total.setSettlementAmount(total.getSettlementAmount() + settlementAmount);
            //设置应付金额
            Long shouldPayAmount = settlementAmount - monthReport.getDeduction();
            monthReport.setShouldPayAmount(shouldPayAmount);
            total.setShouldPayAmount(total.getShouldPayAmount() + shouldPayAmount);
            //设置平均耗油量
            BigDecimal avgUseFill = new BigDecimal(0);
            if (totalWorkTime.compareTo(BigDecimal.ZERO) != 0) {
                avgUseFill = new BigDecimal(grandTotalFill).divide(totalWorkTime, 2, BigDecimal.ROUND_HALF_UP);
            }
            monthReport.setAvgUseFill(avgUseFill);
            //设置平均车辆
            BigDecimal avgCar = new BigDecimal(0);
            if (totalWorkTime.compareTo(BigDecimal.ZERO) != 0) {
                avgCar = new BigDecimal((float) carCount).divide(totalWorkTime, 2, BigDecimal.ROUND_HALF_UP);
            }
            monthReport.setAvgCar(avgCar);
            //设置毛利润
            BigDecimal grossProfit = new BigDecimal(0);
            if (carCount != 0) {
                grossProfit = new BigDecimal((float) (workTotalAmount - grandUsingFill) / carCount).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            monthReport.setGrossProfit(grossProfit);
            //设置油耗
            BigDecimal oilConsumption = new BigDecimal(0);
            if (workTotalAmount != 0) {
                oilConsumption = new BigDecimal(((float) grandUsingFill) / workTotalAmount).setScale(4, BigDecimal.ROUND_HALF_UP);
            }
            monthReport.setOilConsumption(oilConsumption);
            AutoApiUtils.returnProjectDiggingMonthReport().save(monthReport);
        }
        //平均用油量
        BigDecimal avgUseFill = new BigDecimal(0);
        if (total.getGranWorkCountTime().compareTo(BigDecimal.ZERO) != 0) {
            avgUseFill = new BigDecimal((float) total.getGrandTotalFill()).divide(total.getGranWorkCountTime(), 2, BigDecimal.ROUND_HALF_UP);
        }
        total.setAvgUseFill(avgUseFill);
        //平均车辆
        BigDecimal avgCar = new BigDecimal(0);
        if (total.getGrandWorkTimeByTimer().compareTo(BigDecimal.ZERO) != 0L) {
            avgCar = new BigDecimal((float) total.getTotalCount()).divide(total.getGrandWorkTimeByTimer(), 2, BigDecimal.ROUND_HALF_UP);
        }
        total.setAvgCar(avgCar);
        //毛利润 单位分
        BigDecimal grossProfit = new BigDecimal(0);
        if (total.getTotalCount() != 0) {
            grossProfit = new BigDecimal((float) (total.getWorkTotalAmount() - total.getGrandUsingFill()) / total.getTotalCount()).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        total.setGrossProfit(grossProfit);
        //油耗
        BigDecimal oilConsumption = new BigDecimal(0);
        if (total.getWorkTotalAmount() != 0) {
            oilConsumption = new BigDecimal((float) total.getGrandUsingFill() / total.getWorkTotalAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        total.setOilConsumption(oilConsumption);
        total.setOnDutyCount(monthReportList.size());
        List<ProjectMonthReportTotal> projectMonthReportTotalList = AutoApiUtils.returnProjectMonthReportTotal().getByProjectIdAndReportDate(projectId, reportDay);
        BigDecimal unitCost = new BigDecimal(0);
        if (projectMonthReportTotalList.size() > 0) {
            ProjectMonthReportTotal projectMonthReportTotal = projectMonthReportTotalList.get(0);
            unitCost = new BigDecimal(((float) (total.getSettlementAmount() + total.getGrandUsingFill()) / 100) / ((float) projectMonthReportTotal.getTotalCubic() / 1000000)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        total.setUnitCost(unitCost);
        AutoApiUtils.returnProjectDigginggMonthReportTotal().save(total);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setScheduleEnum(ScheduleEnum.DiggingMonthReport);
            log.setCreateDate(new Date());
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    @SuppressWarnings("unchecked")
    public static void settlementDetailByCar(Long projectId, Long carId, Date reportDate) throws IOException {
        Map<String, Date> result = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
        Date startTime = result.get("startDay");
        Date endTime = DateUtils.getEndDate(reportDate);
        reportDate = DateUtils.createReportDateByMonth(endTime);
        Map<String, Date> result1 = AutoApiUtils.returnWorkDate().getWorkTime(projectId, endTime);
        endTime = result1.get("end");
        List<Map> detailList = AutoApiUtils.returnProjectCarWork().getAllSettlementByProjectIdAndCarIdAndTime(projectId, carId, startTime, endTime);
        if (detailList.size() > 0) {
            AutoApiUtils.returnProjectSettlementTotal().deleteByProjectIdAndReportDate(projectId, reportDate, carId);
            AutoApiUtils.returnProjectSettlementDetail().deleteByProjectIdAndReportDate(projectId, reportDate, carId);
            AutoApiUtils.returnProjectSettlementSummary().deleteByProjectIdAndReportDate(projectId, reportDate, carId);

            ProjectCar projectCar = AutoApiUtils.returnProjectCar().get(carId);
            ProjectSettlementTotal total = new ProjectSettlementTotal();
            total.setProjectId(projectId);
            total.setReportDate(reportDate);
            total.setCarId(carId);
            total.setOwnerName(projectCar.getOwnerName());
            total.setCapacity(projectCar.getModifyCapacity());
            total = AutoApiUtils.returnProjectSettlementTotal().save(total);
            Project project = AutoApiUtils.returnProject().get(projectId);
            Long carsCountT = 0L;
            Long oilCountT = 0L;
            Long amountByElseT = 0L;
            Long amountByMealsT = 0L;
            Long subsidyAmountT = 0L;
            Long balanceT = 0L;
            Long mileageT = 0L;
            Long rentT = 0L;
            /*Date chooseDate = new Date();*/
            Long countS = 0L;
            Long amountS = 0L;
            //获取当月所有扣除数据
            List<DeductionBySettlementSummary> deductionBySettlementSummaryList = AutoApiUtils.returnDeductionBySettlementSummary().getAllByProjectIdAndReportDate(projectId, reportDate);

            //总数量
            for (int i = 0; i < detailList.size(); i++) {
                ProjectSettlementDetail detail = new ProjectSettlementDetail();
                detail.setProjectId(projectId);
                detail.setCarId(carId);
                detail.setTotalId(total.getId());
                detail.setPrice(project.getOilPirce());
                if (detailList.get(i).get("count") != null) {
                    detail.setCarsCount(Integer.valueOf(detailList.get(i).get("count").toString()));
                    countS = countS + detail.getCarsCount();
                }
                if (detailList.get(i).get("distance") != null) {
                    detail.setDistance(Long.parseLong(detailList.get(i).get("distance").toString()));
                }
                if (detailList.get(i).get("material_id") != null) {
                    Long materialId = Long.parseLong(detailList.get(i).get("material_id").toString());
                    detail.setMaterialId(materialId);
                    ProjectCarMaterial material = AutoApiUtils.returnProjectCarMaterial().getPayableByProjectIdAndDistance(projectId, detail.getDistance());
                    detail.setPrice(material.getPrice());
                }
                if (detailList.get(i).get("material_name") != null) {
                    detail.setMaterialName(detailList.get(i).get("material_name").toString());
                }
                if (detailList.get(i).get("cubic") != null) {
                    detail.setCubicCount(Long.parseLong(detailList.get(i).get("cubic").toString()));
                }
                if (detailList.get(i).get("amount") != null) {
                    detail.setAmount(Long.parseLong(detailList.get(i).get("amount").toString()));
                    amountS = amountS + detail.getAmount();
                }
                if (detailList.get(i).get("mileage") != null) {
                    mileageT = mileageT + Long.parseLong(detailList.get(i).get("mileage").toString());
                }
                Date beginDate = null;
                Date endDate = null;
                Date date = null;
                if (detailList.get(i).get("reportDate") != null) {
                    date = DateUtils.stringFormatDate(detailList.get(i).get("reportDate").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                    date = DateUtils.createReportDateByMonth(date);
                    detail.setReportDate(date);
                    Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, date);
                    beginDate = dateMap.get("start");
                    endDate = dateMap.get("end");
                }
                detail.setCreateDate(reportDate);
                detail = AutoApiUtils.returnProjectSettlementDetail().save(detail);
                Date date1 = new Date();
                if (i != detailList.size() - 1) {
                    if (detailList.get(i + 1).get("reportDate") != null) {
                        date1 = DateUtils.stringFormatDate(detailList.get(i + 1).get("reportDate").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                        date1 = DateUtils.createReportDateByMonth(date1);
                    }
                }
                if (date.getTime() != date1.getTime()) {
                    //date1 = date;
                    /*chooseDate = date;*/
                    Map fillMap = AutoApiUtils.returnProjectCarFill().getCarFillByProjectIdAAndCarIdAndTime(projectId, carId, beginDate, endDate);
                    /*List<Map> carsMap = AutoApiUtils.returnProjectCarWork().getCarsCountByProjectIdAndCarIdAndTime(projectId, carId, beginDate, endDate);*/
                    ProjectSettlementSummary summary = new ProjectSettlementSummary();
                    if (deductionBySettlementSummaryList != null) {
                        for (DeductionBySettlementSummary deduction : deductionBySettlementSummaryList) {
                            if (deduction.getCarId() == summary.getCarId() && deduction.getReportDate().getTime() == summary.getReportDate().getTime()) {
                                summary.setAmountByElse(deduction.getThiryFive());
                                summary.setRent(deduction.getRent());
                                summary.setAmountByMeals(deduction.getAmountByMeal());
                                summary.setSubsidyAmount(deduction.getAmountBySubsidyAmount());
                            }
                        }
                    }
                    summary.setCarId(carId);
                    summary.setProjectId(projectId);
                    summary.setDetailId(detail.getId());
                    summary.setPrice(project.getOilPirce());
                    summary.setTotalId(total.getId());
                    summary.setReportDate(date);
                    rentT = rentT + summary.getRent();
                    if (fillMap.get("totalFill") != null) {
                        summary.setOilCount(Long.parseLong(fillMap.get("totalFill").toString()));
                    }
                    if (fillMap.get("totalAmount") != null) {
                        summary.setAmountByOil(Long.parseLong(fillMap.get("totalAmount").toString()));
                        total.setAmountByOil(total.getAmountByOil() + summary.getAmountByOil());
                    }
                    summary.setCarsCount(countS);
                    countS = 0L;
                    summary.setPrice(project.getOilPirce());
                    /*if (carsMap.get("amount") != null) {*/
                    /*Long amount = Long.parseLong(carsMap.get("amount").toString());*/
                    summary.setBalance(amountS + (summary.getAmountByElse() * 3500L) - summary.getAmountByMeals() - summary.getAmountByOil() - summary.getAmountByElse() + summary.getSubsidyAmount());
                    amountS = 0L;
                    /*}*/
                    /*if (carsMap.get("mileage") != null) {*/
                    /*mileageT = mileageT + Long.parseLong(carsMap.get("mileage").toString());*/
                    /*}*/
                    carsCountT = carsCountT + summary.getCarsCount();
                    oilCountT = oilCountT + summary.getOilCount();
                    amountByElseT = amountByElseT + summary.getAmountByElse();
                    amountByMealsT = amountByMealsT + summary.getAmountByMeals();
                    subsidyAmountT = subsidyAmountT + summary.getSubsidyAmount();
                    balanceT = balanceT + summary.getBalance();
                    summary.setCreateDate(reportDate);
                    AutoApiUtils.returnProjectSettlementSummary().save(summary);
                }
            }
            List<Map> totalListMap = AutoApiUtils.returnProjectSettlementDetail().getTotalInfoByTotalId(total.getId());
            List<Map> listMap = new ArrayList<>();
            for (int i = 0; i < totalListMap.size(); i++) {
                Map<String, Object> totalMap = new HashMap();
                if (totalListMap.get(i).get("distance") != null && totalListMap.get(i).get("carsCount") != null) {
                    totalMap.put("distance", totalListMap.get(i).get("distance"));
                }
                if (totalListMap.get(i).get("carsCount") != null) {
                    totalMap.put("carsCount", totalListMap.get(i).get("carsCount"));
                }
                if (totalListMap.get(i).get("cubic") != null) {
                    totalMap.put("cubic", totalListMap.get(i).get("cubic"));
                }
                if (totalListMap.get(i).get("amount") != null) {
                    totalMap.put("amount", totalListMap.get(i).get("amount"));
                }
                listMap.add(totalMap);
            }
            String jsonStr = JSON.toJSONString(listMap);
            total.setTotalJson(jsonStr);
            total.setRent(rentT);
            total.setSubsidyAmount(subsidyAmountT);
            total.setAmountByMeals(amountByMealsT);
            total.setAmountByElse(amountByElseT);
            total.setOilCount(oilCountT);
            total.setBalance(balanceT);
            total.setCarsCount(carsCountT);
            total.setMileage(mileageT);
            AutoApiUtils.returnProjectSettlementTotal().save(total);
        }
    }

    /**
     * 成本分析表
     *
     * @param projectId
     * @param reportDate
     * @param cron       执行时间cron
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void scheduleCostAccounting(Long projectId, Date reportDate, String cron) throws IOException {
        Map dayInfoMapByDigging = AutoApiUtils.returnProjectDiggingDayReport().getTotalInfoByProjectIdAndTime(projectId, reportDate);
        Map grandInfoMapByDigging = AutoApiUtils.returnProjectDiggingDayReport().getGrandInfoByProjectIdAndTime(projectId, reportDate);
        Map historyInfoMapByDigging = AutoApiUtils.returnProjectDiggingDayReport().getHistoryInfoByProjectId(projectId);
        Map dayInfoMapByCar = AutoApiUtils.returnDayReportCar().getTotalInfoByProjectIdAndReportDate(projectId, reportDate);
        Map grandInfoMapByCar = AutoApiUtils.returnDayReportCar().getGrandInfoByProjectIdAndReportDate(projectId, reportDate);
        Map historyInfoMapByCar = AutoApiUtils.returnDayReportCar().getHistoryInfoByProjectId(projectId);
        AutoApiUtils.returnReport().getCostAccountingByDigging(projectId, reportDate, dayInfoMapByDigging, dayInfoMapByCar, StatisticsTypeEnums.DAYCOUNT);
        AutoApiUtils.returnReport().getCostAccountingByDigging(projectId, reportDate, grandInfoMapByDigging, grandInfoMapByCar, StatisticsTypeEnums.MONTHCOUNT);
        AutoApiUtils.returnReport().getCostAccountingByDigging(projectId, reportDate, historyInfoMapByDigging, historyInfoMapByCar, StatisticsTypeEnums.HISTORYCOUNT);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.CostReport);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    /**
     * 渣车日报
     *
     * @param project
     * @param reportDate 统计时间
     * @param cron       执行时间字符串
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void scheduleCarReport(Project project, Date reportDate, String cron) throws IOException {
        reportDate = DateUtils.convertDate(reportDate.getTime());
        Date start = DateUtils.createReportDateByMonth(reportDate);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(start);
        Date monthStart = DateUtils.getStartDate(reportDate);
        Date end = DateUtils.getEndDateByNow(reportDate);
        Date dayReportDate = DateUtils.createReportDateByMonth(DateUtils.getEndDate(reportDate));
        reportDate = start;

        AutoApiUtils.returnDayReport().deleteByProjectIdAndReportDate(project.getId(), reportDate);
        AutoApiUtils.returnDayReportCar().deleteByProjectIdAndReportDate(project.getId(), reportDate);
        AutoApiUtils.returnDayReportDistance().deleteByProjectIdAndReportDate(project.getId(), reportDate);

        //获取累计到本日内容，并生成索引
        List<Map> carGrandTotalList = null;
        List<Map> grandTotalDistanceList = null;
        List<Map> grandTotalFillList = null;
        Map<Long, Integer> carGrandTotalIndex = new HashMap();
        Map<Long, Integer> grandTotalDistanceIndex = new LinkedHashMap<>();
        Map<Long, Integer> grandTotalFillIndex = new HashMap();
        //if (calendar1.get(Calendar.DAY_OF_MONTH) != 1) {
        carGrandTotalList = AutoApiUtils.returnProjectCarWork().getCarGrandTotalListByProjectIdAndTime(project.getId(), monthStart, end);
        if (carGrandTotalList != null) {
            for (int i = 0; i < carGrandTotalList.size(); i++)
                carGrandTotalIndex.put(((BigInteger) carGrandTotalList.get(i).get("car_id")).longValue(), i);
        }

        grandTotalDistanceList = AutoApiUtils.returnProjectCarWork().getDistanceListByProjectIdAndTime(project.getId(), monthStart, end);
        if (grandTotalDistanceList != null) {
            for (int i = 0; i < grandTotalDistanceList.size(); i++)
                grandTotalDistanceIndex.put(((BigInteger) grandTotalDistanceList.get(i).get("payable_distance")).longValue(), i);
        }

        //获取当月总加油量
        grandTotalFillList = AutoApiUtils.returnProjectCarFill().getCarGrandTotalFillByProjectIdAndTime(project.getId(), monthStart, end);
        if (grandTotalFillList != null) {
            for (int i = 0; i < grandTotalFillList.size(); i++)
                grandTotalFillIndex.put(((BigInteger) grandTotalFillList.get(i).get("car_id")).longValue(), i);
        }
        //}

        //获取当天加油统计，并生成索引
        Map<Long, Integer> fillListIndex = new HashMap();
        List<Map> fillList = AutoApiUtils.returnProjectCarFill().getCarGrandTotalFillByProjectIdAndTime(project.getId(), start, end);
        if (fillList != null) {
            for (int i = 0; i < fillList.size(); i++)
                fillListIndex.put(((BigInteger) fillList.get(i).get("car_id")).longValue(), i);
        }
        //新建报表，并获得ID
        ProjectDayReport newProjectDayReport = new ProjectDayReport();
        newProjectDayReport = AutoApiUtils.returnDayReport().save(newProjectDayReport);
        Long newReportId = newProjectDayReport.getId();

        Integer _grandTotalCount = 0;
        Long _grandTotalCubic = 0L;
        Long _grandTotalAmount = 0L;

        //获取距离统计表，并生成索引
        Map<Long, Integer> distanceListIndex = new LinkedHashMap<>();
        List<Map> distanceList = AutoApiUtils.returnProjectCarWork().getDistanceListByProjectIdAndTime(project.getId(), start, end);
        List<Long> distanceMap = new ArrayList<>();
        for (int i = 0; i < distanceList.size(); i++) {
            distanceMap.add(((BigInteger) distanceList.get(i).get("payable_distance")).longValue());
        }
        Integer length = distanceList.size();
        for (Map.Entry<Long, Integer> entry : grandTotalDistanceIndex.entrySet()) {
            if (!distanceMap.contains(entry.getKey())) {
                Long distance = entry.getKey();
                Integer distanceIndex = grandTotalDistanceIndex.get(distance);
                Map map = new HashMap();
                map.put("payable_distance", distance);
                map.put("totalCount", grandTotalDistanceList.get(distanceIndex).get("totalCount"));
                map.put("totalCubic", grandTotalDistanceList.get(distanceIndex).get("totalCubic"));
                map.put("totalAmount", grandTotalDistanceList.get(distanceIndex).get("totalAmount"));
                distanceList.add(map);
            }
        }
        if (distanceList != null) {
            for (int i = 0; i < distanceList.size(); i++) {
                ProjectDayReportPartDistance projectDayReportPartDistance = new ProjectDayReportPartDistance();
                Long distance = Long.parseLong(distanceList.get(i).get("payable_distance").toString());
                Integer totalCount = Integer.valueOf(distanceList.get(i).get("totalCount").toString());
                Long totalCubic = Long.parseLong(distanceList.get(i).get("totalCubic").toString());
                Long totalAmount = Long.parseLong(distanceList.get(i).get("totalAmount").toString());
                distanceListIndex.put(distance, i);
                projectDayReportPartDistance.setProjectId(project.getId());
                projectDayReportPartDistance.setReportId(newReportId);
                projectDayReportPartDistance.setReportDate(reportDate);
                projectDayReportPartDistance.setDistance(distance);
                if (i < length) {
                    projectDayReportPartDistance.setTotalCount(totalCount);
                    projectDayReportPartDistance.setTotalCubic(totalCubic);
                    projectDayReportPartDistance.setTotalAmount(totalAmount);
                } else {
                    projectDayReportPartDistance.setTotalCount(0);
                    projectDayReportPartDistance.setTotalCubic(0L);
                    projectDayReportPartDistance.setTotalAmount(0L);
                }
                Integer index = grandTotalDistanceIndex.get(distance);
                if (index != null) {
                    Integer grandTotalCount = ((BigInteger) grandTotalDistanceList.get(index).get("totalCount")).intValue();
                    Long grandTotalCubic = ((BigDecimal) grandTotalDistanceList.get(index).get("totalCubic")).longValue();
                    Long grandTotalAmount = ((BigDecimal) grandTotalDistanceList.get(index).get("totalAmount")).longValue();
                    _grandTotalCount += grandTotalCount;
                    _grandTotalCubic += grandTotalCubic;
                    _grandTotalAmount += grandTotalAmount;
                    projectDayReportPartDistance.setGrandTotalCount(grandTotalCount);
                    projectDayReportPartDistance.setGrandTotalCubic(grandTotalCubic);
                    projectDayReportPartDistance.setGrandTotalAmount(grandTotalAmount);
                } else {
                    _grandTotalCount += totalCount;
                    _grandTotalCubic += totalCubic;
                    _grandTotalAmount += totalAmount;
                    projectDayReportPartDistance.setGrandTotalCount(totalCount);
                    projectDayReportPartDistance.setGrandTotalCubic(totalCubic);
                    projectDayReportPartDistance.setGrandTotalAmount(totalAmount);
                }
                AutoApiUtils.returnDayReportDistance().save(projectDayReportPartDistance);
            }
        }

        newProjectDayReport.set_grandTotalCount(_grandTotalCount);
        newProjectDayReport.set_grandTotalCubic(_grandTotalCubic);
        newProjectDayReport.set_grandTotalAmount(_grandTotalAmount);


        List<Map> countList = AutoApiUtils.returnProjectCarWork().getCountListByProjectIdAndTime(project.getId(), start, end);

        List<Long> carsIdMap = new ArrayList<>();
        for (int i = 0; i < countList.size(); i++) {
            carsIdMap.add(((BigInteger) countList.get(i).get("car_id")).longValue());
        }
        for (Map.Entry<Long, Integer> entry : grandTotalFillIndex.entrySet()) {
            if (!carsIdMap.contains(entry.getKey())) {
                Long carId = entry.getKey();
                ProjectCar projectCar = AutoApiUtils.returnProjectCar().get(carId);
                if (projectCar != null) {
                    Map map = new HashMap();
                    map.put("car_id", entry.getKey());
                    map.put("car_owner_id", projectCar.getOwnerId());
                    map.put("car_owner_name", projectCar.getOwnerName());
                    map.put("car_code", projectCar.getCode());
                    map.put("shift", 0);
                    map.put("payable_distance", 0L);
                    map.put("count", 0);
                    map.put("amount", 0L);
                    map.put("cubic", 0L);
                    countList.add(map);
                    carsIdMap.add(entry.getKey());
                }
            }
        }
        if (countList != null) {
            ProjectDayReportPartCar projectDayReportCar = null;
            Integer earlyTotlaCount = 0;
            Integer nightTotalCount = 0;
            Long amount = 0L;
            Long cubic = 0L;
            Long sumdDistance = 0L;
            Integer[] earlyCountList = new Integer[grandTotalDistanceIndex.size()];
            Integer[] nightCountList = new Integer[grandTotalDistanceIndex.size()];
            Arrays.fill(earlyCountList, 0);
            Arrays.fill(nightCountList, 0);
            for (int i = 0; i < countList.size(); i++) {
                int next_i = i + 1;
                Long carId = Long.parseLong(countList.get(i).get("car_id").toString());
                ProjectCar projectCar = AutoApiUtils.returnProjectCar().get(carId);
                Long carOwnerId = projectCar.getOwnerId();
                //Long carOwnerId = Long.parseLong(countList.get(i).get("car_owner_id").toString());
                //String carOwnerName = countList.get(i).get("car_owner_name").toString();
                String carOwnerName = projectCar.getOwnerName();
                String carCode = countList.get(i).get("car_code").toString();
                Integer shift = ((Integer) countList.get(i).get("shift"));
                Long distance = Long.parseLong(countList.get(i).get("payable_distance").toString());
                Integer count = Integer.valueOf(countList.get(i).get("count").toString());
                amount += Long.parseLong(countList.get(i).get("amount").toString());
                cubic += Long.parseLong(countList.get(i).get("cubic").toString());
                sumdDistance = sumdDistance + (distance * count);

                if (shift == 1) {
                    earlyTotlaCount += count;
                    earlyCountList[grandTotalDistanceIndex.get(distance)] = count;
                } else if (shift == 2) {
                    nightTotalCount += count;
                    nightCountList[grandTotalDistanceIndex.get(distance)] = count;
                }

                if (next_i >= countList.size() || (Long.parseLong(countList.get(next_i).get("car_id").toString())) != carId) {
                    Integer carIndex = carGrandTotalIndex.get(carId);
                    Integer fillIndex = fillListIndex.get(carId);
                    Integer gtFillIndex = grandTotalFillIndex.get(carId);

                    projectDayReportCar = new ProjectDayReportPartCar();
                    projectDayReportCar.setProjectId(project.getId());
                    projectDayReportCar.setReportId(newReportId);
                    projectDayReportCar.setReportDate(reportDate);
                    projectDayReportCar.setCarId(carId);
                    projectDayReportCar.setCarOwnerId(carOwnerId);
                    projectDayReportCar.setCarOwnerName(carOwnerName);
                    projectDayReportCar.setCarCode(carCode);
                    projectDayReportCar.setEarlyTotalCount(earlyTotlaCount);
                    projectDayReportCar.setNightTotalCount(nightTotalCount);
                    projectDayReportCar.setEarlyCountList(JsonHelper.toJsonString(earlyCountList));
                    projectDayReportCar.setNightCountList(JsonHelper.toJsonString(nightCountList));

                    Integer totalCount = earlyTotlaCount + nightTotalCount;
                    Integer grandTotalCount = (carIndex != null ? (Integer.valueOf(carGrandTotalList.get(carIndex).get("count").toString())) : totalCount);
                    projectDayReportCar.setTotalCount(totalCount);
                    projectDayReportCar.setGrandTotalCount(grandTotalCount);

                    Long grandTotalAmount = (carIndex != null ? (Long.parseLong(carGrandTotalList.get(carIndex).get("amount").toString())) : amount);
                    projectDayReportCar.setTotalAmount(amount);
                    projectDayReportCar.setGrandTotalAmount(grandTotalAmount);

                    Long grandTotalCubic = (carIndex != null ? (Long.parseLong(carGrandTotalList.get(carIndex).get("cubic").toString())) : cubic);
                    projectDayReportCar.setTotalCubic(cubic);
                    projectDayReportCar.setGrandTotalCubic(grandTotalCubic);

                    projectDayReportCar.setCubicPerTimes(grandTotalCount != 0 ? grandTotalCubic / grandTotalCount : 0);

                    Long fill = (fillIndex != null ? (Long.parseLong(fillList.get(fillIndex).get("totalFill").toString())) : 0);
                    Long grandTotalFill = (gtFillIndex != null ? (Long.parseLong(grandTotalFillList.get(gtFillIndex).get("totalFill").toString())) : fill);
                    projectDayReportCar.setTotalFill(fill);
                    projectDayReportCar.setGrandTotalFill(grandTotalFill);

                    Long amountFill = (fillIndex != null ? (Long.parseLong(fillList.get(fillIndex).get("totalAmount").toString())) : 0);
                    Long grandTotalAmountFill = (gtFillIndex != null ? (Long.parseLong(grandTotalFillList.get(gtFillIndex).get("totalAmount").toString())) : amountFill);
                    projectDayReportCar.setTotalAmountFill(amountFill);
                    projectDayReportCar.setGrandTotalAmountFill(grandTotalAmountFill);

                    projectDayReportCar.setAvgUsing(totalCount != 0 ? fill / totalCount : 0);
                    projectDayReportCar.setGrandTotalAvgUsing(grandTotalCount != 0 ? grandTotalAmountFill / grandTotalCount : 0);

                    projectDayReportCar.setPayable(amount - amountFill);
                    projectDayReportCar.setGrandTotalPayable(grandTotalAmount - grandTotalAmountFill);

                    Long grandTotalMileage = (carIndex != null ? (Long.parseLong(carGrandTotalList.get(carIndex).get("mileage").toString())) : sumdDistance);
                    projectDayReportCar.setMileage(sumdDistance);
                    projectDayReportCar.setGrandTotalMileage(grandTotalMileage);
                    projectDayReportCar.setGrandTotalAvgMileage(grandTotalCount != 0 ? grandTotalMileage / grandTotalCount : 0);

                    projectDayReportCar.setPercentOfUsing(amount != 0 ? amountFill * 100 / amount : 0);
                    projectDayReportCar.setPercentOfMonthUsing(grandTotalAmount != 0 ? grandTotalAmountFill * 100 / grandTotalAmount : 0);

                    AutoApiUtils.returnDayReportCar().save(projectDayReportCar);

                    newProjectDayReport.setEarlyTotalCount(newProjectDayReport.getEarlyTotalCount() + earlyTotlaCount);
                    newProjectDayReport.setNightTotalCount(newProjectDayReport.getNightTotalCount() + nightTotalCount);

                    if (earlyTotlaCount > 0)
                        newProjectDayReport.setEarlyOnDutyCount(newProjectDayReport.getEarlyOnDutyCount() + 1);
                    if (nightTotalCount > 0)
                        newProjectDayReport.setNightOnDutyCount(newProjectDayReport.getNightOnDutyCount() + 1);
                    if (earlyTotlaCount > 0 || nightTotalCount > 0)
                        newProjectDayReport.setOnDutyCount(newProjectDayReport.getOnDutyCount() + 1);

                    newProjectDayReport.setTotalCount(newProjectDayReport.getTotalCount() + earlyTotlaCount + nightTotalCount);
                    newProjectDayReport.setGrandTotalCount(newProjectDayReport.getGrandTotalCount() + grandTotalCount);
                    newProjectDayReport.setTotalAmount(newProjectDayReport.getTotalAmount() + amount);
                    newProjectDayReport.setGrandTotalAmount(newProjectDayReport.getGrandTotalAmount() + grandTotalAmount);

                    newProjectDayReport.setTotalCubic(newProjectDayReport.getTotalCubic() + cubic);
                    newProjectDayReport.setGrandTotalCubic(newProjectDayReport.getGrandTotalCubic() + grandTotalCubic);

                    newProjectDayReport.setTotalFill(newProjectDayReport.getTotalFill() + fill);
                    newProjectDayReport.setGrandTotalFill(newProjectDayReport.getGrandTotalFill() + grandTotalFill);

                    newProjectDayReport.setTotalAmountFill(newProjectDayReport.getTotalAmountFill() + amountFill);
                    newProjectDayReport.setGrandTotalAmountFill(newProjectDayReport.getGrandTotalAmountFill() + grandTotalAmountFill);

                    newProjectDayReport.setPayable(newProjectDayReport.getPayable() + amount - amountFill);
                    newProjectDayReport.setGrandTotalPayable(newProjectDayReport.getGrandTotalPayable() + grandTotalAmount - grandTotalAmountFill);

                    newProjectDayReport.setMileage(newProjectDayReport.getMileage() + sumdDistance);
                    newProjectDayReport.setGrandTotalMileage(newProjectDayReport.getGrandTotalMileage() + grandTotalMileage);

                    earlyTotlaCount = 0;
                    nightTotalCount = 0;
                    Arrays.fill(earlyCountList, 0);
                    Arrays.fill(nightCountList, 0);
                    amount = 0L;
                    cubic = 0L;
                    sumdDistance = 0L;
                }
            }
            newProjectDayReport.set_totalCount(newProjectDayReport.getTotalCount());
            newProjectDayReport.set_totalCubic(newProjectDayReport.getTotalCubic());
            newProjectDayReport.set_totalAmount(newProjectDayReport.getTotalAmount());
            newProjectDayReport.setGrandTotalCount(newProjectDayReport.get_grandTotalCount());
            newProjectDayReport.setGrandTotalAmount(newProjectDayReport.get_grandTotalAmount());
            newProjectDayReport.setGrandTotalCubic(newProjectDayReport.get_grandTotalCubic());
            newProjectDayReport.setTotalAmount(newProjectDayReport.get_totalAmount());
            newProjectDayReport.setGrandTotalAmount(newProjectDayReport.get_grandTotalAmount());
        }

        newProjectDayReport.setProjectId(project.getId());
        newProjectDayReport.setReportDate(reportDate);
        newProjectDayReport.setCreateDate(dayReportDate);
        System.out.println("报表保存日期：" + DateUtils.formatDateByPattern(reportDate, SmartminingConstant.DATEFORMAT));
        newProjectDayReport.setCubicPerTimes(newProjectDayReport.getGrandTotalCount() != 0 ? newProjectDayReport.getGrandTotalCubic() / newProjectDayReport.getGrandTotalCount() : 0);

        newProjectDayReport.setAvgUsing(newProjectDayReport.getTotalCount() != 0 ? newProjectDayReport.getTotalFill() / newProjectDayReport.getTotalCount() : 0);
        newProjectDayReport.setGrandTotalAvgUsing(newProjectDayReport.getGrandTotalCount() != 0 ? newProjectDayReport.getGrandTotalFill() / newProjectDayReport.getGrandTotalCount() : 0);

        newProjectDayReport.setGrandTotalAvgMileage(newProjectDayReport.getGrandTotalCount() != 0 ? newProjectDayReport.getGrandTotalMileage() / newProjectDayReport.getGrandTotalCount() : 0);

        newProjectDayReport.setPercentOfUsing(newProjectDayReport.getTotalAmount() != 0 ? newProjectDayReport.getTotalAmountFill() * 100 / newProjectDayReport.getTotalAmount() : 0);
        newProjectDayReport.setPercentOfMonthUsing(newProjectDayReport.getGrandTotalAmount() != 0 ? newProjectDayReport.getGrandTotalAmountFill() * 100 / newProjectDayReport.getGrandTotalAmount() : 0);

        newProjectDayReport.setGrossProfit(newProjectDayReport.getTotalCount() != 0 ? (newProjectDayReport.getTotalAmount() - newProjectDayReport.getTotalAmountFill()) / newProjectDayReport.getTotalCount() : 0);
        newProjectDayReport.setMonthGrossProfit(newProjectDayReport.getGrandTotalCount() != 0 ? (newProjectDayReport.getGrandTotalAmount() - newProjectDayReport.getGrandTotalAmountFill()) / newProjectDayReport.getGrandTotalCount() : 0);

        newProjectDayReport.setEarlyAttendance(newProjectDayReport.getOnDutyCount() != 0 ? newProjectDayReport.getEarlyOnDutyCount() * 100 / newProjectDayReport.getOnDutyCount() : 0);
        newProjectDayReport.setNightAttendance(newProjectDayReport.getOnDutyCount() != 0 ? newProjectDayReport.getNightOnDutyCount() * 100 / newProjectDayReport.getOnDutyCount() : 0);

        newProjectDayReport.setAvgCountsPerCarPerDay(newProjectDayReport.getOnDutyCount() != 0 ? newProjectDayReport.getTotalCount() / newProjectDayReport.getOnDutyCount() : 0);
        newProjectDayReport.setProjectTotalCar(AutoApiUtils.returnProjectCar().getCountByProjectId(project.getId()));
        newProjectDayReport.setEarlyAttendance(newProjectDayReport.getProjectTotalCar() != 0 ? newProjectDayReport.getEarlyOnDutyCount() * 100 / newProjectDayReport.getProjectTotalCar() : 0);
        newProjectDayReport.setNightAttendance(newProjectDayReport.getProjectTotalCar() != 0 ? newProjectDayReport.getNightOnDutyCount() * 100 / newProjectDayReport.getProjectTotalCar() : 0);

        AutoApiUtils.returnDayReport().save(newProjectDayReport);


        //计算历史数据   后期直接添加  未优化
        AutoApiUtils.returnProjectDayReportHistory().deleteByProjectIdAndReportDate(project.getId(), reportDate);
        Map workInfoMap = AutoApiUtils.returnProjectCarWork().getHistoryInfoByTime(project.getId(), reportDate);
        //总数量
        Integer count = 0;
        Long cubic = 0L;
        Long amount = 0L;
        Long distance = 0L;
        if (workInfoMap != null) {
            count = workInfoMap.get("count") != null ? Integer.valueOf(workInfoMap.get("count").toString()) : 0;
            //总方量
            cubic = workInfoMap.get("cubic") != null ? Long.parseLong(workInfoMap.get("cubic").toString()) : 0L;
            //总金额
            amount = workInfoMap.get("amount") != null ? Long.parseLong(workInfoMap.get("amount").toString()) : 0L;
            //总里程
            distance = workInfoMap.get("distance") != null ? Long.parseLong(workInfoMap.get("distance").toString()) : 0L;
        }
        Map fillInfoMap = AutoApiUtils.returnProjectCarFill().getAllByProjectIdAndDate(project.getId(), reportDate);
        Long volumn = 0L;
        Long fillAmount = 0L;
        if (fillInfoMap != null) {
            //加油量
            volumn = fillInfoMap.get("volumn") != null ? Long.parseLong(fillInfoMap.get("volumn").toString()) : 0L;
            //加油金额
            fillAmount = fillInfoMap.get("amount") != null ? Long.parseLong(fillInfoMap.get("amount").toString()) : 0L;
        }
        //运输成本 分/立方
        Long cost = cubic != null && cubic != 0 ? amount / (cubic / 1000000L) : 0L;
        //油耗
        BigDecimal oilConsumption = new BigDecimal(0);
        if (amount != null && amount != 0) {
            oilConsumption = new BigDecimal((float) fillAmount / amount).setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        //应付金额
        Long shouldAmount = amount - fillAmount;
        //平均用油 毫升/车
        Long avgOil = count != null && count != 0 ? volumn / count : 0L;
        //毛利 分/车
        Long grossProfit = count != null && count != 0 ? shouldAmount / count : 0L;
        //注册总车数
        Integer totalCount = AutoApiUtils.returnProjectCar().getCountByProjectId(project.getId());
        //总天数
        List<Map> daysList = AutoApiUtils.returnProjectCarWork().countByProjectIdAndDateIdentification(project.getId(), reportDate);
        Integer days = daysList.size();
        //平均次数(趟/天)
        BigDecimal avgCarByTime = new BigDecimal(0);
        if (days != null && days != 0 && totalCount != null && totalCount != 0)
            avgCarByTime = new BigDecimal(count / days / totalCount).setScale(2, BigDecimal.ROUND_HALF_UP);
        Integer countByMaterial = AutoApiUtils.returnProjectCarWork().countByProjectIdAndDateIdentificationAndMaterialId(project.getId(), reportDate, 2L);
        Long avgDistance = count != null && count != 0 ? distance / count : 0L;
        ProjectDayReportHistory history = new ProjectDayReportHistory();
        history.setProjectId(project.getId());
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
        AutoApiUtils.returnProjectDayReportHistory().save(history);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(project.getId());
            log.setScheduleEnum(ScheduleEnum.CarDayReport);
            log.setCreateDate(new Date());
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
        System.out.println("任务调度执行结束，数据已保存到数据库中。");
    }


    /**
     * 挖机日报
     *
     * @param projectId
     * @param reportDate
     * @param cron
     * @throws IOException
     * @throws SmartminingProjectException
     */
    @SuppressWarnings("unchecked")
    public static void scheduleDiggingReport(Long projectId, Date reportDate, String cron) throws IOException, SmartminingProjectException {
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
        Date workStart = dateMap.get("start");
        Date workEnd = dateMap.get("end");
        //获取到统计日期
        reportDate = DateUtils.convertDate(reportDate.getTime());
        //当天最开始日期
        Date start = DateUtils.createReportDateByMonth(reportDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        //获取当天的年数
        int year = calendar.get(Calendar.YEAR);
        //获取当天的月数
        int month = calendar.get(Calendar.MONTH);
        //获取到当天的号数
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //获取到前一天的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date beforeDay = calendar.getTime();
        //获取前一天的年数
        int beforeYear = calendar.get(Calendar.YEAR);
        //获取前一天的月数
        int beforeMonth = calendar.get(Calendar.MONTH);
        //月初的日期
        Date startDay = DateUtils.getStartDate(reportDate);
        //当天最晚的日期
        Date end = DateUtils.getEndDateByNow(reportDate);
        reportDate = start;

        //获取到扣除时间的总集合
        List<DeductionDigging> deductionDiggings = AutoApiUtils.returnDeductionDigging().getAllByProjectIdAndReportDate(projectId, reportDate);
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
        AutoApiUtils.returnProjectDiggingDayReport().deleteByProjectIdAndReportDate(projectId, reportDate);
        AutoApiUtils.returnProjectDiggingDayReportTotal().deleteByProjectIdAndReportDate(projectId, reportDate);
        //AutoApiUtils.returnProjectDiggingDayReportHistory().deleteByProjectIdAndReportDate(projectId, reportDate);
        ProjectDiggingDayReportTotal total = new ProjectDiggingDayReportTotal();
        total.setProjectId(projectId);
        total.setReportDate(reportDate);
        total = AutoApiUtils.returnProjectDiggingDayReportTotal().save(total);
        //获取到所有挖机当天的工作时间
        List<Map> workTimeList = AutoApiUtils.returnProjectWorkTimeByDigging().getAllByProjectIdAndTime(projectId, workStart, workEnd);
        //获取前一天的报表合计对象
        ProjectDiggingDayReportTotal beforeTotal = null;
        //如果当前的号数大于1 并且年月都和前一天的一样
        if (day > 1 && year == beforeYear && month == beforeMonth) {
            List<ProjectDiggingDayReportTotal> totalList = AutoApiUtils.returnProjectDiggingDayReportTotal().getByProjectIdAndReportDate(projectId, beforeDay);
            if (totalList != null && totalList.size() > 0) {
                beforeTotal = totalList.get(0);
            } else {
                beforeTotal = new ProjectDiggingDayReportTotal();
            }
        } else {
            beforeTotal = new ProjectDiggingDayReportTotal();
        }
        //获取到前一天对应报表的所有挖机信息
        List<ProjectDiggingDayReport> projectDiggingDayReportList = AutoApiUtils.returnProjectDiggingDayReport().getByTotalId(beforeTotal.getId());
        //获取当天所有挖机的总加油信息
        List<Map> fillInfoList = AutoApiUtils.returnProjectCarFill().getDiggingTotalFillByProjectIdAndTimeGroupByCar(projectId, workStart, workEnd);
        //创建加油索引
        Map<String, Integer> fillIndexMap = new HashMap();
        for (int i = 0; i < fillInfoList.size(); i++) {
            Integer pricingType = Integer.valueOf(fillInfoList.get(i).get("pricing_type_enums").toString());
            Long machineId = Long.parseLong(fillInfoList.get(i).get("car_id").toString());
            fillIndexMap.put(machineId + "fill" + pricingType, i);
        }
        //获取当天所有挖机的总加油量（唯一）
        List<Map> fillList = AutoApiUtils.returnProjectCarFill().getDiggingMachineIdByProjectIdAndTime(projectId, workStart, workEnd);
        List<Long> carsList = new ArrayList<>();
        //对比未上班但却加油的挖机信息
        if (fillList != null) {
            for (int i = 0; i < fillList.size(); i++) {
                if (fillList.get(i).get("car_id") != null && Integer.valueOf(fillList.get(i).get("car_id").toString()) != 0)
                    carsList.add(Long.parseLong(fillList.get(i).get("car_id").toString()));
            }
        }
        if (projectDiggingDayReportList != null) {
            for (ProjectDiggingDayReport report : projectDiggingDayReportList) {
                if (!carsList.contains(report.getMachineId()))
                    carsList.add(report.getMachineId());
            }
        }
        List<Long> diggingList = new ArrayList<>();
        for (int i = 0; i < workTimeList.size(); i++) {
            Long machineId = Long.parseLong(workTimeList.get(i).get("material_id").toString());
            diggingList.add(machineId);
        }
        for (Long id : carsList) {
            if (!diggingList.contains(id)) {
                Map map = new HashMap();
                map.put("material_id", id);
                workTimeList.add(map);
            }
        }
        //获取到所有挖机当天的工作总车数 总方量和总金额 根据班次分组
        List<Map> workInfoListByShift = AutoApiUtils.returnProjectCarWork().getDiggingDayCountListByProjectIdAndTimeGroupByShift(projectId, start, end);
        //创建工作信息班次索引
        Map<String, Integer> workInfoListShiftMap = new HashMap<>();
        for(int i = 0; i < workInfoListByShift.size(); i++){
            Long diggingMachineId = Long.parseLong(workInfoListByShift.get(i).get("digging_machine_id").toString());
            Integer shift = Integer.valueOf(workInfoListByShift.get(i).get("shift").toString());
            String key = diggingMachineId.toString() + shift;
            workInfoListShiftMap.put(key, i);
        }
        //根据时间查询挖机对应的计时工作信息
        List<Map> countListByHour = AutoApiUtils.returnProjectCarWork().getDiggingDayCountListByProjectIdAndTime(projectId, start, end, 1);
        //创建计时挖机工作信息索引
        Map<Long, Integer> countIndexByHour = new HashMap<>();
        for (int i = 0; i < countListByHour.size(); i++) {
            Long id = countListByHour.get(i).get("digging_machine_id") != null ? Long.parseLong(countListByHour.get(i).get("digging_machine_id").toString()) : 0L;
            countIndexByHour.put(id, i);
        }
        //根据时间查询挖机对应的计方工作信息
        List<Map> countListByCubic = AutoApiUtils.returnProjectCarWork().getDiggingDayCountListByProjectIdAndTime(projectId, start, end, 2);
        //创建计方挖机工作信息索引
        Map<Long, Integer> countIndexByCubic = new HashMap<>();
        for (int i = 0; i < countListByCubic.size(); i++) {
            Long machineId = countListByCubic.get(i).get("digging_machine_id") != null ? Long.parseLong(countListByCubic.get(i).get("digging_machine_id").toString()) : 0L;
            countIndexByCubic.put(machineId, i);
        }
        //获取所有挖机包方详情
        List<Map> detailList = AutoApiUtils.returnProjectCarWork().getMaterialDetailByProjectIdAndMachineIdAndTime(projectId, start, end);
        //创建包方详情索引
        Map<String, Integer> detailIndexCubic = new HashMap<>();
        for (int i = 0; i < detailList.size(); i++) {
            Long materialId = Long.parseLong(detailList.get(i).get("material_id").toString());
            Long machineId = Long.parseLong(detailList.get(i).get("digging_machine_id").toString());
            detailIndexCubic.put(machineId.toString() + materialId, i);
        }
        //获取所有挖机累计包方详情
        List<Map> grandDetailList = AutoApiUtils.returnProjectCarWork().getMaterialDetailByProjectIdAndMachineIdAndTime(projectId, startDay, end);
        //创建累计包方详情索引
        Map<String, Integer> grandDetailIndexCubic = new HashMap<>();
        for (int i = 0; i < grandDetailList.size(); i++) {
            Long machineId = Long.parseLong(grandDetailList.get(i).get("digging_machine_id").toString());
            Long materialId = Long.parseLong(grandDetailList.get(i).get("material_id").toString());
            grandDetailIndexCubic.put(machineId.toString() + materialId, i);
        }
        //挖机当天工作信息
        List<Map> diggingInfoList = AutoApiUtils.returnProjectWorkTimeByDigging().getDiggingInfoByProjectId(projectId, workStart, workEnd);
        Map<String, Integer> diggingWorkIndex = new HashMap<>();
        for (int i = 0; i < diggingInfoList.size(); i++) {
            Integer shifts = Integer.valueOf(diggingInfoList.get(i).get("shift").toString());
            Integer pricingType = Integer.valueOf(diggingInfoList.get(i).get("pricing_type_enums").toString());
            Long machineId = Long.parseLong(diggingInfoList.get(i).get("material_id").toString());
            diggingWorkIndex.put(machineId + "shift" + shifts + "type" + pricingType, i);
        }
        //合计包方详情
        List<Map> detailTotal = new ArrayList<>();
        //累计合计包方详情
        List<Map> grandDetailTotal = new ArrayList<>();
        List<ProjectDiggingDayReport> reportList = new ArrayList<>();
        for (int i = 0; i < workTimeList.size(); i++) {
            //挖机ID
            Long machineId = Long.parseLong(workTimeList.get(i).get("material_id").toString());
            //挖机对象
            ProjectDiggingMachine machine = AutoApiUtils.returnProjectDiggingMachine().get(machineId);
            if (machine == null)
                throw new SmartminingProjectException("挖机不存在，挖机ID：" + machineId);
            //计时金额对象
            ProjectHourPrice projectHourPrice = AutoApiUtils.returnProjectHourPrice().getByProjectIdAndBrandIdAndModelIdAndCarType(projectId, machine.getBrandId(), machine.getModelId(), 2);
            //获取前一天
            ProjectDiggingDayReport beforeReport = null;
            if (day > 1) {
                List<ProjectDiggingDayReport> beforeReportList = AutoApiUtils.returnProjectDiggingDayReport().getAllByProjectIdAndMachineIdAndReportDate(projectId, machineId, beforeDay);
                if (beforeReportList != null && beforeReportList.size() > 0) {
                    beforeReport = beforeReportList.get(0);
                } else {
                    beforeReport = new ProjectDiggingDayReport();
                }
            } else {
                beforeReport = new ProjectDiggingDayReport();
            }
            //日报表对象
            ProjectDiggingDayReport dayReport = new ProjectDiggingDayReport();
            dayReport.setReportDate(start);
            dayReport.setProjectId(projectId);
            dayReport.setTotalId(total.getId());
            dayReport.setMachineId(machineId);
            dayReport.setMachineCode(machine.getCode());
            dayReport.setMachineName(machine.getBrandName() + machine.getModelName());
            dayReport.setOwnerId(machine.getOwnerId());
            dayReport.setOwnerName(machine.getOwnerName());
            //计时单价
            dayReport.setPriceByTimer(projectHourPrice != null ? projectHourPrice.getPrice() : 0L);
            //合计计时单价
            total.setGrandPriceByTimer(dayReport.getPriceByTimer());
            //累计计时单价
            dayReport.setGrandPriceByTimer(dayReport.getPriceByTimer());
            //合计累计计时单价
            total.setGrandPriceByTimer(dayReport.getPriceByTimer());
            //当前挖机白班扣除时间
            BigDecimal deductionTimeByDay = deductionIndexByDay.get(machineId) != null ? deductionIndexByDay.get(machineId) : new BigDecimal(0);
            dayReport.setDeductionTimeByDay(deductionTimeByDay);
            //当前挖机晚班扣除时间
            BigDecimal deductionTimeByNight = deductionIndexByNight.get(machineId) != null ? deductionIndexByNight.get(machineId) : new BigDecimal(0);
            dayReport.setDeductionTimeByNight(deductionTimeByNight);
            Integer fillIndexByHour = null;
            Integer fillIndexByCubic = null;
            //获取到油量
            if (fillIndexMap.size() > 0) {
                fillIndexByHour = fillIndexMap.get(machineId + "fill" + PricingTypeEnums.Hour.getValue()) != null ? Integer.valueOf(fillIndexMap.get(machineId + "fill" + PricingTypeEnums.Hour.getValue()).toString()) : null;
                fillIndexByCubic = fillIndexMap.get(machineId + "fill" + PricingTypeEnums.Cube.getValue()) != null ? Integer.valueOf(fillIndexMap.get(machineId + "fill" + PricingTypeEnums.Cube.getValue()).toString()) : null;
            }

            //计时油量
            Long fillCountByHour = fillIndexByHour != null ? Long.parseLong(fillInfoList.get(fillIndexByHour).get("totalFill").toString()) : 0L;
            //计时油金额
            Long fillAmountByHour = fillIndexByHour != null ? Long.parseLong(fillInfoList.get(fillIndexByHour).get("totalAmount").toString()) : 0L;
            //计方油量
            Long fillCountByCubic = fillIndexByCubic != null ? Long.parseLong(fillInfoList.get(fillIndexByCubic).get("totalFill").toString()) : 0L;
            //计方油金额
            Long fillAmountByCubic = fillIndexByCubic != null ? Long.parseLong(fillInfoList.get(fillIndexByCubic).get("totalAmount").toString()) : 0L;
            //计时加油量
            dayReport.setTotalGrandFillByTimer(fillCountByHour);
            //计时油金额
            dayReport.setTotalAmountByFillByTimer(fillAmountByHour);
            Integer carCountByHourIndex = countIndexByHour.get(machineId);
            //根据班次分组的工作信息白班索引
            Integer dayIndex = workInfoListShiftMap.get(machineId.toString() + Shift.Early.getAlias());
            //根据班次分组的工作信息晚班索引
            Integer nightIndex = workInfoListShiftMap.get(machineId.toString() + Shift.Night.getAlias());
            //白班总车数
            Long carTotalCountByDay = dayIndex != null ? Long.parseLong(workInfoListByShift.get(dayIndex).get("count").toString()) : 0L;
            //晚班总车数
            Long carTotalCountByNight = nightIndex != null ? Long.parseLong(workInfoListByShift.get(nightIndex).get("count").toString()) : 0L;
            //白班总方量
            Long totalCubicByDay = dayIndex != null ? Long.parseLong(workInfoListByShift.get(dayIndex).get("cubic").toString()) : 0L;
            //晚班总方量
            Long totalCubicByNight = nightIndex != null ? Long.parseLong(workInfoListByShift.get(nightIndex).get("cubic").toString()) : 0L;
            //白班总金额
            Long totalAmountByDay = dayIndex != null ? Long.parseLong(workInfoListByShift.get(dayIndex).get("amount").toString()) : 0L;
            //晚班总金额
            Long totalAmountByNight = nightIndex != null ? Long.parseLong(workInfoListByShift.get(nightIndex).get("amount").toString()) : 0L;
            //白班总车数
            dayReport.setCarTotalCountByDay(carTotalCountByDay);
            //合计白班总车数
            total.setCarTotalCountByDay(total.getCarTotalCountByDay() + carTotalCountByDay);
            //累计白班总车数
            dayReport.setCountCarsByDay(beforeReport.getCountCarsByDay() + carTotalCountByDay);
            //累计合计白班总车数
            total.setCountCarsByDay(beforeTotal.getCountCarsByDay() + carTotalCountByDay);
            //晚班总车数
            dayReport.setCarTotalCountByNight(carTotalCountByNight);
            //合计晚班总车数
            total.setCarTotalCountByNight(total.getCarTotalCountByNight() + carTotalCountByNight);
            //累计晚班总车数
            dayReport.setCountCarsByNight(beforeReport.getCountCarsByNight() + carTotalCountByNight);
            //累计合计晚班总车数
            total.setCountCarsByNight(beforeTotal.getCountCarsByNight() + carTotalCountByNight);
            //白班总方量
            dayReport.setTotalCubicByDay(totalCubicByDay);
            //合计白班总方量
            total.setTotalCountByDay(total.getTotalCountByDay() + totalCubicByDay);
            //累计白班总方量
            dayReport.setCountCubicByDay(beforeReport.getCountCubicByDay() + totalCubicByDay);
            //合计累计白班总方量
            total.setCountCubicByDay(beforeTotal.getCountCubicByDay() + totalCubicByDay);
            //晚班总方量
            dayReport.setTotalCubicByNight(totalCubicByNight);
            //合计晚班总方量
            total.setTotalCountByNight(total.getTotalCountByNight() + totalCubicByNight);
            //累计晚班总方量
            dayReport.setCountCubicByNight(beforeReport.getCountCubicByNight() + totalCubicByNight);
            //合计累计晚班总方量
            total.setCountCubicByNight(beforeTotal.getCountCubicByNight() + totalCubicByNight);
            //白班总金额
            dayReport.setTotalAmountByDay(totalAmountByDay);
            //合计白班总金额
            total.setTotalAmountByDay(total.getTotalAmountByDay() + totalAmountByDay);
            //累计白班总金额
            dayReport.setCountAmountByDay(beforeReport.getCountAmountByDay() + totalAmountByDay);
            //合计累计白班总金额
            total.setCountAmountByDay(beforeTotal.getCountAmountByDay() + totalAmountByDay);
            //晚班总金额
            dayReport.setTotalAmountByNight(totalAmountByNight);
            //合计晚班总金额
            total.setTotalAmountByNight(total.getTotalAmountByNight() + totalAmountByNight);
            //累计晚班总金额
            dayReport.setCountAmountByNight(beforeReport.getCountAmountByNight() + totalAmountByNight);
            //合计累计晚班总金额
            total.setCountAmountByNight(beforeTotal.getCountAmountByNight() + totalAmountByNight);
            //计时总车数
            Long countTotalByHour = carCountByHourIndex != null ? Long.parseLong(countListByHour.get(carCountByHourIndex).get("count").toString()) : 0L;
            //计时总方量
            Long cubicTotalByHour = carCountByHourIndex != null ? Long.parseLong(countListByHour.get(carCountByHourIndex).get("cubic").toString()) : 0L;
            dayReport.setTotalCountByTimer(dayReport.getTotalCountByTimer() + countTotalByHour);
            //计时总方量
            dayReport.setCubicCountByTimer(dayReport.getCubicCountByTimer() + cubicTotalByHour);
            Integer workInfoByHourByDay = null;
            Integer workInfoByCubicByDay = null;
            Integer workInfoByHourByNight = null;
            Integer workInfoByCubicByNight = null;
            if (diggingInfoList.size() > 0) {
                workInfoByHourByDay = diggingWorkIndex.get(machineId + "shift" + Shift.Early.getAlias() + "type" + PricingTypeEnums.Hour.getValue()) != null ? Integer.valueOf(diggingWorkIndex.get(machineId + "shift" + Shift.Early.getAlias() + "type" + PricingTypeEnums.Hour.getValue()).toString()) : null;
                workInfoByHourByNight = diggingWorkIndex.get(machineId + "shift" + Shift.Night.getAlias() + "type" + PricingTypeEnums.Hour.getValue()) != null ? Integer.valueOf(diggingWorkIndex.get(machineId + "shift" + Shift.Night.getAlias() + "type" + PricingTypeEnums.Hour.getValue()).toString()) : null;
                workInfoByCubicByDay = diggingWorkIndex.get(machineId + "shift" + Shift.Early.getAlias() + "type" + PricingTypeEnums.Cube.getValue()) != null ? Integer.valueOf(diggingWorkIndex.get(machineId + "shift" + Shift.Early.getAlias() + "type" + PricingTypeEnums.Cube.getValue()).toString()) : null;
                workInfoByCubicByNight = diggingWorkIndex.get(machineId + "shift" + Shift.Night.getAlias() + "type" + PricingTypeEnums.Cube.getValue()) != null ? Integer.valueOf(diggingWorkIndex.get(machineId + "shift" + Shift.Night.getAlias() + "type" + PricingTypeEnums.Cube.getValue()).toString()) : null;
            }
            //白班计时小计
            Long workTimeByHourByDay = workInfoByHourByDay != null ? Long.parseLong(diggingInfoList.get(workInfoByHourByDay).get("workTime").toString()) : 0L;
            dayReport.setSubtotalTimerByDay(dayReport.getSubtotalTimerByDay().add(new BigDecimal((float) workTimeByHourByDay / 3600L).setScale(1, BigDecimal.ROUND_FLOOR)).subtract(dayReport.getDeductionTimeByDay()));
            Long workTimeByHourByNight = workInfoByHourByNight != null ? Long.parseLong(diggingInfoList.get(workInfoByHourByNight).get("workTime").toString()) : 0L;
            //晚班计时小计
            dayReport.setSubtotalTimerByNight(dayReport.getSubtotalTimerByNight().add(new BigDecimal((float) workTimeByHourByNight / 3600L).setScale(1, BigDecimal.ROUND_FLOOR)).subtract(dayReport.getDeductionTimeByNight()));

            //计时总工时
            dayReport.setTotalTimeByTimer(dayReport.getSubtotalTimerByDay().add(dayReport.getSubtotalTimerByNight()));
            //合计计时总工时
            total.setTotalTimeByTimer(total.getTotalTimeByTimer().add(dayReport.getTotalTimeByTimer()));

            //累计计时总工时
            dayReport.setGrandTimeByTimer(beforeReport.getGrandTimeByTimer().add(dayReport.getTotalTimeByTimer()));
            //合计累计计时总工时
            total.setGrandTimeByTimer(beforeTotal.getGrandTimeByTimer().add(total.getTotalTimeByTimer()));
            BigDecimal amountByTime = dayReport.getTotalTimeByTimer().multiply(new BigDecimal(dayReport.getPriceByTimer()));
            //计时总金额
            dayReport.setAmountByTimer(amountByTime.longValue());
            //合计计时总金额
            total.setAmountByTimer(total.getAmountByTimer() + dayReport.getAmountByTimer());
            //累计计时总金额
            dayReport.setGrandAmountByTimer(beforeReport.getGrandAmountByTimer() + dayReport.getAmountByTimer());
            //合计累计计时总金额
            total.setGrandAmountByTimer(beforeTotal.getGrandAmountByTimer() + total.getAmountByTimer());
            //计方加油量
            dayReport.setTotalGrandFillByCubic(fillCountByCubic);
            //计方加油金额
            dayReport.setTotalAmountByFillByCubic(fillAmountByCubic);
            //获取到包方作业信息索引
            Integer cubicIndex = countIndexByCubic.get(machineId) != null ? countIndexByCubic.get(machineId) : null;
            //包方总方量
            Long cubic = cubicIndex != null ? Long.parseLong(countListByCubic.get(cubicIndex).get("cubic").toString()) : 0L;
            //包方总车数
            Long count = cubicIndex != null ? Long.parseLong(countListByCubic.get(cubicIndex).get("count").toString()) : 0L;
            //白班计方小计
            Long workTimeByCubicByDay = workInfoByCubicByDay != null ? Long.parseLong(diggingInfoList.get(workInfoByCubicByDay).get("workTime").toString()) : 0L;
            dayReport.setSubtotalCubicByDay(new BigDecimal((float) workTimeByCubicByDay / 3600L).setScale(2, BigDecimal.ROUND_HALF_UP));
            //晚班计方小计
            Long workTimeByCubicByNight = workInfoByCubicByNight != null ? Long.parseLong(diggingInfoList.get(workInfoByCubicByNight).get("workTime").toString()) : 0L;
            dayReport.setSubtotalCubicByNight(new BigDecimal((float) workTimeByCubicByNight / 3600L).setScale(2, BigDecimal.ROUND_HALF_UP));
            //计方总工时
            dayReport.setTotalTimeByCubic(dayReport.getSubtotalCubicByDay().add(dayReport.getSubtotalCubicByNight()));
            //合计计方总工时
            total.setTotalTimeByCubic(total.getTotalTimeByCubic().add(dayReport.getTotalTimeByCubic()));
            //累计计方总工时
            dayReport.setGrandTimeByCubic(beforeReport.getGrandTimeByCubic().add(dayReport.getTotalTimeByCubic()));
            //合计累计计方总工时
            total.setGrandTimeByCubic(beforeTotal.getGrandTimeByCubic().add(total.getTotalTimeByCubic()));
            //计方总车数
            dayReport.setCarTotalCountByCubic(count);
            //计方总方量
            dayReport.setTotalCountByCubic(cubic);
            //白班总工时
            total.setTotalWorkTimerByDay(total.getTotalWorkTimerByDay().add(dayReport.getSubtotalTimerByDay()).add(dayReport.getSubtotalCubicByDay()));
            //晚班总工时
            total.setTotalWorkTimerByNight(total.getTotalWorkTimerByNight().add(dayReport.getSubtotalTimerByNight()).add(dayReport.getSubtotalCubicByNight()));
            //总工时
            dayReport.setTotalWorkTimer(dayReport.getTotalTimeByTimer().add(dayReport.getTotalTimeByCubic()));
            //合计总工时
            total.setTotalWorkTimer(total.getTotalWorkTimer().add(dayReport.getTotalWorkTimer()));
            //累计总工时
            dayReport.setCountTimer(beforeReport.getCountTimer().add(dayReport.getTotalWorkTimer()));
            //合计累计总工时
            total.setCountTimer(beforeTotal.getCountTimer().add(total.getTotalWorkTimer()));
            //合计计时总车数
            total.setTotalCountByTimer(total.getTotalCountByTimer() + dayReport.getTotalCountByTimer());
            //累计计时总车数
            dayReport.setGrandTotalCountByTimer(beforeReport.getGrandTotalCountByTimer() + dayReport.getTotalCountByTimer());
            //合计累计计时总车数
            total.setGrandTotalCountByTimer(beforeTotal.getGrandTotalCountByTimer() + total.getTotalCountByTimer());
            //合计计时总方量
            total.setCubicCountByTimer(total.getCubicCountByTimer() + dayReport.getCubicCountByTimer());
            //累计计时总方量
            dayReport.setGrandCubicCountByTimer(beforeReport.getGrandCubicCountByTimer() + dayReport.getCubicCountByTimer());
            //合计累计计时总方量
            total.setGrandCubicCountByTimer(beforeTotal.getGrandCubicCountByTimer() + total.getCubicCountByTimer());
            //合计计方总车数
            total.setCarTotalCountByCubic(total.getCarTotalCountByCubic() + dayReport.getCarTotalCountByCubic());
            //累计计方总车数
            dayReport.setCountCarsByCubic(beforeReport.getCountCarsByCubic() + dayReport.getCarTotalCountByCubic());
            //合计累计计方总车数
            total.setCountCarsByCubic(beforeTotal.getCountCarsByCubic() + total.getCarTotalCountByCubic());
            //合计计方总方量
            total.setTotalCountByCubic(total.getTotalCountByCubic() + dayReport.getTotalCountByCubic());
            //累计计方总方量
            dayReport.setCountCubic(beforeReport.getCountCubic() + dayReport.getTotalCountByCubic());
            //合计累计计方总方量
            total.setCountCubic(beforeTotal.getCountCubic() + total.getTotalCountByCubic());
            //包方详情
            List<Map> detail = new ArrayList<>();
            //累计包方详情
            List<Map> grandDetail = new ArrayList<>();
            //获取所有物料集合
            List<ProjectMaterial> materialList = AutoApiUtils.returnProjectMaterial().getByProjectIdOrderById(projectId);
            for (ProjectMaterial material : materialList) {
                Map map = new HashMap();
                map.put("id", material.getId());
                map.put("name", material.getName());
                map.put("count", 0L);
                map.put("amount", 0L);
                map.put("cubic", 0L);
                detail.add(map);
                grandDetail.add(map);
                if (i == 0) {
                    detailTotal.add(map);
                    grandDetailTotal.add(map);
                }
            }
            //设置包方详情
            for (int j = 0; j < detail.size(); j++) {
                Long detailMaterialId = detail.get(j).get("id") != null ? Long.parseLong(detail.get(j).get("id").toString()) : 0L;
                //详情、累计详情包方索引
                Integer detailIndex = detailIndexCubic.get(machineId.toString() + detailMaterialId) != null ? Integer.valueOf(detailIndexCubic.get(machineId.toString() + detailMaterialId).toString()) : null;
                Map detailMap = detailIndex != null ? detailList.get(detailIndex) : new HashMap();
                //总方量
                Long cubicTotal = detailMap.get("cubic") != null ? Long.parseLong(detailMap.get("cubic").toString()) : 0L;
                //总车数
                Long countTotal = detailMap.get("count") != null ? Long.parseLong(detailMap.get("count").toString()) : 0L;
                //物料编号
                Long materialId = detailMap.get("material_id") != null ? Long.parseLong(detailMap.get("material_id").toString()) : 0L;
                ProjectDiggingMachineMaterial projectMaterial = AutoApiUtils.returnProjectDiggingMachineMaterial().get(materialId);
                //总金额
                Long amount = projectMaterial != null ? projectMaterial.getPrice() * (cubicTotal / 1000000L) : 0L;
                String materialName = detail.get(j).get("name") != null ? detail.get(j).get("name").toString() : "";
                if (materialId == detailMaterialId) {
                    //详情车数
                    Long detailCount = detail.get(j).get("count") != null ? Long.parseLong(detail.get(j).get("count").toString()) : 0L;
                    //详情方量
                    Long detailCubic = detail.get(j).get("cubic") != null ? Long.parseLong(detail.get(j).get("cubic").toString()) : 0L;
                    //详情总金额
                    Long detailAmount = detail.get(j).get("amount") != null ? Long.parseLong(detail.get(j).get("amount").toString()) : 0L;
                    //设置最新的包方详情
                    Map map = new HashMap();
                    map.put("id", materialId);
                    map.put("name", materialName);
                    map.put("count", countTotal + detailCount);
                    map.put("cubic", cubicTotal + detailCubic);
                    map.put("amount", amount + detailAmount);
                    detail.remove(j);
                    detail.add(map);
                    //设置最新的合计包方详情
                    for (int z = 0; z < detailTotal.size(); z++) {
                        Long detailTotalMaterialId = detailTotal.get(z).get("id") != null ? Long.parseLong(detailTotal.get(z).get("id").toString()) : 0L;
                        if (detailTotalMaterialId == detailMaterialId) {
                            Long cubicT = detailTotal.get(z).get("cubic") != null ? Long.parseLong(detailTotal.get(z).get("cubic").toString()) : 0L;
                            Long countT = detailTotal.get(z).get("count") != null ? Long.parseLong(detailTotal.get(z).get("count").toString()) : 0L;
                            Long amountT = detailTotal.get(z).get("amount") != null ? Long.parseLong(detailTotal.get(z).get("amount").toString()) : 0L;
                            Map mapT = new HashMap();
                            mapT.put("id", materialId);
                            mapT.put("name", materialName);
                            mapT.put("count", countTotal + countT);
                            mapT.put("cubic", cubicTotal + cubicT);
                            mapT.put("amount", amount + amountT);
                            detailTotal.remove(z);
                            detailTotal.add(mapT);
                            break;
                        }
                    }
                    if (projectMaterial != null) {
                        //设置包方总金额
                        dayReport.setTotalAmountByCubic(dayReport.getTotalAmountByCubic() + amount);
                    }
                    break;
                }
            }
            for (int j = 0; j < grandDetail.size(); j++) {
                Long detailMaterialId = grandDetail.get(j).get("id") != null ? Long.parseLong(grandDetail.get(j).get("id").toString()) : 0L;
                //详情、累计详情包方索引
                Integer detailIndex = detailIndexCubic.get(machineId.toString() + detailMaterialId) != null ? Integer.valueOf(detailIndexCubic.get(machineId.toString() + detailMaterialId).toString()) : null;
                Map grandDetailMap = detailIndex != null ? grandDetailList.get(detailIndex) : new HashMap();
                //总方量
                Long grandCubic = grandDetailMap.get("cubic") != null ? Long.parseLong(grandDetailMap.get("cubic").toString()) : 0L;
                //总车数
                Long grandCount = grandDetailMap.get("count") != null ? Long.parseLong(grandDetailMap.get("count").toString()) : 0L;
                //物料编号
                Long materialId = grandDetailMap.get("material_id") != null ? Long.parseLong(grandDetailMap.get("material_id").toString()) : 0L;
                ProjectDiggingMachineMaterial projectMaterial = AutoApiUtils.returnProjectDiggingMachineMaterial().get(materialId);
                //总金额
                Long grandAmount = projectMaterial != null ? projectMaterial.getPrice() * (grandCubic / 1000000L) : 0L;
                String materialName = grandDetail.get(j).get("name") != null ? grandDetail.get(j).get("name").toString() : "";
                if (materialId == detailMaterialId) {
                    //详情车数
                    Long detailCount = grandDetail.get(j).get("count") != null ? Long.parseLong(grandDetail.get(j).get("count").toString()) : 0L;
                    //详情方量
                    Long detailCubic = grandDetail.get(j).get("cubic") != null ? Long.parseLong(grandDetail.get(j).get("cubic").toString()) : 0L;
                    //详情总金额
                    Long detailAmount = grandDetail.get(j).get("amount") != null ? Long.parseLong(grandDetail.get(j).get("amount").toString()) : 0L;
                    //设置最新的包方详情
                    Map map = new HashMap();
                    map.put("id", materialId);
                    map.put("name", materialName);
                    map.put("count", grandCount + detailCount);
                    map.put("cubic", grandCubic + detailCubic);
                    map.put("amount", grandAmount + detailAmount);
                    grandDetail.remove(j);
                    grandDetail.add(map);
                    for (int z = 0; z < grandDetailTotal.size(); z++) {
                        Long detailTotalMaterialId = grandDetailTotal.get(z).get("id") != null ? Long.parseLong(grandDetailTotal.get(z).get("id").toString()) : 0L;
                        if (detailTotalMaterialId == detailMaterialId) {
                            //设置最新的合计包方详情
                            Long cubicT = grandDetailTotal.get(z).get("cubic") != null ? Long.parseLong(grandDetailTotal.get(z).get("cubic").toString()) : 0L;
                            Long countT = grandDetailTotal.get(z).get("count") != null ? Long.parseLong(grandDetailTotal.get(z).get("count").toString()) : 0L;
                            Long amountT = grandDetailTotal.get(z).get("amount") != null ? Long.parseLong(grandDetailTotal.get(z).get("amount").toString()) : 0L;
                            Map mapT = new HashMap();
                            mapT.put("id", materialId);
                            mapT.put("name", materialName);
                            mapT.put("count", grandCount + detailCount + countT);
                            mapT.put("cubic", grandCubic + detailCubic + cubicT);
                            mapT.put("amount", grandAmount + detailAmount + amountT);
                            grandDetailTotal.remove(z);
                            grandDetailTotal.add(mapT);
                            break;
                        }
                    }
                    break;
                }
            }
            //合计包方总金额
            total.setTotalAmountByCubic(total.getTotalAmountByCubic() + dayReport.getTotalAmountByCubic());
            //累计包方总金额
            dayReport.setCountAmountByCubic(beforeReport.getCountAmountByCubic() + dayReport.getTotalAmountByCubic());
            //合计累计包方总金额
            total.setCountAmountByCubic(beforeTotal.getCountAmountByCubic() + total.getTotalAmountByCubic());
            //设置包方详情字符串
            if (detail != null)
                dayReport.setCubicDetail(JSON.toJSONString(detail));
            //累计包方详情
            dayReport.setGrandCubicDetail(JSON.toJSONString(grandDetail));
            //总金额
            dayReport.setTotalAmount(dayReport.getAmountByTimer() + dayReport.getTotalAmountByCubic());
            //合计总金额
            total.setTotalAmount(total.getTotalAmount() + dayReport.getTotalAmount());
            //累计总金额
            dayReport.setGrandWorkAmount(beforeReport.getGrandWorkAmount() + dayReport.getTotalAmount());
            //合计累计总金额
            total.setGrandWorkAmount(beforeTotal.getGrandWorkAmount() + total.getTotalAmount());
            //当天加油总量
            dayReport.setTotalGrandFill(dayReport.getTotalGrandFillByTimer() + dayReport.getTotalGrandFillByCubic());
            //当天加油总金额
            dayReport.setTotalAmountByFill(dayReport.getTotalAmountByFillByTimer() + dayReport.getTotalAmountByFillByCubic());
            //合计白班加油量
            total.setTotalGrandFillByTimer(total.getTotalGrandFillByTimer() + dayReport.getTotalGrandFillByTimer());
            //合计晚班加油量
            total.setTotalGrandFillByCubic(total.getTotalGrandFillByCubic() + dayReport.getTotalGrandFillByCubic());
            //合计总加油量
            total.setTotalGrandFill(total.getTotalGrandFill() + dayReport.getTotalGrandFill());
            //累计计时加油量
            dayReport.setGrandTotalGrandFillByTimer(beforeReport.getGrandTotalGrandFillByTimer() + dayReport.getTotalGrandFillByTimer());
            //累计计方加油量
            dayReport.setGrandTotalGrandFillByCubic(beforeReport.getGrandTotalGrandFillByCubic() + dayReport.getTotalGrandFillByCubic());
            //累计加油量
            dayReport.setGrandTotalGrandFill(beforeReport.getGrandTotalGrandFill() + dayReport.getTotalGrandFill());
            //合计累计计时加油量
            total.setGrandTotalGrandFillByTimer(beforeTotal.getGrandTotalGrandFillByTimer() + total.getTotalGrandFillByTimer());
            //合计累计计方
            total.setGrandTotalGrandFillByCubic(beforeTotal.getGrandTotalGrandFillByCubic() + total.getTotalGrandFillByCubic());
            //合计累计加油量
            total.setGrandTotalGrandFill(beforeTotal.getGrandTotalGrandFill() + total.getTotalGrandFill());
            //合计计时加油金额
            total.setTotalAmountByFillByTimer(total.getTotalAmountByFillByTimer() + dayReport.getTotalAmountByFillByTimer());
            //合计计方加油金额
            total.setTotalAmountByFillByCubic(total.getTotalAmountByFillByCubic() + dayReport.getTotalAmountByFillByCubic());
            //合计加油金额
            total.setTotalAmountByFill(total.getTotalAmountByFill() + dayReport.getTotalAmountByFill());
            //累计计时加油金额
            dayReport.setGrandTotalAmountByFillByTimer(beforeReport.getGrandTotalAmountByFillByTimer() + dayReport.getTotalAmountByFillByTimer());
            //累计计方加油金额
            dayReport.setGrandTotalAmountByFillByCubic(beforeReport.getGrandTotalAmountByFillByCubic() + dayReport.getTotalAmountByFillByCubic());
            //累计加油金额
            dayReport.setGrandTotalAmountByFill(beforeReport.getGrandTotalAmountByFill() + dayReport.getTotalAmountByFill());
            //合计累计计时加油金额
            total.setGrandTotalAmountByFillByTimer(beforeTotal.getGrandTotalAmountByFillByTimer() + dayReport.getTotalAmountByFillByTimer());
            //合计累计计方加油金额
            total.setGrandTotalGrandFillByCubic(beforeTotal.getGrandTotalAmountByFillByCubic() + dayReport.getTotalAmountByFillByCubic());
            //合计累计加油金额
            total.setGrandTotalAmountByFill(beforeTotal.getGrandTotalAmountByFill() + total.getTotalAmountByFill());
            //结余金额
            dayReport.setShouldPayAmount(dayReport.getTotalAmount() - dayReport.getTotalAmountByFill());
            //合计结余金额
            total.setShouldPayAmount(total.getShouldPayAmount() + dayReport.getShouldPayAmount());
            //累计结余金额
            dayReport.setGrandShouldPayAmount(beforeReport.getGrandShouldPayAmount() + dayReport.getShouldPayAmount());
            //合计累计结余金额
            total.setGrandShouldPayAmount(beforeTotal.getGrandShouldPayAmount() + total.getShouldPayAmount());
            //平均油耗
            if (dayReport.getTotalWorkTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal avgUseFill = new BigDecimal((float) dayReport.getTotalGrandFill() / 1000L).divide(dayReport.getTotalWorkTimer(), 2, BigDecimal.ROUND_HALF_UP);
                dayReport.setAvgUseFill(avgUseFill);
            }
            //合计平均油耗
            if (total.getTotalWorkTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal avgUseFillT = new BigDecimal((float) total.getTotalGrandFill() / 1000L).divide(total.getTotalWorkTimer(), 2, BigDecimal.ROUND_HALF_UP);
                total.setAvgUseFill(avgUseFillT);
            }
            //计时平均车辆
            //计时平均车数
            BigDecimal avgCarByTimer = dayReport.getTotalTimeByTimer().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(countTotalByHour).divide(dayReport.getTotalTimeByTimer(), 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            dayReport.setAvgCarByTimer(avgCarByTimer);
            //合计计时平均车数
            BigDecimal avgCarByTimerT = total.getTotalTimeByTimer().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(total.getTotalCountByTimer()).divide(total.getTotalTimeByTimer(), 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            total.setAvgCarByTimer(avgCarByTimerT);
            //平均车辆
            if (dayReport.getTotalWorkTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal avgCar = new BigDecimal((float) (dayReport.getCarTotalCountByCubic() + dayReport.getTotalCountByTimer())).divide(dayReport.getTotalWorkTimer(), 2, BigDecimal.ROUND_HALF_UP);
                dayReport.setAvgCar(avgCar);
            }
            //合计平均车辆
            if (total.getTotalWorkTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal avgCarT = new BigDecimal((float) (total.getCarTotalCountByCubic() + total.getTotalCountByTimer())).divide(total.getTotalWorkTimer(), 2, BigDecimal.ROUND_HALF_UP);
                total.setAvgCar(avgCarT);
            }
            if (dayReport.getCarTotalCountByCubic() != 0) {
                //平均方数
                BigDecimal avgCubics = new BigDecimal((float) (dayReport.getTotalCountByCubic() / 1000000L) / dayReport.getCarTotalCountByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                dayReport.setAvgCubics(avgCubics);
                //平均价格
                BigDecimal avgAmount = new BigDecimal((float) (dayReport.getShouldPayAmount() / 100L) / dayReport.getCarTotalCountByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                dayReport.setAvgAmount(avgAmount);
            }
            if (total.getCarTotalCountByCubic() != 0) {
                //合计平均方数
                BigDecimal avgCubicsT = new BigDecimal((float) (total.getTotalCountByCubic() / 1000000L) / total.getCarTotalCountByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                total.setAvgCubics(avgCubicsT);
                //合计平均价格
                BigDecimal avgAmountT = new BigDecimal((float) (total.getShouldPayAmount() / 100L) / total.getCarTotalCountByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                total.setAvgAmount(avgAmountT);
            }
            //油耗
            if (dayReport.getTotalAmount() != 0) {
                BigDecimal oilConsumption = new BigDecimal(((float) dayReport.getTotalAmountByFill()) / dayReport.getTotalAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
                dayReport.setOilConsumption(oilConsumption);
            }
            //合计油耗
            if (total.getTotalAmount() != 0) {
                BigDecimal oilConsumptionT = new BigDecimal(((float) total.getTotalAmountByFill()) / total.getTotalAmount()).setScale(4, BigDecimal.ROUND_HALF_UP);
                total.setOilConsumption(oilConsumptionT);
            }
            //累计平均油耗
            if (dayReport.getCountTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal grandAvgUseFill = new BigDecimal((float) dayReport.getTotalGrandFill() / 1000L).divide(dayReport.getCountTimer(), 2, BigDecimal.ROUND_HALF_UP);
                dayReport.setGrandAvgUseFill(grandAvgUseFill);
            }
            //累计合计平均油耗
            if (total.getCountTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal grandAvgUseFillT = new BigDecimal((float) total.getTotalGrandFill() / 1000L).divide(total.getCountTimer(), 2, BigDecimal.ROUND_HALF_UP);
                total.setGrandAvgUseFill(grandAvgUseFillT);
            }
            //累计计时平均车辆
            BigDecimal grandAvgCarByTimer = dayReport.getGrandTimeByTimer().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(dayReport.getGrandTotalCountByTimer()).divide(dayReport.getGrandTimeByTimer(), 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            dayReport.setGrandAvgCarByTimer(grandAvgCarByTimer);
            //累计合计平均车辆
            BigDecimal grandAvgCarByTimerT = total.getGrandTimeByTimer().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(total.getGrandTotalCountByTimer()).divide(total.getGrandTimeByTimer(), 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            total.setGrandAvgCarByTimer(grandAvgCarByTimerT);
            //累计平均车辆
            if (dayReport.getCountTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal grandAvgCarT = new BigDecimal((float) (dayReport.getCountCarsByCubic() + dayReport.getGrandTotalCountByTimer())).divide(dayReport.getCountTimer(), 2, BigDecimal.ROUND_HALF_UP);
                dayReport.setGrandAvgCar(grandAvgCarT);
            }
            //累计合计平均车辆
            if (total.getCountTimer().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal grandAvgCarT = new BigDecimal((float) (total.getCountCarsByCubic() + total.getGrandTotalCountByTimer())).divide(total.getCountTimer(), 2, BigDecimal.ROUND_HALF_UP);
                total.setGrandAvgCar(grandAvgCarT);
            }
            if (dayReport.getCountCarsByCubic() != 0) {
                //平均方数
                BigDecimal grandAvgCubics = new BigDecimal((float) (dayReport.getCountCubic() / 1000000L) / dayReport.getCountCarsByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                dayReport.setGrandAvgCubics(grandAvgCubics);
                //平均价格
                BigDecimal grandAvgAmount = new BigDecimal((float) (dayReport.getGrandShouldPayAmount() / 100L) / dayReport.getCountCarsByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                dayReport.setGrandAvgAmount(grandAvgAmount);
            }
            if (total.getCountCarsByCubic() != 0) {
                //平均方数
                BigDecimal grandAvgCubicsT = new BigDecimal((float) (total.getCountCubic() / 1000000L) / total.getCountCarsByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                total.setGrandAvgCubics(grandAvgCubicsT);
                //平均价格
                BigDecimal grandAvgAmountT = new BigDecimal((float) (total.getGrandShouldPayAmount() / 100L) / total.getCountCarsByCubic()).setScale(2, BigDecimal.ROUND_HALF_UP);
                total.setGrandAvgAmount(grandAvgAmountT);
            }
            if (dayReport.getCountAmountByCubic() != 0) {
                BigDecimal grandOilConsumption = new BigDecimal(((float) dayReport.getGrandTotalAmountByFill()) / dayReport.getCountAmountByCubic()).setScale(4, BigDecimal.ROUND_HALF_UP);
                dayReport.setGrandOilConsumption(grandOilConsumption);
            }
            if (total.getCountAmountByCubic() != 0) {
                BigDecimal grandOilConsumptionT = new BigDecimal(((float) total.getGrandTotalAmountByFill()) / total.getCountAmountByCubic()).setScale(4, BigDecimal.ROUND_HALF_UP);
                total.setGrandOilConsumption(grandOilConsumptionT);
            }
            dayReport.setReportDate(reportDate);
            reportList.add(dayReport);
            //AutoApiUtils.returnProjectDiggingDayReport().save(dayReport);
        }
        AutoApiUtils.returnProjectDiggingDayReport().batchSave(reportList);
        //合计包方详情字符串
        total.setCubicDetail(JSON.toJSONString(detailTotal));
        //累计合计包方详情
        total.setGrandCubicDetail(JSON.toJSONString(grandDetailTotal));


        //后期直接添加 挖机历史数据
        reportDate = DateUtils.getEndDateByNow(reportDate);
        AutoApiUtils.returnProjectDiggingDayReportHistory().deleteByProjectIdAndReportDate(projectId, reportDate);

        ProjectDiggingDayReportHistory history = new ProjectDiggingDayReportHistory();
        history.setProjectId(projectId);
        //查询所有物料的单价
        List<ProjectDiggingMachineMaterial> machineMaterialList = AutoApiUtils.returnProjectDiggingMachineMaterial().getByProjectIdOrderById(projectId);
        //生成物料索引
        Map<Long, Integer> materialIndex = new HashMap<>();
        for (int i = 0; i < machineMaterialList.size(); i++) {
            materialIndex.put(machineMaterialList.get(i).getMaterialId(), i);
        }

        //查询所有挖机的信息
        List<ProjectDiggingMachine> projectDiggingMachineList = AutoApiUtils.returnProjectDiggingMachine().getByProjectIdOrderById(projectId);
        //生成挖机索引
        Map<String, Integer> machineIndex = new HashMap<>();
        for (int i = 0; i < projectDiggingMachineList.size(); i++) {
            machineIndex.put(projectDiggingMachineList.get(i).getCode(), i);
        }

        //查询所有挖机对应的包时金额详情
        List<ProjectHourPrice> projectHourPriceList = AutoApiUtils.returnProjectHourPrice().getAllByProjectId(projectId);
        //生成包时金额详情索引
        Map<String, Integer> hourIndex = new HashMap<>();
        for (int i = 0; i < projectHourPriceList.size(); i++) {
            hourIndex.put(projectHourPriceList.get(i).getBrandId().toString() + projectHourPriceList.get(i).getModelId().toString() + projectHourPriceList.get(i).getCarType().getValue(), i);
        }

        //获取所有挖机的工作信息
        List<Map> workInfoList = AutoApiUtils.returnProjectCarWork().getTotalCubicAndCountByProjectIdAndDateIdentification(projectId, reportDate);
        //查询所有挖机的工作信息
        List<Map> workTimeListHistory = AutoApiUtils.returnProjectWorkTimeByDigging().getTotalTimeAndPricingTypeByProjectIdAndDate(projectId, reportDate);
        //生成工作信息索引
        Map<String, Integer> workTimeIndex = new HashMap<>();
        for (int i = 0; i < workTimeListHistory.size(); i++) {
            String machineCode = workTimeListHistory.get(i).get("material_code").toString();
            Integer pricingType = Integer.valueOf(workTimeListHistory.get(i).get("pricing_type_enums").toString());
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
                workTime = Long.parseLong(workTimeListHistory.get(timeIndex).get("workTime").toString());
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
        List<Map> fillInfoMap = AutoApiUtils.returnProjectCarFill().getAllByProjectIdAndDateAndCarType(projectId, reportDate);
        for (int i = 0; i < fillInfoMap.size(); i++) {
            Integer pricingType = fillInfoMap.get(i).get("pricing_type_enums") != null ? Integer.valueOf(fillInfoMap.get(i).get("pricing_type_enums").toString()) : 0;
            //总加油量
            Long totalFill = Long.parseLong(fillInfoMap.get(i).get("volumn").toString());
            //总加油金额
            Long fillAmount = Long.parseLong(fillInfoMap.get(i).get("amount").toString());
            if (pricingType == PricingTypeEnums.Hour.getValue()) {
                history.setTotalFillByTimer(totalFill);
                history.setTotalFillAmountByTimer(fillAmount);
            } else if (pricingType == PricingTypeEnums.Cube.getValue()) {
                history.setTotalFillByCubic(totalFill);
                history.setTotalFillAmountByCubic(fillAmount);
            }
        }
        history.setTotalFill(history.getTotalFillByTimer() + history.getTotalFillByCubic());
        history.setTotalFillAmount(history.getTotalFillAmountByTimer() + history.getTotalFillAmountByCubic());
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
        AutoApiUtils.returnProjectDiggingDayReportHistory().save(history);
        AutoApiUtils.returnProjectDiggingDayReportTotal().save(total);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.DiggingDayReport);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    /**
     * todo 其它设备工作信息统计
     * @param projectId
     * @param reportDate
     * @param carType
     * @param cron
     * @throws IOException
     */
    public static void scheduleOtherDeviceReport(Long projectId, Date reportDate, Integer carType, String cron) throws IOException {
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
        Date workStart = dateMap.get("start");
        Date workEnd = dateMap.get("end");
        //获取到统计日期
        reportDate = DateUtils.convertDate(reportDate.getTime());
        List<Map> reportList = AutoApiUtils.returnProjectOtherDeviceWorkInfoServiceI().getDayReportByProjectIdAndDateIdentificationAndCarType(projectId, reportDate, carType);

    }

    /**
     * 挖机包方详情表
     *
     * @param projectId
     * @param machineId
     * @param reportDate
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void scheduleCubicDetail(Long projectId, Long machineId, Date reportDate) throws IOException {
        //获取到统计日期
        reportDate = DateUtils.convertDate(reportDate.getTime());
        //获取到当月1号的日期
        Date startDay = DateUtils.getStartDate(reportDate);
        //获取到当月最后一天的日期
        Date end = DateUtils.getEndDate(reportDate);
        reportDate = DateUtils.createReportDateByMonth(end);
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, startDay);
        //当月月初早班开始时间
        Date startBegin = dateMap.get("start");
        Map<String, Date> dateMap1 = AutoApiUtils.returnWorkDate().getWorkTime(projectId, end);
        //当月月底晚班结束时间
        Date endEnd = dateMap1.get("end");
        //获取到对应挖机当月的工作时间信息的日期
        List<Map> projectWorkTimeByDiggingList = AutoApiUtils.returnProjectWorkTimeByDigging().getAllByProjectIdAndStartTimeAndEndTimeAndMaterialId(projectId, machineId, startBegin, endEnd);
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < projectWorkTimeByDiggingList.size(); i++) {
            String workDateStr = projectWorkTimeByDiggingList.get(i).get("workday").toString();
            /*Date oneDate = DateUtils.stringFormatDate(workDateStr, SmartminingConstant.YEARMONTHDAUFORMAT);
            Date workDate = DateUtils.createReportDateByMonth(oneDate);*/
            dateList.add(workDateStr);
        }
        //获取到对应挖机当月的工作信息
        List<Map> workInfoListByDigging = null;
        if (dateList.size() > 0)
            workInfoListByDigging = AutoApiUtils.returnProjectCarWork().getCubicDetailByProjectIdAndDiggingMachineIdAndTime(projectId, machineId, dateList, 2);
        if (workInfoListByDigging != null) {
            AutoApiUtils.returnProjectCubicDetailTotal().deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
            AutoApiUtils.returnProjectCubicDetail().deleteByProjectIdAndCreateDateAndMachineId(projectId, reportDate, machineId);
            ProjectDiggingMachine machine = AutoApiUtils.returnProjectDiggingMachine().get(machineId);
            ProjectCubicDetailTotal detailTotal = new ProjectCubicDetailTotal();
            detailTotal.setProjectId(projectId);
            detailTotal.setReportDate(reportDate);
            detailTotal.setMachineId(machineId);
            detailTotal.setOwnerName(machine.getOwnerName());
            detailTotal = AutoApiUtils.returnProjectCubicDetailTotal().save(detailTotal);
            List<Map> elseList = AutoApiUtils.returnProjectCarWork().getElseTotalByProjectIdAndDiggingMachineIdAndTime(projectId, machineId, dateList, 2);
            Long cubicCount = 0L;
            Long carsCount = 0L;
            if (elseList.size() > 0) {
                for (int i = 0; i < elseList.size(); i++) {
                    if (elseList.get(i).get("cubic") != null) {
                        cubicCount = cubicCount + Long.parseLong(elseList.get(i).get("cubic").toString());
                    }
                    if (elseList.get(i).get("count") != null) {
                        carsCount = carsCount + Long.parseLong(elseList.get(i).get("count").toString());
                    }
                }
            }
            Long carsTemp = 0L;
            Long cubicTemp = 0L;
            Long amountT = 0L;
            Long fillT = 0L;
            Long amountByFillT = 0L;
            Long shouldPayT = 0L;
            for (int i = 0; i < workInfoListByDigging.size(); i++) {
                ProjectCubicDetail detail = new ProjectCubicDetail();
                detail.setTotalId(detailTotal.getId());
                detail.setProjectId(projectId);
                detail.setMachineId(machineId);
                Long carId = 0L;
                if (workInfoListByDigging.get(i).get("car_id") != null) {
                    carId = Long.parseLong(workInfoListByDigging.get(i).get("car_id").toString());
                    detail.setCarId(carId);
                    ProjectCar car = AutoApiUtils.returnProjectCar().get(carId);
                    detail.setCapacity(car.getModifyCapacity());
                }
                if (workInfoListByDigging.get(i).get("car_code") != null) {
                    detail.setCarCode(workInfoListByDigging.get(i).get("car_code").toString());
                }
                Long price = 0L;
                if (workInfoListByDigging.get(i).get("material_id") != null) {
                    Long materialId = Long.parseLong(workInfoListByDigging.get(i).get("material_id").toString());
                    detail.setMaterialId(materialId);
                    ProjectMaterial material = AutoApiUtils.returnProjectMaterial().get(materialId);
                    if (material != null) {
                        detail.setMaterialName(material.getName());
                    }
                    //根据物料编号查询到对应的对象
                    ProjectDiggingMachineMaterial projectDiggingMachineMaterial = AutoApiUtils.returnProjectDiggingMachineMaterial().getByProjectIdAndMaterialId(projectId, materialId);
                    if (projectDiggingMachineMaterial != null) {
                        price = projectDiggingMachineMaterial.getPrice();
                    }
                }
                if (workInfoListByDigging.get(i).get("count") != null) {
                    detail.setCars(Long.parseLong(workInfoListByDigging.get(i).get("count").toString()));
                }
                if (workInfoListByDigging.get(i).get("cubic") != null) {
                    detail.setCubics(((BigDecimal) workInfoListByDigging.get(i).get("cubic")).longValue());
                }
                Date beginDate = null;
                Date endDate = null;
                Date date = null;
                if (workInfoListByDigging.get(i).get("date_identification") != null) {
                    date = DateUtils.stringFormatDate(workInfoListByDigging.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                    date = DateUtils.createReportDateByMonth(date);
                    detail.setReportDate(DateUtils.createReportDateByMonth(date));
                    Map<String, Date> dateMap2 = AutoApiUtils.returnWorkDate().getWorkTime(projectId, date);
                    beginDate = dateMap2.get("start");
                    endDate = dateMap2.get("end");
                }
                detail.setCreateDate(reportDate);

                ProjectCubicDetailElse detailElse = new ProjectCubicDetailElse();
                detailElse.setTotalId(detailTotal.getId());
                detailElse.setCarsByTotal(carsCount + detailElse.getCarsByTemp());
                detailElse.setCubicByTotal(cubicCount + detailElse.getCubicByTemp());
                detailElse.setMachineId(machineId);
                detailElse.setPrice(price);
                detailElse.setAmount(price * detailElse.getCubicByTotal() / 1000000);
                detailElse.setCreateDate(reportDate);
                detailElse.setReportDate(date);
                amountT = amountT + detailElse.getAmount();
                Map fillMap = AutoApiUtils.returnProjectCarFill().getCarFillByProjectIdAAndCarIdAndTime(projectId, carId, beginDate, endDate);
                if (fillMap.get("totalFill") != null) {
                    detailElse.setOilCount(Long.parseLong(fillMap.get("totalFill").toString()));
                }
                fillT = fillT + detailElse.getOilCount();
                if (fillMap.get("totalAmount") != null) {
                    detailElse.setAmountByOil(Long.parseLong(fillMap.get("totalAmount").toString()));
                }
                amountByFillT = amountByFillT + detailElse.getAmountByOil();
                detail.setAmountByShould(detailElse.getAmount() - detailElse.getAmountByOil());
                shouldPayT = shouldPayT + detail.getAmountByShould();
                detail = AutoApiUtils.returnProjectCubicDetail().save(detail);
                detailElse.setProjectId(projectId);
                detailElse.setDetailId(detail.getId());
                carsTemp = carsTemp + detailElse.getCarsByTemp();
                cubicTemp = cubicTemp + detailElse.getCubicByTemp();
                AutoApiUtils.returnProjectCubicDetailElse().save(detailElse);
            }

            List<Map> totalMap = AutoApiUtils.returnProjectCarWork().getDetailTotalByProjectIdAndMachineIdAndTime(projectId, machineId, startDay, end, 2);
            List<Map> list = new ArrayList<>();
            if (totalMap != null) {
                Long carsCountT = 0L;
                Long cubicCountT = 0L;
                for (int i = 0; i < totalMap.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    if (totalMap.get(i).get("car_id") != null) {
                        map.put("carId", totalMap.get(i).get("car_id"));
                    }
                    if (totalMap.get(i).get("count") != null) {
                        map.put("count", totalMap.get(i).get("count"));
                        carsCountT = carsCountT + Long.parseLong(totalMap.get(i).get("count").toString());
                    }
                    if (totalMap.get(i).get("cubic") != null) {
                        map.put("cubic", totalMap.get(i).get("cubic"));
                        cubicCountT = cubicCountT + Long.parseLong(totalMap.get(i).get("cubic").toString());
                    }
                    list.add(map);
                }
                String json = JSON.toJSONString(list);
                detailTotal.setTotalJson(json);
                detailTotal.setCarsByTotal(carsTemp + carsCountT);
                detailTotal.setCubicByTotal(cubicTemp + cubicCountT);
            }
            detailTotal.setCarsByTemp(carsTemp);
            detailTotal.setCubicByTemp(cubicTemp);
            detailTotal.setAmount(amountT);
            detailTotal.setOilCount(fillT);
            detailTotal.setAmountByOil(amountByFillT);
            detailTotal.setAmountByShould(shouldPayT);
            AutoApiUtils.returnProjectCubicDetailTotal().save(detailTotal);
        }
    }

    @SuppressWarnings("unchecked")
    public static void schedulePartCountByDigging(Long projectId, Date reportDate, Long machineId) throws
            IOException {
        //获取到统计日期
        reportDate = DateUtils.convertDate(reportDate.getTime());
        //获取到当月1号的日期
        Date startDay = DateUtils.getStartDate(reportDate);
        //获取到当月最后一天的日期
        Date end = DateUtils.getEndDate(reportDate);
        reportDate = DateUtils.createReportDateByMonth(end);
        AutoApiUtils.returnProjectDiggingPartCount().deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
        AutoApiUtils.returnProjectDiggingPartCountTotal().deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
        AutoApiUtils.returnProjectDiggingPartCountGrand().deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
        //项目对象
        Project project = AutoApiUtils.returnProject().get(projectId);
        //挖机对象
        ProjectDiggingMachine machine = AutoApiUtils.returnProjectDiggingMachine().get(machineId);
        //合计对象
        ProjectDiggingPartCountTotal total = new ProjectDiggingPartCountTotal();
        total.setMachineId(machineId);
        total.setProjectId(projectId);
        total.setReportDate(reportDate);
        total.setPriceByOil(project.getOilPirce());
        total.setMachineCode(machine.getCode());
        total.setOwnerName(machine.getOwnerName());
        total = AutoApiUtils.returnProjectDiggingPartCountTotal().save(total);
        //累计对象
        ProjectDiggingPartCountGrand grand = new ProjectDiggingPartCountGrand();
        grand.setTotalId(total.getId());
        grand.setMachineId(machineId);
        grand.setProjectId(projectId);
        grand.setMachineCode(machine.getCode());
        //获取当月对应挖机的工作信息
        List<Map> toatlList = AutoApiUtils.returnProjectDiggingDayReport().getCubicDetailByProjectIdAndReportDateAndMachineId(projectId, startDay, end, machineId);
        for (int i = 0; i < toatlList.size(); i++) {
            //包时总时间
            BigDecimal totalTimeByTimer = toatlList.get(i).get("totalTimeByTimer") != null ? new BigDecimal(toatlList.get(i).get("totalTimeByTimer").toString()) : new BigDecimal(0L);
            //包时单价
            Long priceByTimer = toatlList.get(i).get("price_by_timer") != null ? Long.parseLong(toatlList.get(i).get("price_by_timer").toString()) : 0L;
            //包时金额
            Long amountByTimer = toatlList.get(i).get("amountByTimer") != null ? Long.parseLong(toatlList.get(i).get("amountByTimer").toString()) : 0L;
            //包方总时间
            BigDecimal totalTimeByCubic = toatlList.get(i).get("totalTimeByCubic") != null ? new BigDecimal(toatlList.get(i).get("totalTimeByCubic").toString()) : new BigDecimal(0L);
            //包方总金额
            Long totalAmountByCubic = toatlList.get(i).get("totalAmountByCubic") != null ? Long.parseLong(toatlList.get(i).get("totalAmountByCubic").toString()) : 0L;
            //总金额
            Long totalAmount = toatlList.get(i).get("totalAmount") != null ? Long.parseLong(toatlList.get(i).get("totalAmount").toString()) : 0L;
            //总加油量
            Long totalGrandFill = toatlList.get(i).get("totalGrandFill") != null ? Long.parseLong(toatlList.get(i).get("totalGrandFill").toString()) : 0L;
            //加油总金额
            Long totalAmountByFill = toatlList.get(i).get("totalAmountByFill") != null ? Long.parseLong(toatlList.get(i).get("totalAmountByFill").toString()) : 0L;
            //统计时间
            Date reportDay = toatlList.get(i).get("report_date") != null ? DateUtils.stringFormatDate(toatlList.get(i).get("report_date").toString(), SmartminingConstant.DATEFORMAT) : null;
            //计时及包方详情对象
            ProjectDiggingPartCount partCount = new ProjectDiggingPartCount();
            partCount.setTotalId(total.getId());
            partCount.setMachineId(machineId);
            partCount.setMachineCode(machine.getCode());
            partCount.setProjectId(projectId);
            partCount.setWorkTimeByTimer(totalTimeByTimer);
            partCount.setWorkTimeByCubic(totalTimeByCubic);
            partCount.setPriceByTimer(priceByTimer);
            partCount.setAmountByTimer(amountByTimer);
            partCount.setAmountByCubic(totalAmountByCubic);
            partCount.setAmountByCount(totalAmount);
            partCount.setOilCount(totalGrandFill);
            partCount.setPriceByOil(project.getOilPirce());
            partCount.setAmountByOil(totalAmountByFill);
            partCount.setCreateDate(reportDay);
            partCount.setReportDate(reportDate);
            BigDecimal oilConsumption = partCount.getOilCount() != 0 ? (partCount.getWorkTimeByTimer().add(partCount.getWorkTimeByCubic()).divide(new BigDecimal(partCount.getOilCount()), 2, BigDecimal.ROUND_HALF_UP)) : new BigDecimal(0);
            partCount.setOilConsumption(oilConsumption);
            partCount.setBalance(partCount.getAmountByCount() - partCount.getAmountByOil() - partCount.getRent() - partCount.getAmountByMeals() + partCount.getSubsidyAmount());
            total.setWorkTimeByTimer(total.getWorkTimeByTimer().add(partCount.getWorkTimeByTimer()));
            total.setWorkTimeByCubic(total.getWorkTimeByCubic().add(partCount.getWorkTimeByCubic()));
            total.setPriceByTimer(priceByTimer);
            total.setAmountByTimer(total.getAmountByTimer() + partCount.getAmountByTimer());
            total.setAmountByCubic(total.getAmountByCubic() + partCount.getAmountByCubic());
            total.setAmountByCount(total.getAmountByCount() + partCount.getAmountByCount());
            total.setOilCount(total.getOilCount() + partCount.getOilCount());
            total.setPriceByOil(project.getOilPirce());
            total.setAmountByOil(total.getAmountByOil() + partCount.getAmountByOil());
            BigDecimal oilConsumptionByTotal = total.getOilCount() != 0 ? (total.getWorkTimeByTimer().add(total.getWorkTimeByCubic())).divide(new BigDecimal(total.getOilCount()), 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            total.setOilConsumption(oilConsumptionByTotal);
            total.setRent(total.getRent() + partCount.getRent());
            total.setAmountByMeals(total.getAmountByMeals() + partCount.getAmountByMeals());
            total.setSubsidyAmount(total.getSubsidyAmount() + partCount.getSubsidyAmount());
            total.setBalance(total.getBalance() + partCount.getBalance());
            AutoApiUtils.returnProjectDiggingPartCount().save(partCount);
        }
        total.setReportDate(reportDate);
        grand.setWorkTime(total.getWorkTimeByTimer().add(total.getWorkTimeByCubic()));
        grand.setPriceByTimer(total.getPriceByTimer());
        grand.setAmountByTimer(total.getAmountByTimer());
        grand.setAmountByCubic(total.getAmountByCubic());
        grand.setAmountByCount(total.getAmountByCount());
        grand.setOilCount(total.getOilCount());
        grand.setPriceByOil(project.getOilPirce());
        grand.setAmountByOil(total.getAmountByOil());
        grand.setOilConsumption(total.getOilConsumption());
        grand.setRent(total.getRent());
        grand.setAmountByMeals(total.getAmountByMeals());
        grand.setSubsidyAmount(total.getSubsidyAmount());
        grand.setBalance(total.getBalance());
        grand.setReportDate(reportDate);
        AutoApiUtils.returnProjectDiggingPartCountTotal().save(total);
        AutoApiUtils.returnProjectDiggingPartCountGrand().save(grand);
    }

    public static void getNotEndWorkMachineInfo(Long projectId, Date date, Date reportDate, String cron) throws IOException {
        System.out.println("项目编号：" + projectId);
        List<ProjectWorkTimeByDigging> diggingList = AutoApiUtils.returnProjectWorkTimeByDigging().getByProjectIdByQuery(projectId, reportDate);
        if (diggingList != null) {
            for (ProjectWorkTimeByDigging digging : diggingList) {
                ProjectDiggingMachine machine = AutoApiUtils.returnProjectDiggingMachine().get(digging.getMaterialId());
                Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
                Date earlyDate = dateMap.get("start");
                if (earlyDate.getTime() > date.getTime()) {
                    Date dateIdentification = DateUtils.subtractionOneDay(date);
                    digging.setDateIdentification(dateIdentification);
                }
                digging.setStatus(DiggingMachineStatus.Stop);
                digging.setStopMode(StopEnum.AUTOMATIC);
                digging.setEndTime(date);
                Long workTime = DateUtils.calculationHour(digging.getStartTime(), digging.getEndTime());
                digging.setWorkTime(workTime);
                machine.setEndWorkTime(date);
                machine.setStatus(DiggingMachineStatus.Stop);
                ProjectWorkTimeByDiggingLog log = new ProjectWorkTimeByDiggingLog();
                log.setProjectId(projectId);
                log.setCarId(machine.getId());
                log.setCarCode(machine.getCode());
                log.setDoStatus(DeviceDoStatusEnum.AutoUnLine);
                log.setCreateId(-1L);
                log.setCreateName("交班自动触发");
                log.setDateIdentification(digging.getDateIdentification());
                Shift shift = Shift.Unknown;
                if (digging.getShift().getAlias() == 1)
                    shift = Shift.Early;
                else if (digging.getShift().getAlias() == 2)
                    shift = Shift.Night;
                else
                    shift = Shift.Unknown;
                log.setShift(shift);
                log.setProjectDeviceType(ProjectDeviceType.DiggingMachineDevice);
                log.setRemark("请求成功");
                log.setSuccess(true);
                AutoApiUtils.returnProjectWorkTimeByDigging().save(digging);
                AutoApiUtils.returnProjectDiggingMachine().save(machine);
            }
        }
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.DiggingWork);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    public static void deleteScheduleModel() {
        System.out.println("开始自动删除排班中多余数据");
        //查询所有的排班分组编号
        List<ProjectScheduleModel> projectScheduleList = AutoApiUtils.returnProjectScheduleModelServiceI().getAll();
        //在使用的分组编号
        List<String> groupCodeList = AutoApiUtils.returnScheduleMachineModelServiceI().getGroupCodeList();
        //List<String> carGroupList = AutoApiUtils.returnScheduleCar().getGroupCodeList();
        for (ProjectScheduleModel schedule : projectScheduleList) {
            if (!groupCodeList.contains(schedule.getGroupCode())) {
                AutoApiUtils.returnProjectScheduleModelServiceI().deleteByGroupCode(schedule.getGroupCode());
                AutoApiUtils.returnScheduleMachineModelServiceI().deleteByGroupCode(schedule.getGroupCode());
                AutoApiUtils.returnScheduleCarModelServiceI().deleteByGroupCode(schedule.getGroupCode());
            }
        }
    }

    public static void deleteSchedule() {
        System.out.println("开始自动删除排班中多余数据");
        //查询所有的排班分组编号
        List<ProjectSchedule> projectScheduleList = AutoApiUtils.returnProjectSchedule().getAll();
        //在使用的分组编号
        List<String> groupCodeList = AutoApiUtils.returnScheduleMachine().getGroupCodeList();
        //List<String> carGroupList = AutoApiUtils.returnScheduleCar().getGroupCodeList();
        for (ProjectSchedule schedule : projectScheduleList) {
            if (!groupCodeList.contains(schedule.getGroupCode())) {
                AutoApiUtils.returnProjectSchedule().deleteByGroupCode(schedule.getGroupCode());
                AutoApiUtils.returnScheduleMachine().deleteByGroupCode(schedule.getGroupCode());
                AutoApiUtils.returnScheduleCar().deleteByGroupCode(schedule.getGroupCode());
            }
        }
        /*for(String code : groupCodeList){
            if (!carGroupList.contains(code)) {
                AutoApiUtils.returnProjectSchedule().deleteByGroupCode(code);
                AutoApiUtils.returnScheduleMachine().deleteByGroupCode(code);
                AutoApiUtils.returnScheduleCar().deleteByGroupCode(code);
            }
        }*/
    }

    public static void doProjectCarFillMeterReadingTask(Project project, Date reportDate) {
        //  异常时返回的油车id
        Long oilCarId = null;

        //  油枪端口
        final Integer port1 = 1;
        final Integer port2 = 2;
        try {
            //每个项目的抄表记录,取最后一条记录
            //  该项目中的油车
            Specification<ProjectOtherDevice> specOilCar = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    list.add(cb.equal(root.get("carType").as(CarType.class), CarType.OilCar));
                    list.add(cb.equal(root.get("projectId").as(Long.class), project.getId()));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectOtherDevice> projectOilCarList = AutoApiUtils.returnProjectOtherDeviceServiceI().queryWx(specOilCar);

            for (ProjectOtherDevice projectOilCar : projectOilCarList) {
                oilCarId = projectOilCar.getId();
                //  1号端口油枪
                Specification<ProjectCarFillMeterReadingLog> specPort1 = new Specification<ProjectCarFillMeterReadingLog>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCarFillMeterReadingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                        list.add(cb.equal(root.get("oilCarId").as(Long.class), projectOilCar.getId()));
                        list.add(cb.equal(root.get("port").as(Integer.class), port1));
                        list.add(cb.equal(root.get("projectId").as(Long.class), project.getId()));
                        //  *****必须倒叙,因为要取最近一条*****
                        query.orderBy(cb.desc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCarFillMeterReadingLog> readingLogsPort1 = AutoApiUtils.returnProjectCarFillMeterReadingLogServiceI().query(specPort1, PageRequest.of(0, 10)).getContent();
                ProjectCarFillMeterReadingLog projectCarFillMeterReadingLog1 = new ProjectCarFillMeterReadingLog();
                projectCarFillMeterReadingLog1.setAddTime(new Date());
                projectCarFillMeterReadingLog1.setPort(port1);
                //  有该油车的抄表记录,复制上一次的数据
                if (readingLogsPort1.size() != 0) {
                    //  当日油表初始数 = 昨日油表终止数
                    projectCarFillMeterReadingLog1.setStartOilMeterToday(readingLogsPort1.get(0).getEndOilMeterToday());
                    //  当日油表终止量 = 当日油表初始数 + 当日加油合计
                    projectCarFillMeterReadingLog1.setEndOilMeterToday(projectCarFillMeterReadingLog1.getStartOilMeterToday() + projectCarFillMeterReadingLog1.getOilMeterTodayTotal());
                }
                projectCarFillMeterReadingLog1.setProjectId(project.getId());
                projectCarFillMeterReadingLog1.setOilCarId(projectOilCar.getId());
                projectCarFillMeterReadingLog1.setOilCarCode(projectOilCar.getCode());
                projectCarFillMeterReadingLog1.setId(null);
                projectCarFillMeterReadingLog1.setProjectId(project.getId());
//                projectCarFillMeterReadingLog1.setOperatorId(projectOilCar.getManagerId());
//                projectCarFillMeterReadingLog1.setOperatorName(projectOilCar.getManagerName());
                projectCarFillMeterReadingLog1.setShifts(AutoApiUtils.returnWorkDate().getTargetDateShift(projectCarFillMeterReadingLog1.getAddTime(), project.getId()));
                projectCarFillMeterReadingLog1.setDateIdentification(AutoApiUtils.returnWorkDate().getTargetDateIdentification(projectCarFillMeterReadingLog1.getAddTime(), project.getId()));
                ProjectCarFillMeterReadingLog savedLog1 = AutoApiUtils.returnProjectCarFillMeterReadingLogServiceI().save(projectCarFillMeterReadingLog1);
                log.info("项目id:{},油车id:{},端口:{};抄表完成", project.getId(), projectOilCar.getId(), port1);


                //  --------------------------------


                //  2号端口油枪
                Specification<ProjectCarFillMeterReadingLog> specPort2 = new Specification<ProjectCarFillMeterReadingLog>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCarFillMeterReadingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("oilCarId").as(Long.class), projectOilCar.getId()));
                        list.add(cb.equal(root.get("port").as(Integer.class), port2));
                        list.add(cb.equal(root.get("projectId").as(Long.class), project.getId()));
                        //  *****必须倒叙,因为要取最近一条*****
                        query.orderBy(cb.desc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCarFillMeterReadingLog> readingLogsPort2 = AutoApiUtils.returnProjectCarFillMeterReadingLogServiceI().query(specPort2, PageRequest.of(0, 10)).getContent();
                ProjectCarFillMeterReadingLog projectCarFillMeterReadingLog2 = new ProjectCarFillMeterReadingLog();
                projectCarFillMeterReadingLog2.setAddTime(new Date());
                projectCarFillMeterReadingLog2.setPort(port2);
                //  有该油车的抄表记录,复制上一次的数据
                if (readingLogsPort2.size() != 0) {
                    //  当日油表初始数 = 昨日油表终止数
                    projectCarFillMeterReadingLog2.setStartOilMeterToday(readingLogsPort2.get(0).getEndOilMeterToday());
                    //  当日油表终止量 = 当日油表初始数 + 当日加油合计
                    projectCarFillMeterReadingLog2.setEndOilMeterToday(projectCarFillMeterReadingLog2.getStartOilMeterToday() + projectCarFillMeterReadingLog2.getOilMeterTodayTotal());
                }
                projectCarFillMeterReadingLog2.setProjectId(project.getId());
                projectCarFillMeterReadingLog2.setOilCarId(projectOilCar.getId());
                projectCarFillMeterReadingLog2.setOilCarCode(projectOilCar.getCode());
                projectCarFillMeterReadingLog2.setId(null);
                projectCarFillMeterReadingLog2.setProjectId(project.getId());
//                projectCarFillMeterReadingLog2.setOperatorId(projectOilCar.getManagerId());
//                projectCarFillMeterReadingLog2.setOperatorName(projectOilCar.getManagerName());
                projectCarFillMeterReadingLog2.setShifts(AutoApiUtils.returnWorkDate().getTargetDateShift(projectCarFillMeterReadingLog2.getAddTime(), project.getId()));
                projectCarFillMeterReadingLog2.setDateIdentification(AutoApiUtils.returnWorkDate().getTargetDateIdentification(projectCarFillMeterReadingLog2.getAddTime(), project.getId()));
                ProjectCarFillMeterReadingLog savedLog2 = AutoApiUtils.returnProjectCarFillMeterReadingLogServiceI().save(projectCarFillMeterReadingLog2);
                log.info("项目id:{},油车id:{},端口:{};抄表完成", project.getId(), projectOilCar.getId(), port2);

                projectOilCar.setEndOilMeterPort1(savedLog1.getEndOilMeterToday());
                projectOilCar.setEndOilMeterPort2(savedLog2.getEndOilMeterToday());
                AutoApiUtils.returnProjectOtherDeviceServiceI().save(projectOilCar);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("抄表异常: 项目id:{},油车id:{}", project.getId(), oilCarId);
        }
    }

    public static void matchingDegreeReport(Long projectId, Date startTime, Date endTime, TimeTypeEnum timeType, String cron) throws IOException {
        //创建时间 用作日期标识
        Date createDate = DateUtils.createReportDateByMonth(startTime);
        //班次
        ShiftsEnums shifts = AutoApiUtils.returnWorkDate().getTargetDateShift(startTime, projectId);
        AutoApiUtils.returnMatchDegree().deleteByProjectIdAndTimeAndType(projectId, createDate, timeType.getAlias(), shifts.getAlias());
        //当前时间段内 有效的渣车编号集合
        List<Map> carCodeList = AutoApiUtils.returnProjectUnload().getCarCodeByProjectIDAndTime(projectId, startTime, endTime);

        //获取完成的渣车集合
        List<Map> finishList = AutoApiUtils.returnProjectCarWork().getFinishCarCountByProjectIdAndTime(projectId, startTime, endTime);
        //生成完成渣车的索引集合
        Map<String, Integer> finishIndex = new HashMap<>();
        for (int i = 0; i < finishList.size(); i++) {
            String carCode = finishList.get(i).get("car_code").toString();
            finishIndex.put(carCode, i);
        }

        //获取渣场上传失效的车数
        List<Map> unValidList = AutoApiUtils.returnProjectUnload().getUnValidByProjectIDAndTime(projectId, startTime, endTime);
        //生成无效渣车的索引集合
        Map<String, Integer> unValidIndex = new HashMap<>();
        for (int i = 0; i < unValidList.size(); i++) {
            String carCode = unValidList.get(i).get("car_code").toString();
            unValidIndex.put(carCode, i);
        }

        //获取带装载时间渣场上传的车数
        List<Map> uploadCountList = AutoApiUtils.returnProjectUnload().getCarCountByProjectIDAndTime(projectId, startTime, endTime, new Date(0));
        //生成装载带时间渣车上传的索引
        Map<String, Integer> uploadIndex = new HashMap<>();
        for (int i = 0; i < uploadCountList.size(); i++) {
            String carCode = uploadCountList.get(i).get("car_code").toString();
            uploadIndex.put(carCode, i);
        }

        //获取带检测时间渣场上传的车数
        List<Map> uploadCountByCheckTimeList = AutoApiUtils.returnProjectUnload().getUploadCountByCheck(projectId, startTime, endTime, new Date(0));
        //生成到检测时间渣车上传的索引
        Map<String, Integer> checkIndexMap = new HashMap<>();
        for (int i = 0; i < uploadCountByCheckTimeList.size(); i++) {
            String carCode = uploadCountByCheckTimeList.get(i).get("car_code").toString();
            checkIndexMap.put(carCode, i);
        }
        //获取挖机上传的车数
        List<Map> uploadMachineList = AutoApiUtils.returnProjectLoad().getMachineCountByProjectIdAndTime(projectId, startTime, endTime);
        //生成挖机上传的索引
        Map<String, Integer> uploadMachineIndex = new HashMap<>();
        for (int i = 0; i < uploadMachineList.size(); i++) {
            String carCode = uploadMachineList.get(i).get("car_code").toString();
            uploadMachineIndex.put(carCode, i);
        }

        //获取检测终端上传的车数
        List<Map> checkCarList = AutoApiUtils.returnProjectCheckLog().getCheckCountByProjectIDAndTimeCheck(projectId, startTime, endTime);
        //生成检测终端上传的索引
        Map<String, Integer> checkCarIndexMap = new HashMap<>();
        for (int i = 0; i < checkCarList.size(); i++) {
            String carCode = checkCarList.get(i).get("car_code").toString();
            checkCarIndexMap.put(carCode, i);
        }

        //获取渣车终端上传的车数
        List<Map> slagCarList = AutoApiUtils.returnProjectSlagCarLog().getCarCountByProjectIDAndTime(projectId, startTime, endTime);
        //生成检测终端上传的索引
        Map<String, Integer> slagCarIndexMap = new HashMap<>();
        for (int i = 0; i < slagCarList.size(); i++) {
            String carCode = slagCarList.get(i).get("car_code").toString();
            slagCarIndexMap.put(carCode, i);
        }
        //获取未按规定卸载的车数
        List<ProjectErrorLoadLog> loadLogList = AutoApiUtils.returnProjectErrorLoadLogServiceI().getAllByProjectIdAndDateIdentificationAndShift(projectId, createDate, shifts.getAlias());
        //生成未按规定卸载的索引
        Map<String, Integer> errorLoadIndexMap = new HashMap<>();
        for (int i = 0; i < loadLogList.size(); i++) {
            String carCode = loadLogList.get(i).getCarCode();
            errorLoadIndexMap.put(carCode, i);
        }

        List<MatchingDegree> degreeList = new ArrayList<>();
        for (int i = 0; i < carCodeList.size(); i++) {
            //渣车ID
            Long carId = Long.parseLong(carCodeList.get(i).get("carid").toString());
            //渣车编号
            String carCode = carCodeList.get(i).get("car_code").toString();
            //对应时间段上传数量
            Long totalCount = Long.parseLong(carCodeList.get(i).get("count").toString());
            //该渣车完成数量
            Integer finishCarIndex = finishIndex.get(carCode);
            Long finishCount = 0L;
            if (finishCarIndex != null)
                finishCount = Long.parseLong(finishList.get(finishCarIndex).get("count").toString());
            MatchingDegree degree = new MatchingDegree();
            degree.setProjectId(projectId);
            degree.setCarCode(carCode);
            degree.setCarId(carId);
            degree.setUploadTotalCountByCar(totalCount);
            degree.setFinishCount(finishCount);
            //该渣车带装载时间的数量
            Integer uploadCarIndex = uploadIndex.get(carCode);
            if (uploadCarIndex != null)
                degree.setUploadCountByCar(Long.parseLong(uploadCountList.get(uploadCarIndex).get("count").toString()));
            //该渣车带检测时间的数量
            Integer checkCarIndex = checkIndexMap.get(carCode);
            if (checkCarIndex != null)
                degree.setUploadCountByCheckTime(Long.parseLong(uploadCountByCheckTimeList.get(checkCarIndex).get("count").toString()));

            //该渣车上传失效的数量
            Integer validIndex = unValidIndex.get(carCode);
            if (validIndex != null)
                degree.setUnValidCount(Long.parseLong(unValidList.get(validIndex).get("count").toString()));
            //挖机上传车数
            Integer machineIndex = uploadMachineIndex.get(carCode);
            if (machineIndex != null)
                degree.setUploadCountByMachine(Long.parseLong(uploadMachineList.get(machineIndex).get("count").toString()));
            //检测终端上传车数
            Integer checkIndex = checkCarIndexMap.get(carCode);
            if (checkIndex != null)
                degree.setUploadCountByCheck(Long.parseLong(checkCarList.get(checkIndex).get("count").toString()));
            //渣车终端上传车数
            Integer slagCarIndex = slagCarIndexMap.get(carCode);
            if (slagCarIndex != null)
                degree.setUploadCountByCarDevice(Long.parseLong(slagCarList.get(slagCarIndex).get("count").toString()));
            //获取未按规定卸载的车数
            Integer errorLoadIndex = errorLoadIndexMap.get(carCode);
            if (errorLoadIndex != null)
                degree.setNoMachineIdCount(Long.parseLong(loadLogList.get(errorLoadIndex).getCount().toString()));
            //定位成功率
            BigDecimal locationPercent = degree.getUploadTotalCountByCar() != 0 ? new BigDecimal((float) degree.getUploadCountByCarDevice() / degree.getUploadTotalCountByCar()).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            degree.setLocationPercent(locationPercent);
            //匹配率
            BigDecimal degreePercent = degree.getUploadTotalCountByCar() != 0 ? new BigDecimal((float) degree.getUploadCountByMachine() / degree.getUploadTotalCountByCar()).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            degree.setDegreePercent(degreePercent);
            //写卡成功率
            BigDecimal writeCardPercent = degree.getUploadTotalCountByCar() != 0 ? new BigDecimal((float) degree.getUploadCountByCar() / degree.getUploadTotalCountByCar()).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            degree.setWriteCardPercent(writeCardPercent);
            //完成率
            BigDecimal finishPercent = degree.getUploadTotalCountByCar() != 0 ? new BigDecimal((float) degree.getFinishCount() / degree.getUploadTotalCountByCar()).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            degree.setFinishPercent(finishPercent);
            degree.setStartTime(startTime);
            degree.setEndTime(endTime);
            degree.setShifts(shifts);
            degree.setCreateTime(new Date());
            degree.setDateIdentification(createDate);
            degree.setTimeType(timeType);
            degreeList.add(degree);
        }
        AutoApiUtils.returnMatchDegree().batchSave(degreeList);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.MatchingDegreeReport);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    public static void slagSiteCarReport(Long projectId, Date reportDate, String cron) throws IOException {
        AutoApiUtils.returnProjectSlagSiteCar().deleteByProjectIdAndReportDate(projectId, reportDate);
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
        Date startTime = dateMap.get("start");
        Date endTime = dateMap.get("end");
        reportDate = DateUtils.createReportDateByMonth(reportDate);
        List<Map> slagSiteList = AutoApiUtils.returnProjectCarWork().getReportInfoGroupBySlagSite(projectId, startTime, endTime);
        List<ProjectSlagSiteCarReport> reportList = new ArrayList<>();
        List<ProjectCarWorkInfo> infoList = AutoApiUtils.returnProjectCarWork().getAllByProjectIdAndTime(projectId, startTime, endTime);
        for (int i = 0; i < slagSiteList.size(); i++) {
            ProjectSlagSiteCarReport report = new ProjectSlagSiteCarReport();
            //总数量
            Long count = Long.parseLong(slagSiteList.get(i).get("count").toString());
            //渣场ID
            Long slagSiteId = Long.parseLong(slagSiteList.get(i).get("slag_site_id").toString());
            //渣场名称
            String slagSiteName = slagSiteList.get(i).get("slag_site_name").toString();
            //渣车ID
            Long carId = Long.parseLong(slagSiteList.get(i).get("car_id").toString());
            //渣车编号
            String carCode = slagSiteList.get(i).get("car_code").toString();
            //运距
            Long distance = Long.parseLong(slagSiteList.get(i).get("distance").toString());
            //班次
            Integer shiftValue = Integer.valueOf(slagSiteList.get(i).get("shift").toString());
            ShiftsEnums shifts = ShiftsEnums.converShift(shiftValue);
            List<Date> dateList = new ArrayList<>();
            for (ProjectCarWorkInfo info : infoList) {
                if (info.getCarId().compareTo(carId) == 0 && info.getSlagSiteId().compareTo(slagSiteId) == 0 && info.getShift().getAlias() == shiftValue)
                    dateList.add(info.getTimeDischarge());
            }
            report.setProjectId(projectId);
            report.setCount(count);
            report.setSlagSiteId(slagSiteId);
            report.setSlagSiteName(slagSiteName);
            report.setCarCode(carCode);
            report.setCarId(carId);
            report.setCreateDate(new Date());
            report.setReportDate(reportDate);
            report.setShift(shifts);
            report.setDistance(distance);
            report.setDetailJson(JSON.toJSONString(dateList));
            reportList.add(report);
        }
        AutoApiUtils.returnProjectSlagSiteCar().batchSave(reportList);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.SlagSiteReport);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    public static void reportDiggingByPlace(Long projectId, Date reportDate, String cron) throws IOException {
        AutoApiUtils.returnProjectDiggingReportByPlace().deleteByProjectIdAndAndDateIdentification(projectId, reportDate);
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
        Date startTime = dateMap.get("start");
        Date date = DateUtils.createReportDateByMonth(startTime);
        List<Map> timeByPlace = AutoApiUtils.returnProjectWorkTimeByDigging().reportDiggingWorkTimeByPlace(projectId, reportDate);
        List<ProjectDiggingReportByPlace> placeList = new ArrayList<>();
        for (int i = 0; i < timeByPlace.size(); i++) {
            ProjectDiggingReportByPlace place = new ProjectDiggingReportByPlace();
            Long machineId = timeByPlace.get(i).get("material_id") != null ? Long.parseLong(timeByPlace.get(i).get("material_id").toString()) : 0L;
            String machineCode = timeByPlace.get(i).get("material_code") != null ? timeByPlace.get(i).get("material_code").toString() : "";
            Long placeId = timeByPlace.get(i).get("place_id") != null ? Long.parseLong(timeByPlace.get(i).get("place_id").toString()) : 0L;
            String placeName = timeByPlace.get(i).get("place_name") != null ? timeByPlace.get(i).get("place_name").toString() : "";
            Integer shiftValue = timeByPlace.get(i).get("shift") != null ? Integer.valueOf(timeByPlace.get(i).get("shift").toString()) : 0;
            ShiftsEnums shifts = ShiftsEnums.converShift(shiftValue);
            Long workTime = timeByPlace.get(i).get("workTime") != null ? Long.parseLong(timeByPlace.get(i).get("workTime").toString()) : 0L;
            BigDecimal time = new BigDecimal((float) workTime / 60).setScale(2, BigDecimal.ROUND_HALF_UP);
            time = time.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
            place.setMachineId(machineId);
            place.setMachineCode(machineCode);
            place.setProjectId(projectId);
            place.setDateIdentification(date);
            place.setPlaceId(placeId);
            place.setPlaceName(placeName);
            place.setShifts(shifts);
            place.setWorkTime(time);
            placeList.add(place);
        }
        AutoApiUtils.returnProjectDiggingReportByPlace().batchSave(placeList);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.DiggingPlaceReport);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    public static void reportDiggingByMaterial(Long projectId, Date reportDate, String cron) throws IOException {
        AutoApiUtils.returnProjectDiggingReportByMaterial().deleteByProjectIdAndDateIdentification(projectId, reportDate);
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
        Date startTime = dateMap.get("start");
        Date date = DateUtils.createReportDateByMonth(startTime);
        List<Map> timeByPlace = AutoApiUtils.returnProjectWorkTimeByDigging().reportDiggingWorkTimeByMaterial(projectId, reportDate);
        List<ProjectDiggingReportByMaterial> materialList = new ArrayList<>();
        for (int i = 0; i < timeByPlace.size(); i++) {
            ProjectDiggingReportByMaterial material = new ProjectDiggingReportByMaterial();
            Long machineId = timeByPlace.get(i).get("material_id") != null ? Long.parseLong(timeByPlace.get(i).get("material_id").toString()) : 0L;
            String machineCode = timeByPlace.get(i).get("material_code") != null ? timeByPlace.get(i).get("material_code").toString() : "";
            Long dataId = timeByPlace.get(i).get("data_id") != null ? Long.parseLong(timeByPlace.get(i).get("data_id").toString()) : 0L;
            String dataName = timeByPlace.get(i).get("data_name") != null ? timeByPlace.get(i).get("data_name").toString() : "";
            Integer shiftValue = timeByPlace.get(i).get("shift") != null ? Integer.valueOf(timeByPlace.get(i).get("shift").toString()) : 0;
            ShiftsEnums shifts = ShiftsEnums.converShift(shiftValue);
            Long workTime = timeByPlace.get(i).get("workTime") != null ? Long.parseLong(timeByPlace.get(i).get("workTime").toString()) : 0L;
            BigDecimal time = new BigDecimal((float) workTime / 60).setScale(2, BigDecimal.ROUND_HALF_UP);
            time = time.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
            material.setMachineId(machineId);
            material.setMachineCode(machineCode);
            material.setProjectId(projectId);
            material.setDateIdentification(date);
            material.setMaterialId(dataId);
            material.setMaterialName(dataName);
            material.setShifts(shifts);
            material.setWorkTime(time);
            materialList.add(material);
        }
        AutoApiUtils.returnProjectDiggingReportByMaterial().batchSave(materialList);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.DiggingMaterialReport);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    /*public static void modifyWorkInfoValid(Long projectId, Shift shift, Date reportDate, String cron) throws JsonProcessingException {
        reportDate = DateUtils.subtractionOneDay(reportDate);
        List<ProjectCarWorkInfo> projectCarWorkInfoList = AutoApiUtils.returnProjectCarWork().getAllByProjectIdAndDateIdentificationAndShift(projectId, reportDate, shift.getAlias());
        List<ProjectCarWorkInfo> saveList = new ArrayList<>();
        for(ProjectCarWorkInfo info : projectCarWorkInfoList){
            Date maxTime = null;
            //间隔时间
            Long intervalSecond = 0L;
            if(info.getTimeLoad() != null && info.getTimeDischarge() != null && info.getTimeCheck() != null){
                if(info.getTimeDischarge().getTime() > info.getTimeCheck().getTime())
                    maxTime = info.getTimeDischarge();
                else
                    maxTime = info.getTimeCheck();
                intervalSecond = DateUtils.calculationHour(info.getTimeLoad(), maxTime);
            }else if(info.getTimeLoad() != null && info.getTimeDischarge() != null){
                maxTime = info.getTimeDischarge();
                intervalSecond = DateUtils.calculationHour(info.getTimeLoad(), maxTime);
            }else{
                info.setInfoValid(false);
            }
            if(intervalSecond > 60 * 60 * 12){
                info.setInfoValid(false);
            }
            saveList.add(info);
        }
        AutoApiUtils.returnProjectCarWork().savAll(saveList);
        if(StringUtils.isNotEmpty(cron)){
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.CarWorkInfoValid);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }*/

    public static void stopSlagCar(Long projectId, String cron) throws JsonProcessingException {
        List<ProjectCar> projectCarList = AutoApiUtils.returnProjectCar().getByProjectIdAndIsVaild(projectId, true);
        List<ProjectCar> saveList = new ArrayList<>();
        for (ProjectCar car : projectCarList) {
            if (car.getStatus().compareTo(ProjectCarStatus.Working) == 0) {
                car.setStatus(ProjectCarStatus.Stop);
                saveList.add(car);
            }
        }
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.SlagCarWork);
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
        AutoApiUtils.returnProjectCar().batchSave(saveList);
    }

    public static void cardCountReport(Long projectId, Shift shift, Date date) throws IOException {
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, date);
        Date startTime = new Date(0);
        Date endTime = new Date(0);
        Date start = dateMap.get("start");
        if (shift.compareTo(Shift.Early) == 0) {
            startTime = dateMap.get("start");
            endTime = dateMap.get("earlyEnd");
        } else {
            startTime = dateMap.get("nightStart");
            endTime = dateMap.get("end");
        }
        if (date.getTime() < start.getTime()) {
            date = DateUtils.subtractionOneDay(date);
            startTime = DateUtils.subtractionOneDay(startTime);
            endTime = DateUtils.subtractionOneDay(endTime);
        }
        AutoApiUtils.returnProjectMqttCardReportServiceI().deleteAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        List<ProjectSlagCarLog> projectSlagCarLogList = AutoApiUtils.returnProjectSlagCarLog().getAllByProjectIDAndTimeDischarge(projectId, startTime, endTime);
        List<ProjectUnloadLog> projectUnloadLogList = AutoApiUtils.returnProjectUnload().getAllByProjectIDAndTime(projectId, startTime, endTime);
        List<ProjectCarWorkInfo> projectCarWorkInfoList = AutoApiUtils.returnProjectCarWork().getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        //渣车上传总数
        Long totalCountByCar = Long.parseLong(String.valueOf(projectSlagCarLogList.size()));
        //渣场上传总数
        Long totalCountBySlagSite = Long.parseLong(String.valueOf(projectUnloadLogList.size()));
        //渣车上传没有挖机ID
        Long slagCarWithOutMachineId = 0L;
        //渣车上传没有装载时间
        Long slagCarWithOutTimeLoad = 0L;
        //渣车上传没有调度
        Long slagCarWithOutSchedule = 0L;
        //渣车上传没有无聊
        Long slagCarWithOutLoader = 0L;
        for (ProjectSlagCarLog log : projectSlagCarLogList) {
            if (log.getExcavatCurrent() == null || log.getExcavatCurrent() == 0)
                slagCarWithOutMachineId++;
            if (log.getTimeLoad() == null || log.getTimeLoad().getTime() == 0)
                slagCarWithOutTimeLoad++;
            if (log.getSchMode() == null || log.getSchMode() == 0)
                slagCarWithOutSchedule++;
            if (log.getLoader() == null || log.getLoader() == 0)
                slagCarWithOutLoader++;
        }
        //渣场上传没有挖机编号
        Long slagSiteWithOutMachineId = 0L;
        //渣场上传没有装载时间
        Long slagSiteWithOutTimeLoad = 0L;
        //渣场上传没有物料
        Long slagSiteWithOutLoader = 0L;
        //渣场上传没有调度
        Long slagSiteWithOutSchedule = 0L;
        for (ProjectUnloadLog log : projectUnloadLogList) {
            if (log.getExcavatCurrent() == null || log.getExcavatCurrent() == 0)
                slagSiteWithOutMachineId++;
            if (log.getTimeLoad() == null || log.getTimeLoad().getTime() == 0)
                slagSiteWithOutTimeLoad++;
            if (log.getLoader() == null || log.getLoader() == 0)
                slagSiteWithOutLoader++;
            if (log.getSchMode() == null || log.getSchMode() == 0)
                slagSiteWithOutSchedule++;
        }
    }

    public static void workExceptionReport(Long projectId, Shift shift, Date date, String cron) throws IOException {
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, date);
        Date earlyStart = dateMap.get("start");
        if (date.getTime() < earlyStart.getTime())
            date = DateUtils.getAddDate(date, -1);
        AutoApiUtils.returnProjectMqttCardCountReportServiceI().deleteByProjectIdAndCreateTime(projectId, date, shift.getAlias());
        List<Map> countList = AutoApiUtils.returnProjectMqttCardReportServiceI().getReportCountByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        List<ProjectMqttCardCountReport> reportList = new ArrayList<>();
        for (int i = 0; i < countList.size(); i++) {
            //渣车编号
            String carCode = countList.get(i).get("car_code").toString();
            //总数量
            Integer count = Integer.valueOf(countList.get(i).get("count").toString());
            //异常编号
            Long errorCode = Long.parseLong(countList.get(i).get("error_code").toString());
            //异常详情
            String message = countList.get(i).get("message").toString();
            //调度模式
            Integer dispatchModeValue = Integer.valueOf(countList.get(i).get("dispatch_mode").toString());
            ProjectDispatchMode dispatchMode = ProjectDispatchMode.converMode(dispatchModeValue);
            ProjectMqttCardCountReport report = new ProjectMqttCardCountReport();
            report.setProjectId(projectId);
            report.setErrorCode(errorCode);
            report.setMessage(message);
            report.setCarCode(carCode);
            report.setCount(count);
            report.setDateIdentification(DateUtils.createReportDateByMonth(date));
            report.setShift(shift);
            report.setDispatchMode(dispatchMode);
            reportList.add(report);
        }
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.ExceptionReport);
            log.setDateIdentification(DateUtils.createReportDateByMonth(date));
            log.setCron(cron);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
        AutoApiUtils.returnProjectMqttCardCountReportServiceI().batchSave(reportList);
    }

    public static void totalCountCarReport(Long projectId, Shift shift, Date date, String cron) throws IOException {
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, date);
        Date startTime = new Date(0);
        Date endTime = new Date(0);
        if (shift.compareTo(Shift.Early) == 0) {
            startTime = dateMap.get("start");
            endTime = dateMap.get("earlyEnd");
        } else {
            startTime = dateMap.get("nightStart");
            endTime = dateMap.get("end");
        }
        Date dateIdentification = DateUtils.createReportDateByMonth(date);
        AutoApiUtils.returnProjectCarTotalCountReportByTotalServiceI().deleteByProjectIdAndDateIdentificationAndShift(projectId, dateIdentification, shift.getAlias());
        AutoApiUtils.returnProjectCarTotalCountReportServiceI().deleteByProjectIdAndDateIdentificationAndShift(projectId, dateIdentification, shift.getAlias());
        //获取有效的刷卡总数
        List<Map> unloadCountList = AutoApiUtils.returnProjectUnload().getTotalReportInfoByCarCode(projectId, startTime, endTime);
        //生成有效刷卡索引
        Map<String, Integer> unloadMapIndex = new HashMap<>();
        for (int i = 0; i < unloadCountList.size(); i++) {
            String carCode = unloadCountList.get(i).get("car_code").toString();
            unloadMapIndex.put(carCode, i);
        }
        //完成车数集合
        List<Map> finishCountList = AutoApiUtils.returnProjectCarWork().getAllByProjectIdAndDateIdentificationAndShiftAndStatusGroupByErrorCode(projectId, dateIdentification, shift.getAlias(), 7);
        //生成完成车数索引
        Map<String, Integer> finishCountMapIndex = new HashMap<>();
        for (int i = 0; i < finishCountList.size(); i++) {
            Long mergeCode = finishCountList.get(i) != null && finishCountList.get(i).get("merge_code") != null ? Long.parseLong(finishCountList.get(i).get("merge_code").toString()) : 0L;
            String carCode = finishCountList.get(i).get("car_code").toString();
            finishCountMapIndex.put(carCode + mergeCode, i);
        }
        //异常车数集合
        List<Map> errorCountList = AutoApiUtils.returnProjectMqttCardReportServiceI().getErrorCountByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        //生成异常车数索引
        Map<String, Integer> errorCountMapIndex = new HashMap<>();
        for (int i = 0; i < errorCountList.size(); i++) {
            Long errorCode = Long.parseLong(errorCountList.get(i).get("error_code").toString());
            String carCode = errorCountList.get(i).get("car_code").toString();
            errorCountMapIndex.put(carCode + errorCode, i);
        }
        //所有车辆
        List<ProjectCar> projectCarList = AutoApiUtils.returnProjectCar().getByProjectIdAndIsVaild(projectId, true);
        List<ProjectCarTotalCountReport> reportList = new ArrayList<>();
        //合计
        ProjectCarTotalCountReportByTotal total = new ProjectCarTotalCountReportByTotal();
        total.setProjectId(projectId);
        total.setShift(shift);
        total.setDateIdentification(dateIdentification);
        total = AutoApiUtils.returnProjectCarTotalCountReportByTotalServiceI().save(total);
        for (ProjectCar car : projectCarList) {
            //车辆编号
            String carCode = car.getCode();
            //正常合并索引
            Integer successIndex = finishCountMapIndex.get(carCode + WorkMergeSuccessEnum.SuccessMerge.getValue());
            Long successCount = successIndex != null ? Long.parseLong(finishCountList.get(successIndex).get("count").toString()) : 0L;
            //仅渣场数据合并索引
            Integer onlyBySlagSiteIndex = finishCountMapIndex.get(carCode + WorkMergeSuccessEnum.SingleSlagSiteSuccessMerge.getValue());
            Long onlyBySlagSiteCount = onlyBySlagSiteIndex != null ? Long.parseLong(finishCountList.get(onlyBySlagSiteIndex).get("count").toString()) : 0L;
            //自动容错
            Integer autoMergeIndex = finishCountMapIndex.get(carCode + WorkMergeSuccessEnum.AutoErrorMerge.getValue());
            Long autoMergeCount = autoMergeIndex != null ? Long.parseLong(finishCountList.get(autoMergeIndex).get("count").toString()) : 0L;
            //实际总完成车数
            Long totalFinishCount = successCount + onlyBySlagSiteCount + autoMergeCount;

            //后台异常
            Integer backStageErrorIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.BackStageError.getValue());
            Long backStageErrorCount = backStageErrorIndex != null ? Long.parseLong(errorCountList.get(backStageErrorIndex).get("count").toString()) : 0L;
            //终端离线异常
            Integer deviceUnLineErrorIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.DeviceUnLineError.getValue());
            Long deviceUnLineErrorCount = deviceUnLineErrorIndex != null ? Long.parseLong(errorCountList.get(deviceUnLineErrorIndex).get("count").toString()) : 0L;
            //未安装终端
            Integer noHaveDeviceIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.NoHaveDevice.getValue());
            Long noHaveDeviceCount = noHaveDeviceIndex != null ? Long.parseLong(errorCountList.get(noHaveDeviceIndex).get("count").toString()) : 0L;
            //未按规定装载
            Integer workErrorIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.WorkError.getValue());
            Long workErrorCount = workErrorIndex != null ? Long.parseLong(errorCountList.get(workErrorIndex).get("count").toString()) : 0L;
            //排班不存在
            Integer withoutScheduleIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.WithoutSchedule.getValue());
            Long withoutScheduleCount = withoutScheduleIndex != null ? Long.parseLong(errorCountList.get(withoutScheduleIndex).get("count").toString()) : 0L;
            //渣车不存在
            Integer withoutCarCodeIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.WithoutCarCode.getValue());
            Long withoutCarCodeCount = withoutCarCodeIndex != null ? Long.parseLong(errorCountList.get(withoutCarCodeIndex).get("count").toString()) : 0L;
            //渣场不存在
            Integer withoutSlagSiteCodeIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.WithoutSlagSiteCode.getValue());
            Long withoutSlagSiteCodeCount = withoutSlagSiteCodeIndex != null ? Long.parseLong(errorCountList.get(withoutSlagSiteCodeIndex).get("count").toString()) : 0L;
            //不支持混编
            Integer scheduleErrorIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.ScheduleError.getValue());
            Long scheduleErrorCount = scheduleErrorIndex != null ? Long.parseLong(errorCountList.get(scheduleErrorIndex).get("count").toString()) : 0L;
            //物料不存在
            Integer withoutLoaderIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.WithoutLoader.getValue());
            Long withoutLoaderCount = withoutLoaderIndex != null ? Long.parseLong(errorCountList.get(withoutLoaderIndex).get("count").toString()) : 0L;
            //渣车终端未上传
            Integer withoutSlagCarDeviceIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.WithoutSlagCarDevice.getValue());
            Long withoutSlagCarDeviceCount = withoutSlagCarDeviceIndex != null ? Long.parseLong(errorCountList.get(withoutSlagCarDeviceIndex).get("count").toString()) : 0L;
            //排班丢失
            Integer lostScheduleIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.LostSchedule.getValue());
            Long lostScheduleCount = lostScheduleIndex != null ? Long.parseLong(errorCountList.get(lostScheduleIndex).get("count").toString()) : 0L;
            //挖机不存在
            Integer withoutDiggingMachineIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.WithoutDiggingMachine.getValue());
            Long withoutDiggingMachineCount = withoutDiggingMachineIndex != null ? Long.parseLong(errorCountList.get(withoutDiggingMachineIndex).get("count").toString()) : 0L;
            //疑似终端异常
            Integer deviceErrorLikeIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.DeviceErrorLike.getValue());
            Long deviceErrorLikeCount = deviceErrorLikeIndex != null ? Long.parseLong(errorCountList.get(deviceErrorLikeIndex).get("count").toString()) : 0L;
            //容错失败
            Integer recoverWorkInfoFailIndex = errorCountMapIndex.get(carCode + WorkMergeFailEnum.RecoverWorkInfoFail.getValue());
            Long recoverWorkInfoFailCount = recoverWorkInfoFailIndex != null ? Long.parseLong(errorCountList.get(recoverWorkInfoFailIndex).get("count").toString()) : 0L;
            //总异常车数
            Long totalErrorCount = backStageErrorCount + deviceUnLineErrorCount + noHaveDeviceCount + workErrorCount + withoutScheduleCount + withoutCarCodeCount + withoutSlagSiteCodeCount + scheduleErrorCount + withoutLoaderCount + withoutSlagCarDeviceCount + lostScheduleCount + withoutDiggingMachineCount + deviceErrorLikeCount + recoverWorkInfoFailCount;

            Integer indexBySite = unloadMapIndex.get(carCode);
            //渣场刷卡总车数
            Long totalCountBySite = indexBySite != null ? Long.parseLong(unloadCountList.get(indexBySite).get("count").toString()) : 0L;
            Long totalCountFact = totalFinishCount + totalErrorCount;
            //完成率
            BigDecimal finishPercent = totalCountFact != 0 ? new BigDecimal((float) totalFinishCount / totalCountFact).setScale(4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            //异常率
            BigDecimal exceptionPercent = totalCountFact != 0 ? new BigDecimal((float) totalErrorCount / totalCountFact).setScale(4, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
            ProjectCarTotalCountReport report = new ProjectCarTotalCountReport();
            report.setProjectId(projectId);
            report.setTotalId(total.getId());
            report.setCarCode(carCode);
            report.setCarId(car.getId());
            report.setTotalCount(totalCountBySite);
            report.setTotalCountFact(totalCountFact);
            report.setFinishCount(totalFinishCount);
            report.setFinishPercent(finishPercent);
            report.setExceptionCount(totalErrorCount);
            report.setExceptionPercent(exceptionPercent);
            report.setSuccessCount(successCount);
            total.setSuccessCount(total.getSuccessCount() + report.getSuccessCount());
            report.setOnlyBySlagSiteSuccessCount(onlyBySlagSiteCount);
            total.setOnlyBySlagSiteSuccessCount(total.getOnlyBySlagSiteSuccessCount() + report.getOnlyBySlagSiteSuccessCount());
            report.setAutoMergeSuccessCount(autoMergeCount);
            total.setAutoMergeSuccessCount(total.getAutoMergeSuccessCount() + report.getAutoMergeSuccessCount());
            report.setFailByBackStageCount(backStageErrorCount);
            total.setFailByBackStageCount(total.getFailByBackStageCount() + report.getFailByBackStageCount());
            report.setDeviceUnLineErrorCount(deviceUnLineErrorCount);
            total.setDeviceUnLineErrorCount(total.getDeviceUnLineErrorCount() + report.getDeviceUnLineErrorCount());
            report.setNoHaveDeviceCount(noHaveDeviceCount);
            total.setNoHaveDeviceCount(total.getNoHaveDeviceCount() + report.getNoHaveDeviceCount());
            report.setWorkErrorCount(workErrorCount);
            total.setWorkErrorCount(total.getWorkErrorCount() + report.getWorkErrorCount());
            report.setWithoutScheduleCount(withoutScheduleCount);
            total.setWithoutScheduleCount(total.getWithoutScheduleCount() + report.getWithoutScheduleCount());
            report.setWithoutCarCodeCount(withoutCarCodeCount);
            total.setWithoutCarCodeCount(total.getWithoutCarCodeCount() + report.getWithoutCarCodeCount());
            report.setWithoutSlagSiteCodeCount(withoutSlagSiteCodeCount);
            total.setWithoutSlagSiteCodeCount(total.getWithoutSlagSiteCodeCount() + report.getWithoutSlagSiteCodeCount());
            report.setScheduleErrorCount(scheduleErrorCount);
            total.setScheduleErrorCount(total.getScheduleErrorCount() + report.getScheduleErrorCount());
            report.setWithoutLoaderCount(withoutLoaderCount);
            total.setWithoutLoaderCount(total.getWithoutLoaderCount() + report.getWithoutLoaderCount());
            report.setWithoutSlagCarDeviceCount(withoutSlagCarDeviceCount);
            total.setWithoutSlagCarDeviceCount(total.getWithoutSlagCarDeviceCount() + report.getWithoutSlagCarDeviceCount());
            report.setLostScheduleCount(lostScheduleCount);
            total.setLostScheduleCount(total.getLostScheduleCount() + report.getLostScheduleCount());
            report.setWithoutDiggingMachineCount(withoutDiggingMachineCount);
            total.setWithoutDiggingMachineCount(total.getWithoutDiggingMachineCount() + report.getWithoutDiggingMachineCount());
            report.setDeviceErrorLikeCount(deviceErrorLikeCount);
            total.setDeviceErrorLikeCount(total.getDeviceErrorLikeCount() + report.getDeviceErrorLikeCount());
            report.setRecoverWorkInfoFailCount(recoverWorkInfoFailCount);
            total.setRecoverWorkInfoFailCount(total.getRecoverWorkInfoFailCount() + report.getRecoverWorkInfoFailCount());
            //report.setExceptionDetail(errorJson);
            report.setShift(shift);
            report.setDateIdentification(dateIdentification);
            total.setTotalCount(total.getTotalCount() + report.getTotalCount());
            total.setTotalCountFact(total.getTotalCountFact() + report.getTotalCountFact());
            total.setFinishCount(total.getFinishCount() + report.getFinishCount());
            total.setExceptionCount(total.getExceptionCount() + report.getExceptionCount());
            reportList.add(report);
        }
        //完成率
        BigDecimal finishPercentByTotal = total.getTotalCountFact() != 0 ? new BigDecimal((float) total.getFinishCount() / total.getTotalCountFact()).setScale(4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        //异常率
        BigDecimal exceptionPercentByTotal = total.getTotalCountFact() != 0 ? new BigDecimal((float) total.getExceptionCount() / total.getTotalCountFact()).setScale(4, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
        total.setFinishPercent(finishPercentByTotal);
        total.setExceptionPercent(exceptionPercentByTotal);
        AutoApiUtils.returnProjectCarTotalCountReportServiceI().batchSave(reportList);
        AutoApiUtils.returnProjectCarTotalCountReportByTotalServiceI().save(total);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.WorkInfoReport);
            log.setCron(cron);
            log.setDateIdentification(date);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
    }

    /*public static void totalCountCarReport(Long projectId, Shift shift, Date date, String cron) throws IOException {
        Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, date);
        Date startTime = new Date(0);
        Date endTime = new Date(0);
        if (shift.compareTo(Shift.Early) == 0) {
            startTime = dateMap.get("start");
            endTime = dateMap.get("earlyEnd");
        } else {
            startTime = dateMap.get("nightStart");
            endTime = dateMap.get("end");
        }
        AutoApiUtils.returnProjectCarTotalCountReportByTotalServiceI().deleteByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        AutoApiUtils.returnProjectCarTotalCountReportServiceI().deleteByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        //获取有效的刷卡总数
        List<Map> unloadCountList = AutoApiUtils.returnProjectUnload().getTotalReportInfoByCarCode(projectId, startTime, endTime);
        //生成有效刷卡索引
        Map<String, Integer> unloadMapIndex = new HashMap<>();
        for (int i = 0; i < unloadCountList.size(); i++) {
            String carCode = unloadCountList.get(i).get("car_code").toString();
            unloadMapIndex.put(carCode, i);
        }
        //完成车数集合
        List<Map> finishCountList = AutoApiUtils.returnProjectCarWork().getAllByProjectIdAndDateIdentificationAndShiftAndStatusGroupByErrorCode(projectId, date, shift.getAlias(), 7);
        //生成完成车数索引
        Map<String, Integer> finishCountMapIndex = new HashMap<>();
        for (int i = 0; i < finishCountList.size(); i++) {
            Long mergeCode = finishCountList.get(i) != null && finishCountList.get(i).get("merge_code") != null ? Long.parseLong(finishCountList.get(i).get("merge_code").toString()) : 0L;
            String carCode = finishCountList.get(i).get("car_code").toString();
            finishCountMapIndex.put(carCode + mergeCode, i);
        }
        //完成编号集合
        List<Map> finishCodeList = new ArrayList<>();
        //完成合计详情集合
        List<Map> finishTotalCountList = new ArrayList<>();
        for (WorkMergeSuccessEnum successEnum : WorkMergeSuccessEnum.values()) {
            Map map = new HashMap();
            Map totalMap = new HashMap();
            map.put("mergeCode", successEnum.getValue());
            map.put("message", successEnum.getName());
            totalMap.put("mergeCode", successEnum.getValue());
            totalMap.put("message", successEnum.getName());
            totalMap.put("count", 0);
            finishCodeList.add(map);
            finishTotalCountList.add(totalMap);
        }
        //完成合计详情索引
        Map<String, Integer> finishTotalCountMapIndex = new HashMap<>();
        for (int i = 0; i < finishTotalCountList.size(); i++) {
            String mergeCode = finishTotalCountList.get(i).get("mergeCode").toString();
            finishTotalCountMapIndex.put(mergeCode, i);
        }
        //异常车数集合
        List<Map> errorCountList = AutoApiUtils.returnProjectMqttCardReportServiceI().getErrorCountByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        //生成异常车数索引
        Map<String, Integer> errorCountMapIndex = new HashMap<>();
        for (int i = 0; i < errorCountList.size(); i++) {
            Long errorCode = Long.parseLong(errorCountList.get(i).get("error_code").toString());
            String carCode = errorCountList.get(i).get("car_code").toString();
            errorCountMapIndex.put(carCode + errorCode, i);
        }
        //异常编号集合
        List<Map> errorCodeList = new ArrayList<>();
        //异常合计详情集合
        List<Map> errorTotalCountList = new ArrayList<>();
        for (WorkMergeFailEnum fail : WorkMergeFailEnum.values()) {
            Map map = new HashMap();
            Map totalMap = new HashMap();
            map.put("errorCode", fail.getValue());
            map.put("message", fail.getName());
            totalMap.put("errorCode", fail.getValue());
            totalMap.put("message", fail.getName());
            totalMap.put("count", 0);
            errorCodeList.add(map);
            errorTotalCountList.add(totalMap);
        }
        //异常合计详情索引
        Map<String, Integer> errorTotalCountMapIndex = new HashMap<>();
        for (int i = 0; i < errorTotalCountList.size(); i++) {
            String errorCode = errorTotalCountList.get(i).get("errorCode").toString();
            errorTotalCountMapIndex.put(errorCode, i);
        }
        //所有车辆
        List<ProjectCar> projectCarList = AutoApiUtils.returnProjectCar().getByProjectIdAndIsVaild(projectId, true);
        List<ProjectCarTotalCountReport> reportList = new ArrayList<>();
        //合计
        ProjectCarTotalCountReportByTotal total = new ProjectCarTotalCountReportByTotal();
        total.setProjectId(projectId);
        total.setShift(shift);
        total.setDateIdentification(date);
        total = AutoApiUtils.returnProjectCarTotalCountReportByTotalServiceI().save(total);
        for (ProjectCar car : projectCarList) {
            //车辆编号
            String carCode = car.getCode();
            List<Map> finishList = new ArrayList<>();
            Long totalFinishCount = 0L;
            Long totalErrorCount = 0L;
            for (int i = 0; i < finishCodeList.size(); i++) {
                Integer mergeCode = Integer.valueOf(finishCodeList.get(i).get("mergeCode").toString());
                String message = finishCodeList.get(i).get("message").toString();
                Integer finishIndex = finishCountMapIndex.get(carCode + mergeCode);
                Long count = finishIndex != null ? Long.parseLong(finishCountList.get(finishIndex).get("count").toString()) : 0L;
                String remark = finishIndex != null ? finishCountList.get(finishIndex).get("merge_message").toString() : message;

                Integer totalFinishIndex = finishTotalCountMapIndex.get(mergeCode.toString());
                if (totalFinishIndex != null) {
                    Map map = finishTotalCountList.get(totalFinishIndex);
                    Long totalCount = Long.parseLong(map.get("count").toString());
                    totalCount = totalCount + count;
                    map.put("count", totalCount);
                }
                totalFinishCount = totalFinishCount + count;
                Map map = new HashMap();
                map.put("mergeCode", mergeCode);
                map.put("count", count);
                map.put("remark", remark);
                finishList.add(map);
            }
            List<Map> errorList = new ArrayList<>();
            for (int i = 0; i < errorCodeList.size(); i++) {
                Integer errorCode = Integer.valueOf(errorCodeList.get(i).get("errorCode").toString());
                String message = errorCodeList.get(i).get("message").toString();
                Integer errorIndex = errorCountMapIndex.get(carCode + errorCode);
                Long count = errorIndex != null ? Long.parseLong(errorCountList.get(errorIndex).get("count").toString()) : 0L;
                String remark = errorIndex != null ? errorCountList.get(errorIndex).get("error_code_message").toString() : message;

                Integer totalErrorIndex = errorTotalCountMapIndex.get(errorCode.toString());
                if (totalErrorIndex != null) {
                    Map map = errorTotalCountList.get(totalErrorIndex);
                    Long totalCount = Long.parseLong(map.get("count").toString());
                    totalCount = totalCount + count;
                    map.put("count", totalCount);
                }

                totalErrorCount = totalErrorCount + count;
                Map map = new HashMap();
                map.put("errorCode", errorCode);
                map.put("message", remark);
                map.put("count", count);
                errorList.add(map);
            }
            String errorJson = JSON.toJSONString(errorList);
            String finishJson = JSON.toJSONString(finishList);
            Integer indexBySite = unloadMapIndex.get(carCode);
            //渣场刷卡总车数
            Long totalCountBySite = indexBySite != null ? Long.parseLong(unloadCountList.get(indexBySite).get("count").toString()) : 0L;
            Long totalCountFact = totalFinishCount + totalErrorCount;
            //完成率
            BigDecimal finishPercent = totalCountFact != 0 ? new BigDecimal((float) totalFinishCount / totalCountFact).setScale(4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
            //异常率
            BigDecimal exceptionPercent = totalCountFact != 0 ? new BigDecimal((float) totalErrorCount / totalCountFact).setScale(4, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
            ProjectCarTotalCountReport report = new ProjectCarTotalCountReport();
            report.setProjectId(projectId);
            report.setTotalId(total.getId());
            report.setCarCode(carCode);
            report.setCarId(car.getId());
            report.setTotalCount(totalCountBySite);
            report.setTotalCountFact(totalCountFact);
            report.setFinishCount(totalFinishCount);
            report.setFinishPercent(finishPercent);
            report.setExceptionCount(totalErrorCount);
            report.setExceptionPercent(exceptionPercent);
            report.setFinishDetail(finishJson);
            report.setExceptionDetail(errorJson);
            report.setShift(shift);
            report.setDateIdentification(date);
            total.setTotalCount(total.getTotalCount() + report.getTotalCount());
            total.setTotalCountFact(total.getTotalCountFact() + report.getTotalCountFact());
            total.setFinishCount(total.getFinishCount() + report.getFinishCount());
            total.setExceptionCount(total.getExceptionCount() + report.getExceptionCount());
            reportList.add(report);
        }
        //完成率
        BigDecimal finishPercentByTotal = total.getTotalCountFact() != 0 ? new BigDecimal((float) total.getFinishCount() / total.getTotalCountFact()).setScale(4, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        //异常率
        BigDecimal exceptionPercentByTotal = total.getTotalCountFact() != 0 ? new BigDecimal((float) total.getExceptionCount() / total.getTotalCountFact()).setScale(4, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
        total.setFinishPercent(finishPercentByTotal);
        total.setExceptionPercent(exceptionPercentByTotal);
        total.setFinishDetail(JSON.toJSONString(finishTotalCountList));
        total.setExceptionDetail(JSON.toJSONString(errorTotalCountList));
        AutoApiUtils.returnProjectCarTotalCountReportByTotalServiceI().save(total);
        if (StringUtils.isNotEmpty(cron)) {
            ProjectScheduleLog log = new ProjectScheduleLog();
            log.setProjectId(projectId);
            log.setCreateDate(new Date());
            log.setScheduleEnum(ScheduleEnum.WorkInfoReport);
            log.setCron(cron);
            log.setDateIdentification(date);
            AutoApiUtils.returnProjectScheduleLog().save(log);
        }
        AutoApiUtils.returnProjectCarTotalCountReportServiceI().batchSave(reportList);
    }*/
}
