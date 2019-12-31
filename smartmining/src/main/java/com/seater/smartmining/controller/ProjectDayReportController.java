package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.seater.helpers.DateEditor;
import com.seater.helpers.JsonHelper;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.api.AutoApiUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.beans.Transient;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.*;

@RestController
@RequestMapping("/api/projectDayReport")
public class ProjectDayReportController {
    @Autowired
    ProjectServiceI projectServiceI;
    @Autowired
    ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    ProjectDayReportServiceI projectDayReportServiceI;
    @Autowired
    ProjectDayReportPartCarServiceI projectDayReportPartCarServiceI;
    @Autowired
    ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    ProjectDayReportPartDistanceServiceI projectDayReportPartDistanceServiceI;
    @Autowired
    ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ExcelReportService excelReportService;
    @Autowired
    private ProjectDayReportHistoryServiceI projectDayReportHistoryServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/calc")
    @Transactional
    public Object calc(HttpServletRequest request, Date reportDate)
    {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Project project = projectServiceI.get(projectId);
            ScheduleService.scheduleCarReport(project, reportDate,  null);
            return new HashMap<String, Object>() {{put("status", "true");}};

        } catch(Exception exception) {
            exception.printStackTrace();
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Date reportDate)
    {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Map<String, Object> report = new HashMap<String, Object>();
            ProjectDayReport body = projectDayReportServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            List<ProjectDayReportPartCar> cars = null;
            List<ProjectDayReportPartDistance> distances = null;
            ProjectDayReportHistory history = null;
            if(body != null) {
                cars = projectDayReportPartCarServiceI.getByReportIdOrderByCarCode(body.getId());
                distances = projectDayReportPartDistanceServiceI.getByReportIdOrderByDistance(body.getId());
                history = projectDayReportHistoryServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
            }
            report.put("body", body);
            report.put("cars", cars);
            report.put("distances", distances);
            report.put("history", history);
            return report;

        } catch(Exception exception) {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response, Date reportDate){
        try{
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ProjectDayReport body = projectDayReportServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            List<ProjectDayReportPartCar> cars = projectDayReportPartCarServiceI.getByReportIdOrderByCarCode(body.getId());
            List<ProjectDayReportPartDistance> distances = projectDayReportPartDistanceServiceI.getByReportIdOrderByDistance(body.getId());
            String path = excelReportService.createCarDayReport(request, body, cars, distances, reportDate);
            excelReportService.downLoadFile(response, request, path, reportDate);
            FileUtils.delFile(path);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@RequestMapping("/tempReportByDay")
    public Result tempReportByDay(HttpServletRequest request, @RequestParam Date startDate, @RequestParam Date endDate){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        startDate = DateUtils.createReportDateByMonth(startDate);
        String startStr = DateUtils.formatDateByPattern(startDate, SmartminingConstant.DATEFORMAT);
        endDate = DateUtils.getEndDateByNow(endDate);
        String endStr = DateUtils.formatDateByPattern(endDate, SmartminingConstant.DATEFORMAT);
        //获取到汇总日报表后的数据集合
        List<Map> monthReportList = projectDayReportPartCarServiceI.getMonthReportByProjectIdAndReportDate(projectId, startStr, endStr);
        ProjectMonthReportTotal total = new ProjectMonthReportTotal();
        total.setProjectId(projectId);
        List<ProjectMonthReport> detailList = new ArrayList<>();
        if (monthReportList != null && monthReportList.size() > 0) {
            for (int i = 0; i < monthReportList.size(); i++) {
                ProjectMonthReport monthReport = new ProjectMonthReport();
                monthReport.setTotalId(total.getId());
                monthReport.setProjectId(projectId);
                //车辆主键编号
                Long carId = ((BigInteger) monthReportList.get(i).get("car_id")).longValue();
                monthReport.setCarId(carId);
                //车辆编号
                String code = monthReportList.get(i).get("car_code").toString();
                monthReport.setCode(code);
                //车主编号
                String carOwnerName = monthReportList.get(i).get("car_owner_name").toString();
                monthReport.setCarOwnerName(carOwnerName);
                //总车数
                Integer totalCount = Integer.valueOf(monthReportList.get(i).get("total_count").toString());
                monthReport.setTotalCount(totalCount);
                total.setTotalCount(total.getTotalCount() + totalCount);
                //总方量
                Long totalCubic = ((BigDecimal) monthReportList.get(i).get("total_cubic")).longValue();
                monthReport.setTotalCubic(totalCubic);
                total.setTotalCubic(total.getTotalCubic() + totalCubic);
                //总金额
                Long totalAmount = ((BigDecimal) monthReportList.get(i).get("total_amount")).longValue();
                monthReport.setTotalAmount(totalAmount);
                total.setTotalAmount(total.getTotalAmount() + totalAmount);
                total.setSubsidyAmount(total.getSubsidyAmount() + monthReport.getSubsidyAmount());
                //总加油量
                Long totalFill = ((BigDecimal) monthReportList.get(i).get("total_fill")).longValue();
                monthReport.setTotalFill(totalFill);
                total.setTotalFill(total.getTotalFill() + totalFill);
                //总加油金额
                Long totalAmountByFill = ((BigDecimal) monthReportList.get(i).get("total_amount_fill")).longValue();
                monthReport.setTotalAmountByFill(totalAmountByFill);
                total.setTotalAmountByFill(total.getTotalAmountByFill() + totalAmountByFill);
                total.setDeduction(total.getDeduction() + monthReport.getDeduction());
                //应付金额
                Long shouldPayAmount = totalAmount + monthReport.getSubsidyAmount() - totalAmountByFill - monthReport.getDeduction();
                monthReport.setShouldPayAmount(shouldPayAmount);
                total.setShouldPayAmount(total.getShouldPayAmount() + shouldPayAmount);
                //里程数
                Long distance = ((BigDecimal) monthReportList.get(i).get("mileage")).longValue();
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
                detailList.add(monthReport);
            }
        }
        Map map = new HashMap();
        map.put("total", total);
        map.put("detail", detailList);
        return Result.ok(map);
    }*/

    /**
     * 临时查询
     * @param request
     * @param startDate
     * @param endDate
     * @return
     * @throws IOException
     */
    @RequestMapping("/tempReportByDay")
    public Result tempReportByDay(HttpServletRequest request, @RequestParam Date startDate, @RequestParam Date endDate) throws IOException {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        startDate = DateUtils.createReportDateByMonth(startDate);
        endDate = DateUtils.getEndDateByNow(endDate);
        //对应时间内所有工作信息
        List<Map> countList = projectCarWorkInfoServiceI.getCountListByProjectIdAndTime(projectId, startDate, endDate);
        Map<String, Integer> countMapIndex = new HashMap<>();
        for(int i = 0; i < countList.size(); i++){
            String carCode = countList.get(i).get("car_code").toString();
            countMapIndex.put(carCode, i);
        }
        //对应时间内所有加油信息
        List<Map> fillList = projectCarFillLogServiceI.getCarGrandTotalFillByProjectIdAndTime(projectId, startDate, endDate);
        Map<String, Integer> fillMapIndex= new HashMap<>();
        for(int i = 0; i < fillList.size(); i++){
            Long carId = Long.parseLong(fillList.get(i).get("car_id").toString());
            String carCode = fillList.get(i).get("car_code").toString();
            Integer index = countMapIndex.get(carCode);
            if(index == null){
                ProjectCar projectCar = AutoApiUtils.returnProjectCar().get(carId);
                if (projectCar != null) {
                    Map map = new HashMap();
                    map.put("car_id", carId);
                    map.put("car_owner_id", projectCar.getOwnerId());
                    map.put("car_owner_name", projectCar.getOwnerName());
                    map.put("car_code", projectCar.getCode());
                    map.put("shift", 0);
                    map.put("payable_distance", 0L);
                    map.put("count", 0);
                    map.put("amount", 0L);
                    map.put("cubic", 0L);
                    countList.add(map);
                }
            }
            fillMapIndex.put(carCode, i);
        }
        //获取对应时间段内运距的距离
        List<Map> distanceList = projectCarWorkInfoServiceI.getDistinctByProjectIdAndTime(projectId, startDate, endDate);
        //对应运距详情
        List<Map> detailDistance = new ArrayList<>();
        Map<Long, Integer> distanceMapIndex = new HashMap<>();
        for(int i = 0; i < distanceList.size(); i++){
            Long distance = Long.parseLong(distanceList.get(i).get("payable_distance").toString());
            distanceMapIndex.put(distance, i);
            Map map = new HashMap();
            map.put("distance", distance);
            map.put("count", 0L);
            map.put("amount", 0L);
            map.put("cubic", 0L);
            detailDistance.add(map);
        }
        Integer earlyTotlaCount = 0;
        Integer nightTotalCount = 0;
        Long amount = 0L;
        Long cubic = 0L;
        Long distanceSum = 0L;
        Integer[] earlyCountList = new Integer[distanceList.size()];
        Integer[] nightCountList = new Integer[distanceList.size()];
        Arrays.fill(earlyCountList, 0);
        Arrays.fill(nightCountList, 0);
        List<Map> resultList = new ArrayList<>();
        List<ProjectTempDayReport> reportList = new ArrayList<>();
        ProjectTempDayReportTotal total = new ProjectTempDayReportTotal();
        for(int i = 0; i < countList.size(); i++){
            int next_i = i + 1;
            Long carId = Long.parseLong(countList.get(i).get("car_id").toString());
            String carCode = countList.get(i).get("car_code").toString();
            Integer shift = Integer.valueOf(countList.get(i).get("shift").toString());
            Long carOwnerId = Long.parseLong(countList.get(i).get("car_owner_id").toString());
            String carOwnerName = countList.get(i).get("car_owner_name").toString();
            amount = amount + Long.parseLong(countList.get(i).get("amount").toString());
            cubic = Long.parseLong(countList.get(i).get("cubic").toString());
            Long distance = Long.parseLong(countList.get(i).get("payable_distance").toString());
            Integer count = Integer.valueOf(countList.get(i).get("count").toString());
            distanceSum = distanceSum + distance;
            total.setAmount(total.getAmount() + amount);
            total.setCubic(total.getCubic() + cubic);
            total.setTotalCount(total.getTotalCount() + count);
            total.setTotalDistance(total.getTotalDistance() + distance);
            for(int j = 0; j < detailDistance.size(); j++){
                Map map = detailDistance.get(j);
                Long distanceD = Long.parseLong(map.get("distance").toString());
                if(distanceD.compareTo(distance) == 0){
                    Long countD = Long.parseLong(map.get("count").toString());
                    Long amountD = Long.parseLong(map.get("amount").toString());
                    Long cubicD = Long.parseLong(map.get("cubic").toString());
                    map.put("count", countD + count);
                    map.put("amount", amountD + amount);
                    map.put("cubic", cubicD + cubic);
                    break;
                }
            }
            if (shift == 1) {
                earlyTotlaCount += count;
                total.setEarlyCount(total.getEarlyCount() + count);
                earlyCountList[distanceMapIndex.get(distance)] = count;
                //total.setEarlyOnDutyCount(total.getEarlyOnDutyCount() + 1);
            } else if (shift == 2) {
                nightTotalCount += count;
                total.setNightCount(total.getNightCount() + count);
                nightCountList[distanceMapIndex.get(distance)] = count;
                //total.setNightOnDutyCount(total.getNightOnDutyCount() + 1);
            }
            //total.setOnDutyCount(total.getOnDutyCount() + 1);
            if (next_i >= countList.size() || (Long.parseLong(countList.get(next_i).get("car_id").toString())) != carId) {
                ProjectTempDayReport report = new ProjectTempDayReport();
                report.setCarId(carId);
                report.setCarCode(carCode);
                report.setTotalDistance(distanceSum);
                report.setEarlyDetailDistance(earlyCountList);
                report.setNightDetailDistance(nightCountList);
                report.setCarOwnerId(carOwnerId);
                report.setCarOwnerName(carOwnerName);
                report.setEarlyCount(earlyTotlaCount);
                report.setNightCount(nightTotalCount);
                report.setTotalCount(earlyTotlaCount + nightTotalCount);
                report.setAmount(amount);
                report.setCubic(cubic);
                Integer index = fillMapIndex.get(carCode);
                if(index != null){
                    Long fillCount = Long.parseLong(fillList.get(index).get("totalFill").toString());
                    total.setFillCount(total.getFillCount() + fillCount);
                    Long fillAmount = Long.parseLong(fillList.get(index).get("totalAmount").toString());
                    total.setFillAmount(total.getFillAmount() + fillAmount);
                    report.setFillCount(fillCount);
                    report.setFillAmount(fillAmount);
                    BigDecimal percentOfUsing = fillCount != 0 ? new BigDecimal((float) report.getTotalCount() / (fillCount / 1000L)).setScale(2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
                    report.setPercentOfUsing(percentOfUsing);
                }
                reportList.add(report);
            }
        }
        List<Map> earlyOndutyCount = projectCarWorkInfoServiceI.getOnDutyCountByProjectIdAndTime(projectId, startDate, endDate, Shift.Early.getAlias());
        List<Map> nightOndutyCount = projectCarWorkInfoServiceI.getOnDutyCountByProjectIdAndTime(projectId, startDate, endDate, Shift.Night.getAlias());
        total.setEarlyOnDutyCount(earlyOndutyCount.size());
        total.setNightOnDutyCount(nightOndutyCount.size());
        total.setOnDutyCount(total.getEarlyOnDutyCount() + total.getNightOnDutyCount());
        BigDecimal avgFillTotal = total.getTotalCount() != 0 ? new BigDecimal((float) total.getFillCount() / total.getTotalCount()).setScale(2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        total.setAvgFill(avgFillTotal);
        total.setShouldPayAmount(total.getAmount() - total.getFillAmount());
        BigDecimal percentOfUsingTotal = total.getFillCount() != 0 ? new BigDecimal((float) total.getTotalCount() / (total.getFillCount() / 1000L)).setScale(2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        total.setPercentOfUsing(percentOfUsingTotal);
        total.setDetailDistance(JSON.toJSONString(detailDistance));
        total.setCarCount(AutoApiUtils.returnProjectCar().getCountByProjectId(projectId));
        BigDecimal earlyAttendance = total.getOnDutyCount() != 0 ? new BigDecimal((float) total.getEarlyOnDutyCount() / total.getOnDutyCount()).setScale(2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        total.setEarlyAttendance(earlyAttendance);
        BigDecimal nightAttendance = total.getOnDutyCount() != 0 ? new BigDecimal((float) total.getNightOnDutyCount() / total.getOnDutyCount()).setScale(2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        total.setNightAttendance(nightAttendance);
        Integer coalCount = projectCarWorkInfoServiceI.getCoalCountByTime(projectId, startDate, endDate);
        total.setCoalCount(coalCount);
        Long grossProfit = total.getTotalCount() != 0 ? total.getShouldPayAmount() / total.getTotalCount() : 0L;
        total.setGrossProfit(grossProfit);
        BigDecimal avgCars = total.getOnDutyCount() != 0 ? new BigDecimal((float) total.getTotalCount() / total.getOnDutyCount()).setScale(2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        total.setAvgCars(avgCars);
        Map map = new HashMap();
        map.put("detail", reportList);
        map.put("total", total);
        resultList.add(map);
        return Result.ok(resultList);
    }
}
