package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Project  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private String name = "";                                       //项目名称

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectType projectType = ProjectType.Unknown;      //项目类型

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;           //调度模式

    @Column
    private Date startTime = null;          //开始时间

    @Column
    private Date endTime = null;            //结束时间

    @Column(nullable = false)
    private Boolean isEnd = false;          //是否结束

    @Column(nullable = false)
    private Long oilPirce = 0L;           //油价(分/升)

    @Column(nullable = false)
    private Date earlyStartTime = new Time(-7200000L);   //早班开始时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint earlyEndPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    private Date earlyEndTime = new Time(35999000L);   //早班结束时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint nightStartPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    private Date nightStartTime = new Time(36000000);   //晚班开始时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint nightEndPoint = ProjectWorkTimePoint.Tomorrow;

    @Column(nullable = false)
    private Date nightEndTime = new Time(-7201000);   //晚班结束时间

  @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectStatus status = ProjectStatus.Unknown; //状态

    @Column(nullable = false)
    private Date addTime = new Date();      //添加时间
    
    @Column
    private String avatar;      //  项目图标

    @Column(scale = 6)
    private BigDecimal longitude = BigDecimal.ZERO;     //经度

    @Column(scale = 6)
    private BigDecimal latitude = BigDecimal.ZERO;      //纬度

    @Column
    private String address = "";            //地区

    @Column
    private String detailAddress = "";      //详情地址

    @Column
    private String rootUser = "";           //根用户

    @Column
    private String chargePerson = "";       //负责人

    @Column
    private String contact = "";        //联系方式

    @Column
    private Integer reportDay = 30;

    private Date beginDate = null;

    private Date endDate = null;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public ProjectDispatchMode getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(ProjectDispatchMode dispatchMode) {
        this.dispatchMode = dispatchMode;
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

    public Boolean getEnd() {
        return isEnd;
    }

    public void setEnd(Boolean end) {
        isEnd = end;
    }

    public Long getOilPirce() {
        return oilPirce;
    }

    public void setOilPirce(Long oilPirce) {
        this.oilPirce = oilPirce;
    }

    public Date getEarlyStartTime() {
        return earlyStartTime;
    }

    public void setEarlyStartTime(Date earlyStartTime) {
        this.earlyStartTime = earlyStartTime;
    }

    public ProjectWorkTimePoint getEarlyEndPoint() {
        return earlyEndPoint;
    }

    public void setEarlyEndPoint(ProjectWorkTimePoint earlyEndPoint) {
        this.earlyEndPoint = earlyEndPoint;
    }

    public Date getEarlyEndTime() {
        return earlyEndTime;
    }
    public void setEarlyEndTime(Date earlyEndTime) {
        this.earlyEndTime = earlyEndTime;
    }


    public ProjectWorkTimePoint getNightStartPoint() {
        return nightStartPoint;
    }

    public void setNightStartPoint(ProjectWorkTimePoint nightStartPoint) {
        this.nightStartPoint = nightStartPoint;
    }

    public Date getNightStartTime() {
        return nightStartTime;
    }

    public void setNightStartTime(Date nightStartTime) {
        this.nightStartTime = nightStartTime;
    }

    public ProjectWorkTimePoint getNightEndPoint() {
        return nightEndPoint;
    }

    public void setNightEndPoint(ProjectWorkTimePoint nightEndPoint) {
        this.nightEndPoint = nightEndPoint;
    }

    public Date getNightEndTime() {
        return nightEndTime;
    }

    public void setNightEndTime(Date nightEndTime) {
        this.nightEndTime = nightEndTime;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getRootUser() {
        return rootUser;
    }

    public void setRootUser(String rootUser) {
        this.rootUser = rootUser;
    }

    public String getChargePerson() {
        return chargePerson;
    }

    public void setChargePerson(String chargePerson) {
        this.chargePerson = chargePerson;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getReportDay() {
        return reportDay;
    }

    public void setReportDay(Integer reportDay) {
        this.reportDay = reportDay;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}



