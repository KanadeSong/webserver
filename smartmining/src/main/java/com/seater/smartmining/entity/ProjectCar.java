package com.seater.smartmining.entity;

import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.enums.ProjectCarStatus;
import com.seater.smartmining.utils.schedule.AutoScheduleType;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "code"}, name = "repeatCheck")})
public class ProjectCar implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private String code = "";        //项目中的车辆编号

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

    @Column(nullable = false)
    private Integer length = 0; //车厢长度

    @Column(nullable = false)
    private Integer width = 0; //车厢宽度

    @Column(nullable = false)
    private Integer height = 0; //车厢高度

    @Column(nullable = false)
    private Integer thickness = 0; //车厢底板厚度

    @Column(nullable = false)
    private Long calcCapacity = 0L; //计算容量

    @Column(nullable = false)
    private Long modifyCapacity = 0L; //修正容量

    @Column
    private String icCardNumber = "";   //IC卡号

    @Column
    private Boolean icCardStatus = false;   //IC卡状态

    @Column(nullable = false)
    private Boolean isVaild = true; //是否有效

    @Column
    private String uid = "";        //设备编号

    @Column
    private Long slagCarId;         //  车主选择进入项目的车的id

    @Column
    private CheckStatus checkStatus = CheckStatus.UnCheck;  //  车主车进入项目的检查状态

    @Column
    private String interPhoneAccount = "";  //  相应对讲机账号

    @Column
    private String interPhoneAccountId = "";  //  相应对讲机账号id
    
    @Column
    private Boolean seleted = false;        //是否排班

    @Column
    private Date addTime = new Date();

    @Column
    private String carName = "";

    @Column
    private String picturePath = "";        //车照

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

    @Column
    private ProjectCarStatus status = ProjectCarStatus.Unknow;      //渣车状态

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    @Column
    private String prepayId = "";       //小程序预支付ID  到期续费提醒时使用

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

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getThickness() {
        return thickness;
    }

    public void setThickness(Integer thickness) {
        this.thickness = thickness;
    }

    public Long getCalcCapacity() {
        return calcCapacity;
    }

    public void setCalcCapacity(Long calcCapacity) {
        this.calcCapacity = calcCapacity;
    }

    public Long getModifyCapacity() {
        return modifyCapacity;
    }

    public void setModifyCapacity(Long modifyCapacity) {
        this.modifyCapacity = modifyCapacity;
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

    public Long getSlagCarId() {
        return slagCarId;
    }

    public void setSlagCarId(Long slagCarId) {
        this.slagCarId = slagCarId;
    }

    public CheckStatus getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(CheckStatus checkStatus) {
        this.checkStatus = checkStatus;
    }

    public Boolean getSeleted() {
        return seleted;
    }

    public void setSeleted(Boolean seleted) {
        this.seleted = seleted;
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

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
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

    public ProjectCarStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectCarStatus status) {
        this.status = status;
    }
}


