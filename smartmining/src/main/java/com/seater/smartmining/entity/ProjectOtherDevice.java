package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "code", "carType"}, name = "repeatCheck")})
public class ProjectOtherDevice  implements Serializable {

    private static final long serialVersionUID = -7612144849363985876L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    private String name = "";        //设备名称

    @Column(nullable = false)
    private String code = "";        //项目中的设备编号

    @Column
    private Long projectId = null;    //参与的项目编号

    @Column(nullable = false)
    private Long driverId = 0L;     //司机编号

    @Column(nullable = false)
    private String driverName = ""; //司机名称

    @Column(nullable = false)
    private Long ownerId = 0L;      //车主编号

    @Column(nullable = false)
    private String ownerName = ""; //车主名称

    @Column(nullable = false)
    private Long brandId = 0L;      //品牌ID

    @Column(nullable = false)
    private String brandName = ""; //品牌名

    @Column(nullable = false)
    private Long modelId = 0L;      //型号ID

    @Column(nullable = false)
    private String modelName = ""; //型号名

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectOtherDeviceStatusEnum status = ProjectOtherDeviceStatusEnum.Stop; //状态

    @Column(nullable = false)
    private Boolean isVaild = true; //是否有效

    @Column(nullable = false)
    private CarType carType = CarType.Unknow;   //  车类型

    @Column
    private Long managerId;         //  该油车的管理人员 使用加油app的人

    @Column
    private String managerName;         //  该油车的管理人员名称

    @Column
    private String managerNames;         //  该油车的管理人员名称数组

    @Column
    private String managerIds;         //  该油车的管理人员名称数组

    @Column
    private Date addTime = new Date();  //  添加日期

    @Column
    private String interPhoneAccount = "";  //  相应对讲机账号

    @Column
    private String interPhoneAccountId = "";  //  相应对讲机账号id

    @Column
    private Long endOilMeterPort1 = 0L;      //当日油表终止数(单位:毫升)

    @Column
    private Long endOilMeterPort2 = 0L;      //当日油表终止数(单位:毫升)

    @Column
    private String picturePath = "";        //车照

    @Column
    private Date startTime = null;

    @Column
    private Date endTime = null;

    @Column
    private BigDecimal workTime = BigDecimal.ZERO;

    @Column
    private String uid = "";

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectDeviceStatus deviceStatus = ProjectDeviceStatus.OffLine;     //设备状态 默认离线

    @Column
    private Boolean deducted = false;       //是否缴费

    @Column
    private Date deductedDate = null;       //缴费日期

    @Column
    private Date expireDate = null;         //到期日期

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    @Column
    private Date lastDate = new Date(0);        //最后一次离线时间

    public Boolean getDeducted() {
        return deducted;
    }

    public void setDeducted(Boolean deducted) {
        this.deducted = deducted;
    }

    public Date getDeductedDate() {
        return deductedDate;
    }

    public void setDeductedDate(Date deductedDate) {
        this.deductedDate = deductedDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public ProjectOtherDeviceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ProjectOtherDeviceStatusEnum status) {
        this.status = status;
    }

    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
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


    public String getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(String managerIds) {
        this.managerIds = managerIds;
    }


    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }


    public String getManagerNames() {
        return managerNames;
    }

    public void setManagerNames(String managerNames) {
        this.managerNames = managerNames;
    }


    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getInterPhoneAccount() {
        return interPhoneAccount;
    }

    public void setInterPhoneAccount(String interPhoneAccount) {
        this.interPhoneAccount = interPhoneAccount;
    }

    public String getInterPhoneAccountId() {
        return interPhoneAccountId;
    }

    public void setInterPhoneAccountId(String interPhoneAccountId) {
        this.interPhoneAccountId = interPhoneAccountId;
    }

    public Long getEndOilMeterPort1() {
        return endOilMeterPort1;
    }

    public void setEndOilMeterPort1(Long endOilMeterPort1) {
        this.endOilMeterPort1 = endOilMeterPort1;
    }

    public Long getEndOilMeterPort2() {
        return endOilMeterPort2;
    }

    public void setEndOilMeterPort2(Long endOilMeterPort2) {
        this.endOilMeterPort2 = endOilMeterPort2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getWorkTime() {
        return workTime;
    }

    public void setWorkTime(BigDecimal workTime) {
        this.workTime = workTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ProjectDeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(ProjectDeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}
