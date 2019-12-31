package com.seater.smartmining.entity;

import com.seater.smartmining.enums.AlarmInfoEnum;
import com.seater.smartmining.enums.FaultInfoEnum;
import com.seater.smartmining.enums.ProjectDeviceType;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"uid"}, name = "repeatCheck")})
public class ProjectDevice  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column/*(nullable = false)*/
    private String code = "";              //项目中的设备

    @Column
    private Long projectId = 0L;       //参与的项目编号

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectDeviceType deviceType = ProjectDeviceType.Unknown;     //设备类型

    @Column(nullable = false)
    private String uid = "";              //设备UID

    @Column
    private String deviceCode = "";     //终端编号

    @Column
    private String phoneNumber = "";    //设备电话号话

    @Column(scale = 6)
    private BigDecimal longitude = BigDecimal.ZERO;     //经度

    @Column(scale = 6)
    private BigDecimal latitude = BigDecimal.ZERO;      //纬度

    @Column(scale = 6)
    private BigDecimal altitude = BigDecimal.ZERO;      //海拔高度

    @Column(scale = 4)
    private BigDecimal distance = BigDecimal.ZERO;      //距离

    @Column
    private String softwareVersion = "";            //软件版本号

    @Column
    private String hardwareVersion = "";            //硬件版本号

    @Column
    private String imgUrl = "";           //图片

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectDeviceStatus status = ProjectDeviceStatus.OffLine;     //设备状态 默认离线

    @Column(nullable = false)
    private Boolean isVaild = true; //是否有效

    @Column
    private AlarmInfoEnum alarmInfos = AlarmInfoEnum.Unknow;       //警告信息  todo 具体定义待定

    @Column
    private FaultInfoEnum faultInfos = FaultInfoEnum.Unknow;       //故障信息  todo 具体定义待定

    @Column
    private Long versionId = 0L;        //版本编号

    @Column
    private String fileName = "";       //版本文件名称

    @Column
    private String productionBatchNumber = "";      //生产批次号

    @Column
    private Date createDate = null;         //创建日期

    @Column
    private Long carId = 0L;

    @Column
    private String iccid = "";

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    @Column
    private Date lastDate = new Date(0);        //最后一次离线时间

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getProductionBatchNumber() {
        return productionBatchNumber;
    }

    public void setProductionBatchNumber(String productionBatchNumber) {
        this.productionBatchNumber = productionBatchNumber;
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

    public ProjectDeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(ProjectDeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }

    public ProjectDeviceStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectDeviceStatus status) {
        this.status = status;
    }

    public AlarmInfoEnum getAlarmInfos() {
        return alarmInfos;
    }

    public void setAlarmInfos(AlarmInfoEnum alarmInfos) {
        this.alarmInfos = alarmInfos;
    }

    public FaultInfoEnum getFaultInfos() {
        return faultInfos;
    }

    public void setFaultInfos(FaultInfoEnum faultInfos) {
        this.faultInfos = faultInfos;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}
