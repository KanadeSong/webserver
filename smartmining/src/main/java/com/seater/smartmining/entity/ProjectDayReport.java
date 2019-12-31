package com.seater.smartmining.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ProjectDayReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;                    //项目ID

    @Column
    private Date reportDate = new Date(0L);         //报表日期

    @Column
    private Date createDate = new Date(0L);

    @Column
    private Integer totalCount = 0;                 //当天所有车总车数

    @Column
    private Integer grandTotalCount = 0;                 //本月所有车总车数

    @Column
    private Long cubicPerTimes = 0L;       //所有车平均每车立方数

    @Column
    private Long totalCubic = 0L;         //所有车当天总方量

    @Column
    private Long grandTotalCubic = 0L;    //所有车本月累计总方量

    @Column
    private Long totalFill = 0L;          //所有车当天总加油量

    @Column
    private Long grandTotalFill = 0L;     //所有车本月累计总加油量

    @Column
    private Long avgUsing = 0L;            //所有车当天平均用油量(毫升/车)

    @Column
    private Long grandTotalAvgUsing = 0L; //所有车本月累计平均用油量(毫升/车)

    @Column
    private Long totalAmount = 0L;        //所有车总金额(分)

    @Column
    private Long grandTotalAmount = 0L;   //所有车本月累计总金额(分)

    @Column
    private Long totalAmountFill = 0L;   //所有车当天总加油金额

    @Column
    private Long grandTotalAmountFill = 0L; //所有车本月累计总加油金额

    @Column
    private Long payable = 0L;        //所有车当天总应付金额

    @Column
    private Long grandTotalPayable = 0L;  //所有车本月累计总应付金额

    @Column
    private Long mileage = 0L;        //所有车当日总里程

    @Column
    private Long grandTotalMileage = 0L; //本月累计里程

    @Column
    private Long grandTotalAvgMileage = 0L; //平均累计里程(米/车)

    @Column
    private Long percentOfUsing  = 0L;      //当日油耗(%)

    @Column
    private Long percentOfMonthUsing = 0L; //本月油耗(%)

//出勤统计
    @Column
    private Integer earlyTotalCount = 0;    //日班总车数

    @Column
    private Integer nightTotalCount = 0;    //夜班总车数

    @Column
    private Integer avgCountsPerCarPerDay = 0; //平均每部车每天车数

    @Column
    private Integer projectTotalCar = 0;    //项总总车辆数

    @Column
    private Integer onDutyCount = 0;        //总出勤数

    @Column
    private Integer earlyOnDutyCount = 0;   //早班出勤数

    @Column
    private Integer earlyAttendance = 0;    //早班出勤(0.00%)

    @Column
    private Integer nightOnDutyCount = 0;   //晚班出勤数

    @Column
    private Integer nightAttendance = 0;    //晚班出勤(0.00%)

//煤车
    @Column
    private Integer coalCount = 0;          //当日煤车数

    @Column
    private Integer grandTotalCoalCount = 0; //本月累计煤车数

    @Column
    private Integer historyCoalCount = 0;   //历史煤车数

//利润
    @Column
    private Long grossProfit = 0L;        //当日毛利(分)

    @Column
    private  Long monthGrossProfit = 0L; //本月毛利(分)

//
//    @OneToMany
//    private List<ProjectDayReportPartDistance> distances = new ArrayList<ProjectDayReportPartDistance>();

    @Column
    private Integer _totalCount = 0; //所有运距当日车数和

    @Column
    private Long _totalCubic = 0L; //所有运距当日方量和(两位小数)

    @Column
    private Long _totalAmount = 0L;   //所有运距当日金额和(两位小数)

    @Column
    private Integer _grandTotalCount = 0;//所有运距本月车数和

    @Column
    private Long _grandTotalCubic = 0L; //所有运距本月方量和(两位小数)

    @Column
    private Long _grandTotalAmount = 0L; //所有运距本月金额和(两位小数)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

//    public List<ProjectDayReportPartCar> getCars() {
//        return cars;
//    }
//
//    public void setCars(List<ProjectDayReportPartCar> cars) {
//        this.cars = cars;
//    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getGrandTotalCount() {
        return grandTotalCount;
    }

    public void setGrandTotalCount(Integer grandTotalCount) {
        this.grandTotalCount = grandTotalCount;
    }

    public Long getCubicPerTimes() {
        return cubicPerTimes;
    }

    public void setCubicPerTimes(Long cubicPerTimes) {
        this.cubicPerTimes = cubicPerTimes;
    }

    public Long getTotalCubic() {
        return totalCubic;
    }

    public void setTotalCubic(Long totalCubic) {
        this.totalCubic = totalCubic;
    }

    public Long getGrandTotalCubic() {
        return grandTotalCubic;
    }

    public void setGrandTotalCubic(Long grandTotalCubic) {
        this.grandTotalCubic = grandTotalCubic;
    }

    public Long getTotalFill() {
        return totalFill;
    }

    public void setTotalFill(Long totalFill) {
        this.totalFill = totalFill;
    }

    public Long getGrandTotalFill() {
        return grandTotalFill;
    }

    public void setGrandTotalFill(Long grandTotalFill) {
        this.grandTotalFill = grandTotalFill;
    }

    public Long getAvgUsing() {
        return avgUsing;
    }

    public void setAvgUsing(Long avgUsing) {
        this.avgUsing = avgUsing;
    }

    public Long getGrandTotalAvgUsing() {
        return grandTotalAvgUsing;
    }

    public void setGrandTotalAvgUsing(Long grandTotalAvgUsing) {
        this.grandTotalAvgUsing = grandTotalAvgUsing;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getGrandTotalAmount() {
        return grandTotalAmount;
    }

    public void setGrandTotalAmount(Long grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }

    public Long getTotalAmountFill() {
        return totalAmountFill;
    }

    public void setTotalAmountFill(Long totalAmountFill) {
        this.totalAmountFill = totalAmountFill;
    }

    public Long getGrandTotalAmountFill() {
        return grandTotalAmountFill;
    }

    public void setGrandTotalAmountFill(Long grandTotalAmountFill) {
        this.grandTotalAmountFill = grandTotalAmountFill;
    }

    public Long getPayable() {
        return payable;
    }

    public void setPayable(Long payable) {
        this.payable = payable;
    }

    public Long getGrandTotalPayable() {
        return grandTotalPayable;
    }

    public void setGrandTotalPayable(Long grandTotalPayable) {
        this.grandTotalPayable = grandTotalPayable;
    }

    public Long getMileage() {
        return mileage;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }

    public Long getGrandTotalMileage() {
        return grandTotalMileage;
    }

    public void setGrandTotalMileage(Long grandTotalMileage) {
        this.grandTotalMileage = grandTotalMileage;
    }

    public Long getGrandTotalAvgMileage() {
        return grandTotalAvgMileage;
    }

    public void setGrandTotalAvgMileage(Long grandTotalAvgMileage) {
        this.grandTotalAvgMileage = grandTotalAvgMileage;
    }

    public Long getPercentOfUsing() {
        return percentOfUsing;
    }

    public void setPercentOfUsing(Long percentOfUsing) {
        this.percentOfUsing = percentOfUsing;
    }

    public Long getPercentOfMonthUsing() {
        return percentOfMonthUsing;
    }

    public void setPercentOfMonthUsing(Long percentOfMonthUsing) {
        percentOfMonthUsing = percentOfMonthUsing;
    }

    public Integer getEarlyTotalCount() {
        return earlyTotalCount;
    }

    public void setEarlyTotalCount(Integer earlyTotalCount) {
        this.earlyTotalCount = earlyTotalCount;
    }

    public Integer getNightTotalCount() {
        return nightTotalCount;
    }

    public void setNightTotalCount(Integer nightTotalCount) {
        this.nightTotalCount = nightTotalCount;
    }

    public Integer getAvgCountsPerCarPerDay() {
        return avgCountsPerCarPerDay;
    }

    public void setAvgCountsPerCarPerDay(Integer avgCountsPerCarPerDay) {
        this.avgCountsPerCarPerDay = avgCountsPerCarPerDay;
    }

    public Integer getProjectTotalCar() {
        return projectTotalCar;
    }

    public void setProjectTotalCar(Integer projectTotalCar) {
        this.projectTotalCar = projectTotalCar;
    }

    public Integer getOnDutyCount() {
        return onDutyCount;
    }

    public void setOnDutyCount(Integer onDutyCount) {
        this.onDutyCount = onDutyCount;
    }

    public Integer getEarlyOnDutyCount() {
        return earlyOnDutyCount;
    }

    public void setEarlyOnDutyCount(Integer earlyOnDutyCount) {
        this.earlyOnDutyCount = earlyOnDutyCount;
    }

    public Integer getEarlyAttendance() {
        return earlyAttendance;
    }

    public void setEarlyAttendance(Integer earlyAttendance) {
        this.earlyAttendance = earlyAttendance;
    }

    public Integer getNightOnDutyCount() {
        return nightOnDutyCount;
    }

    public void setNightOnDutyCount(Integer nightOnDutyCount) {
        this.nightOnDutyCount = nightOnDutyCount;
    }

    public Integer getNightAttendance() {
        return nightAttendance;
    }

    public void setNightAttendance(Integer nightAttendance) {
        this.nightAttendance = nightAttendance;
    }

    public Integer getCoalCount() {
        return coalCount;
    }

    public void setCoalCount(Integer coalCount) {
        this.coalCount = coalCount;
    }

    public Integer getGrandTotalCoalCount() {
        return grandTotalCoalCount;
    }

    public void setGrandTotalCoalCount(Integer grandTotalCoalCount) {
        this.grandTotalCoalCount = grandTotalCoalCount;
    }

    public Integer getHistoryCoalCount() {
        return historyCoalCount;
    }

    public void setHistoryCoalCount(Integer historyCoalCount) {
        this.historyCoalCount = historyCoalCount;
    }

    public Long getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(Long grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Long getMonthGrossProfit() {
        return monthGrossProfit;
    }

    public void setMonthGrossProfit(Long monthGrossProfit) {
        this.monthGrossProfit = monthGrossProfit;
    }

//    public List<ProjectDayReportPartDistance> getDistances() {
//        return distances;
//    }
//
//    public void setDistances(List<ProjectDayReportPartDistance> distances) {
//        this.distances = distances;
//    }

    public Integer get_totalCount() {
        return _totalCount;
    }

    public void set_totalCount(Integer _totalCount) {
        this._totalCount = _totalCount;
    }

    public Long get_totalCubic() {
        return _totalCubic;
    }

    public void set_totalCubic(Long _totalCubic) {
        this._totalCubic = _totalCubic;
    }

    public Long get_totalAmount() {
        return _totalAmount;
    }

    public void set_totalAmount(Long _totalAmount) {
        this._totalAmount = _totalAmount;
    }

    public Integer get_grandTotalCount() {
        return _grandTotalCount;
    }

    public void set_grandTotalCount(Integer _grandTotalCount) {
        this._grandTotalCount = _grandTotalCount;
    }

    public Long get_grandTotalCubic() {
        return _grandTotalCubic;
    }

    public void set_grandTotalCubic(Long _grandTotalCubic) {
        this._grandTotalCubic = _grandTotalCubic;
    }

    public Long get_grandTotalAmount() {
        return _grandTotalAmount;
    }

    public void set_grandTotalAmount(Long _grandTotalAmount) {
        this._grandTotalAmount = _grandTotalAmount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


}
