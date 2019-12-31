package com.seater.smartmining.entity;

import com.seater.smartmining.enums.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 挖机工作时间实体类
 * @Author zenghang
 * @Date 2019/1/26 0026 13:27
 */
@Entity
@Table(name = "project_work_time_by_digging")
public class ProjectWorkTimeByDigging implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    //项目编号
    @Column
    private Long projectId = 0L;

    //设备编号
    @Column
    private Long materialId = 0L;

    @Column(nullable = false)
    private String materialCode = "";

    //设备名称
    @Column
    private String materialInfo = "";

    //工作开始时间
    @Column(nullable = false)
    private Date startTime = null;

    //工作结束时间
    @Column
    private Date endTime = null;

    //计价方式
    @Column
    @Enumerated(EnumType.ORDINAL)
    private PricingTypeEnums pricingTypeEnums = PricingTypeEnums.Unknow;   //计价方式

    //班次
    @Column
    private ShiftsEnums shift = ShiftsEnums.UNKNOW;

    //状态
    @Column
    @Enumerated(EnumType.ORDINAL)
    private DiggingMachineStatus status = DiggingMachineStatus.Unknow;

    @Column
    private Long workTime = 0L;         //工作时长

    @Column
    private Date createTime = null;                 //创建时间

    public DiggingMachineStopStatus getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(DiggingMachineStopStatus stopStatus) {
        this.stopStatus = stopStatus;
    }

    //停机状态
    @Column
    private DiggingMachineStopStatus stopStatus = DiggingMachineStopStatus.Normal;

    //开机方式
    @Column
    private StartEnum startMode = StartEnum.UNKONW;

    //关机方式
    @Column
    private StopEnum stopMode = StopEnum.UNKNOW;

    @Column
    private Long dataId = 0L;       //物料ID

    @Column
    private String dataName = "";       //物料名称

    @Column
    private Long placeId = 0L;      //工作平台ID

    @Column
    private String placeName = "";      //工作平台名称

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private String slagSiteId = "";

    @Column
    private String slagSiteName = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialInfo() {
        return materialInfo;
    }

    public void setMaterialInfo(String materialInfo) {
        this.materialInfo = materialInfo;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public PricingTypeEnums getPricingTypeEnums() {
        return pricingTypeEnums;
    }

    public void setPricingTypeEnums(PricingTypeEnums pricingTypeEnums) {
        this.pricingTypeEnums = pricingTypeEnums;
    }

    public ShiftsEnums getShift() {
        return shift;
    }

    public void setShift(ShiftsEnums shift) {
        this.shift = shift;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }

    public DiggingMachineStatus getStatus() {
        return status;
    }

    public void setStatus(DiggingMachineStatus status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
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

    public Date getDateIdentification() {
        return dateIdentification;
    }

    public void setDateIdentification(Date dateIdentification) {
        this.dateIdentification = dateIdentification;
    }

    public String getSlagSiteId() {
        return slagSiteId;
    }

    public void setSlagSiteId(String slagSiteId) {
        this.slagSiteId = slagSiteId;
    }

    public String getSlagSiteName() {
        return slagSiteName;
    }

    public void setSlagSiteName(String slagSiteName) {
        this.slagSiteName = slagSiteName;
    }
}
