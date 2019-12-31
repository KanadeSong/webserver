package com.seater.smartmining.entity;

import com.seater.smartmining.enums.SlagSiteEnum;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "deviceUid"}, name = "repeatCheck")})
public class ProjectSlagSite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;    //参与的项目编号

    @Column
    private String slagSiteCode = "";

    @Column(nullable = false)
    private String name = "";   //场地名称

    @Column
    private String description = "";    //场地説明

    @Column(nullable = false)
    private Long distance = 0L;   //距离

    @Column(nullable = false)
    private Long deviceId = 0L; //设备ID

    @Column(nullable = false)
    private String deviceCode = ""; //设备编号

    @Column(nullable = false)
    private String deviceUid = "";              //设备UID

    @Column(nullable = false)
    private Long swipeIntervent = 300000L;      //刷卡间格

    @Column
    private SlagSiteEnum slagSite = SlagSiteEnum.UNKNOW;

    @Column
    private String managerId = "";

    @Column
    private String managerName = "";

    @Column
    private String materialId = "";   //物料ID

    @Column
    private String materialName = "";       //物料名称

    @Column(scale = 6)
    private BigDecimal longitude = BigDecimal.ZERO;     //经度

    @Column(scale = 6)
    private BigDecimal latitude = BigDecimal.ZERO;      //纬度

    @Column
    private Long radius = 0L;           //半径

    @Column
    private Long radiusByPhone = 0L;        //对讲机的半径

    @Column
    public Integer curCarNum = 10;        //当班车数

    @Column
    public Integer defaultCarNum = 10;        //设定车数

    @Column
    public Integer intervalTime = 10;       //派车间隔/分



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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Long getSwipeIntervent() {
        return swipeIntervent;
    }

    public void setSwipeIntervent(Long swipeIntervent) {
        this.swipeIntervent = swipeIntervent;
    }

    public SlagSiteEnum getSlagSite() {
        return slagSite;
    }

    public void setSlagSite(SlagSiteEnum slagSite) {
        this.slagSite = slagSite;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
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

    public Long getRadius() {
        return radius;
    }

    public void setRadius(Long radius) {
        this.radius = radius;
    }

    public Long getRadiusByPhone() {
        return radiusByPhone;
    }

    public void setRadiusByPhone(Long radiusByPhone) {
        this.radiusByPhone = radiusByPhone;
    }

    public String getSlagSiteCode() {
        return slagSiteCode;
    }

    public void setSlagSiteCode(String slagSiteCode) {
        this.slagSiteCode = slagSiteCode;
    }



    public Integer getCurCarNum() {
        return curCarNum;
    }

    public void setCurCarNum(Integer curCarNum) {
        this.curCarNum = curCarNum;
    }


    public Integer getDefaultCarNum() {
        return defaultCarNum;
    }

    public void setDefaultCarNum(Integer defaultCarNum) {
        this.defaultCarNum = defaultCarNum;
    }


    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }
}
