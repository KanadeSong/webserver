package com.seater.smartmining.entity;

import com.seater.smartmining.enums.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "code"}, name = "repeatCheck")})
public class ProjectDiggingMachine  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private String code = "";        //项目中的挖机编号

    @Column
    private Long projectId = null;    //参与的项目编号

    @Column(nullable = false)
    private String driverId = "";     //司机编号

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

    @Column
    private Date startWorkTime = null;  //上机时间

    @Column
    private Date endWorkTime = null;    //停机时间


    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public DiggingMachineStatus status = DiggingMachineStatus.Stop; //状态

    @Column(nullable = false)
    private Boolean isVaild = true; //是否有效

    @Column
    private String uid = "";        //设备编号

    @Column
    private String icCardNumber = "";   //IC卡号

    @Column
    private Boolean icCardStatus = false;   //IC卡状态

    @Column
    private Long diggingMachineId;     //车主选进来项目的车的id

    @Column
    private CheckStatus checkStatus = CheckStatus.UnCheck;  //  进入项目时车的检查状态

    @Column
    private String interPhoneAccount = "";  //  相应对讲机账号
    
    @Column
    private String interPhoneAccountId = "";  //  相应对讲机账号id
    
    @Column
    private Boolean selected = false;    //是否排班

    public DiggingMachineStopStatus getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(DiggingMachineStopStatus stopStatus) {
        this.stopStatus = stopStatus;
    }

    //停机状态
    @Column
    private DiggingMachineStopStatus stopStatus = DiggingMachineStopStatus.Normal;

    @Column
    private Date addTime = new Date();      //创建时间

    @Column
    private Long placeId = 0L;      //平台ID

    @Column
    private String placeName = "";      //平台名称

    @Column
    private StartEnum startMode = StartEnum.UNKONW;         //开机方式

    @Column
    private StopEnum stopMode = StopEnum.UNKNOW;            //下机方式

    @Column
    private String picturePath = "";        //车照

    @Column
    private String machineName = "";        //挖机名称

    @Column
    public Integer realCapacity = 2;        //实时产能

    @Column
    public Integer defaultCapacity = 2;        //默认产能

    @Column
    public Integer minCapacity = 2;        //最小产能

    @Column
    public Integer maxCapacity = 10;        //最大产能

    @Column
    public Integer intervalTime = 10;       //开工派车间隔/分

    @Column
    private String payBody = "";        //付款人

    @Column
    private Long shopId = 0L;       //商品ID

    @Column
    private String shopName = "";       //商品名称

    @Column
    private Boolean deducted = false;       //是否缴费

    @Column
    private Date deductedDate = null;       //缴费日期

    @Column
    private Date expireDate = null;         //到期日期

    public BigDecimal getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(BigDecimal efficiency) {
        this.efficiency = efficiency;
    }

    @Column
    private BigDecimal efficiency = BigDecimal.ZERO;        //效率

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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
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

    public Date getStartWorkTime() {
        return startWorkTime;
    }

    public void setStartWorkTime(Date startWorkTime) {
        this.startWorkTime = startWorkTime;
    }

    public Date getEndWorkTime() {
        return endWorkTime;
    }

    public void setEndWorkTime(Date endWorkTime) {
        this.endWorkTime = endWorkTime;
    }

    public DiggingMachineStatus getStatus() {
        return status;
    }

    public void setStatus(DiggingMachineStatus status) {
        this.status = status;
    }

    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIcCardNumber() {
        return icCardNumber;
    }

    public void setIcCardNumber(String icCardNumber) {
        this.icCardNumber = icCardNumber;
    }

    public Boolean getIcCardStatus() {
        return icCardStatus;
    }

    public void setIcCardStatus(Boolean icCardStatus) {
        this.icCardStatus = icCardStatus;
    }

    public Long getDiggingMachineId() {
        return diggingMachineId;
    }

    public void setDiggingMachineId(Long diggingMachineId) {
        this.diggingMachineId = diggingMachineId;
    }

    public CheckStatus getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(CheckStatus checkStatus) {
        this.checkStatus = checkStatus;
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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public StartEnum getStartMode() {
        return startMode;
    }

    public void setStartMode(StartEnum startMode) {
        this.startMode = startMode;
    }

    public StopEnum getStopMode() {
        return stopMode;
    }

    public void setStopMode(StopEnum stopMode) {
        this.stopMode = stopMode;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }


    public Integer getRealCapacity() {
        return realCapacity;
    }

    public void setRealCapacity(Integer realCapacity) {
        this.realCapacity = realCapacity;
    }


    public Integer getDefaultCapacity() {
        return defaultCapacity;
    }

    public void setDefaultCapacity(Integer defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
    }


    public Integer getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(Integer minCapacity) {
        this.minCapacity = minCapacity;
    }


    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }


    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String getPayBody() {
        return payBody;
    }

    public void setPayBody(String payBody) {
        this.payBody = payBody;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

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

}
