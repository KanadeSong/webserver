package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.ShiftsEnums;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ProjectCarFillLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private Long projectId = null;    //参与的项目编号

    @Column(nullable = false)
    private Long carId = 0L;     //车辆ID

    @Column(nullable = false)
    private String carCode = "";        //项目中的车辆编号

    @Column(nullable = false)
    private CarType carType = CarType.Unknow;   //车辆类型

    @Column(nullable = false)
    private Date date = new Date();           //加油日期

    @Column(nullable = false)
    private Long volumn = 0L;                   //加油量

    @Column(nullable = false)
    private Long amount = 0L;              //加油金额

    @Column(nullable = false)
    private Boolean isVaild = true; //是否有效

    @Column
    private Long startOilMeter = 0L;    //开始油量码表

    @Column
    private Long endOilMeter = 0L;      //结束油量码表
    
    @Column(nullable = true)
    private Long managerId;    //  操作人id

    @Column(nullable = true)
    private String managerName;  //  操作人名称
    
    @Column(nullable = true)
    private Long oilCarId = 0L;      //  油车id
    
    @Column(nullable = true)
    private String oilCarCode = "";  //  油车编号
    
    @Column
    private String remark = "";     //  加油备注

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;    //  班次

    @Column
    private String eventId = "";   //  上传上来的事件id

    @Column
    private Date dateIdentification = null;  //  当班日期(当前班次对应日期)

    //计价方式
    @Column
    @Enumerated(EnumType.ORDINAL)
    private PricingTypeEnums pricingTypeEnums = PricingTypeEnums.Unknow;   //计价方式

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

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getVolumn() {
        return volumn;
    }

    public void setVolumn(Long volumn) {
        this.volumn = volumn;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }

    public Long getStartOilMeter() {
        return startOilMeter;
    }

    public void setStartOilMeter(Long startOilMeter) {
        this.startOilMeter = startOilMeter;
    }

    public Long getEndOilMeter() {
        return endOilMeter;
    }

    public void setEndOilMeter(Long endOilMeter) {
        this.endOilMeter = endOilMeter;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Long getOilCarId() {
        return oilCarId;
    }

    public void setOilCarId(Long oilCarId) {
        this.oilCarId = oilCarId;
    }

    public String getOilCarCode() {
        return oilCarCode;
    }

    public void setOilCarCode(String oilCarCode) {
        this.oilCarCode = oilCarCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ShiftsEnums getShift() {
        return shifts;
    }

    public void setShift(ShiftsEnums shifts) {
        this.shifts = shifts;
    }

    public ShiftsEnums getShifts() {
        return shifts;
    }

    public void setShifts(ShiftsEnums shifts) {
        this.shifts = shifts;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getDateIdentification() {
        return dateIdentification;
    }

    public void setDateIdentification(Date dateIdentification) {
        this.dateIdentification = dateIdentification;
    }

    public PricingTypeEnums getPricingTypeEnums() {
        return pricingTypeEnums;
    }

    public void setPricingTypeEnums(PricingTypeEnums pricingTypeEnums) {
        this.pricingTypeEnums = pricingTypeEnums;
    }
}
