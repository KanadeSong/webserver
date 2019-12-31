package com.seater.smartmining.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ProjectDayReportPartCar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;                    //项目ID

    @Column
    private Long reportId = 0L;            //日报ID

    @Column
    private Date reportDate = new Date(0L); //日报日期

    @Column
    private Long carId = 0L;                  //车辆ID

    @Column
    private String carCode = "";              //车辆编号

    @Column
    private Long carOwnerId = 0L;             //车主ID

    @Column
    private String carOwnerName = "";         //车主名

    @Column
    private String earlyCountList = "";    //早班各运距车数

    @Column
    private String nightCountList = "";     //晚班各运距车数

    @Column
    private Integer earlyTotalCount = 0;     //早班总车数

    @Column
    private Integer nightTotalCount = 0;     //晚班总车数

    @Column
    private Integer totalCount = 0;          //当天总车数

    @Column
    private Integer grandTotalCount = 0;     //本月累计车数

    @Column
    private Long cubicPerTimes = 0L;       //每车立方数

    @Column
    private Long totalCubic = 0L;         //当天方量

    @Column
    private Long grandTotalCubic = 0L;    //本月累计方量

    @Column
    private Long totalFill = 0L;          //当天加油量

    @Column
    private Long grandTotalFill = 0L;     //本月累计加油量

    @Column
    private Long avgUsing = 0L;            //当天平均用油量(毫升/车)

    @Column
    private Long grandTotalAvgUsing = 0L; //本月累计平均用油量(毫升/车)

    @Column
    private Long totalAmount = 0L;        //总金额(分)

    @Column
    private Long grandTotalAmount = 0L;   //本月累计总金额(分)

    @Column
    private Long totalAmountFill = 0L;   //当天加油金额

    @Column
    private Long grandTotalAmountFill = 0L; //本月累计加油金额

    @Column
    private Long payable = 0L;        //当天应付金额

    @Column
    private Long grandTotalPayable = 0L;  //本月累计应付金额

    @Column
    private Long mileage = 0L;        //当日里程

    @Column
    private Long grandTotalMileage = 0L; //本月累计里程

    @Column
    private Long grandTotalAvgMileage = 0L; //平均累计里程(米/车)

    @Column
    private Long percentOfUsing  = 0L;      //当日油耗(0L.0L0L%)

    @Column
    private Long PercentOfMonthUsing = 0L; //本月油耗(0L.0L0L%)

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

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public Long getCarOwnerId() {
        return carOwnerId;
    }

    public void setCarOwnerId(Long carOwnerId) {
        this.carOwnerId = carOwnerId;
    }

    public String getCarOwnerName() {
        return carOwnerName;
    }

    public void setCarOwnerName(String carOwnerName) {
        this.carOwnerName = carOwnerName;
    }

    public String getEarlyCountList() {
        return earlyCountList;
    }

    public void setEarlyCountList(String earlyCountList) {
        this.earlyCountList = earlyCountList;
    }

    public String getNightCountList() {
        return nightCountList;
    }

    public void setNightCountList(String nihtCountList) {
        this.nightCountList = nihtCountList;
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
        return PercentOfMonthUsing;
    }

    public void setPercentOfMonthUsing(Long percentOfMonthUsing) {
        PercentOfMonthUsing = percentOfMonthUsing;
    }

}


