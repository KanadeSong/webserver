package com.seater.smartmining.entity;

import com.seater.smartmining.enums.VaildEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "carId", "timeDischarge"}, name = "repeatCheck")})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "carId", "terminalTime"}, name = "repeatCheck")})
public class ProjectUnloadLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private String cmdInd = "";

    @Column
    private Long pktID = 0L;

    @Column
    private String eventId = "";

    @Column
    private Long projectID = 0L;

    @Column
    private Long carID = 0L;

    @Column
    private String carCode = "";

    @Column
    private Date timeLoad = new Date(0L);

    @Column
    private Date timeCheck = new Date(0L);

    @Column
    private Date timeDischarge = new Date(0L);

    @Column
    private Long slagfieldID = 0L;

    @Column
    private String slagFieldName = "";

    public String getSlagFieldName() {
        return slagFieldName;
    }

    public void setSlagFieldName(String slagFieldName) {
        this.slagFieldName = slagFieldName;
    }

    @Column
    private Long excavatCurrent = 0L;

    @Column
    private Long excavatNext = 0L;

    @Column
    private String uid = "";

    @Column
    private String m1fare = "";

    @Column
    private Long loader = 0L;       //物料ID

    public String getLoaderName() {
        return loaderName;
    }

    public void setLoaderName(String loaderName) {
        this.loaderName = loaderName;
    }

    @Column
    private String loaderName = "";     //物料名称

    @Column
    private Date recviceDate = new Date();

    @Column
    private boolean isVaild = true;       //是否有效

    @Column
    private Long terminalTime = 0L;       //终端上传的时间戳

    @Column
    private String remark = "";     //备注

    @Column
    private Long device = 0L;

    public Boolean getDetail() {
        return detail;
    }

    public void setDetail(Boolean detail) {
        this.detail = detail;
    }

    private Boolean detail = false;

    public String getSlagSiteID() {
        return slagSiteID;
    }

    public void setSlagSiteID(String slagSiteID) {
        this.slagSiteID = slagSiteID;
    }

    @Column
    private String slagSiteID = "";        //允许倒渣的渣场ID 多个用逗号隔开

    public Long getExctDist() {
        return exctDist;
    }

    public void setExctDist(Long exctDist) {
        this.exctDist = exctDist;
    }

    public Integer getPriceMethod() {
        return priceMethod;
    }

    public void setPriceMethod(Integer priceMethod) {
        this.priceMethod = priceMethod;
    }

    @Column
    private Integer priceMethod = 0;    //计价方式

    @Column
    private Long exctDist = 0L;

    @Column
    private Integer schMode = 0;      // 排班类型   1 - 渣场终端和挖机终端启用   2 - 渣场终端和检测终端启用
                                                // 3 - 渣场终端、挖机终端、检测终端都启用  4 - 仅渣场终端启用
    @Column
    private Integer dispatchMode = 0;

    @Column
    private Date dateIdentification = null;

    public Date getDateIdentification() {
        return dateIdentification;
    }

    public void setDateIdentification(Date dateIdentification) {
        this.dateIdentification = dateIdentification;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    @Column
    private Shift shift = Shift.Unknown;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCmdInd() {
        return cmdInd;
    }

    public void setCmdInd(String cmdInd) {
        this.cmdInd = cmdInd;
    }

    public Long getPktID() {
        return pktID;
    }

    public void setPktID(Long pktID) {
        this.pktID = pktID;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getProjectID() {
        return projectID;
    }

    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    public Long getCarID() {
        return carID;
    }

    public void setCarID(Long carID) {
        this.carID = carID;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public Date getTimeLoad() {
        return timeLoad;
    }

    public void setTimeLoad(Date timeLoad) {
        this.timeLoad = timeLoad;
    }

    public Date getTimeCheck() {
        return timeCheck;
    }

    public void setTimeCheck(Date timeCheck) {
        this.timeCheck = timeCheck;
    }

    public Date getTimeDischarge() {
        return timeDischarge;
    }

    public void setTimeDischarge(Date timeDischarge) {
        this.timeDischarge = timeDischarge;
    }

    public Long getSlagfieldID() {
        return slagfieldID;
    }

    public void setSlagfieldID(Long slagfieldID) {
        this.slagfieldID = slagfieldID;
    }

    public Long getExcavatCurrent() {
        return excavatCurrent;
    }

    public void setExcavatCurrent(Long excavatCurrent) {
        this.excavatCurrent = excavatCurrent;
    }

    public Long getExcavatNext() {
        return excavatNext;
    }

    public void setExcavatNext(Long excavatNext) {
        this.excavatNext = excavatNext;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getM1fare() {
        return m1fare;
    }

    public void setM1fare(String m1fare) {
        this.m1fare = m1fare;
    }

    public Long getLoader() {
        return loader;
    }

    public void setLoader(Long loader) {
        this.loader = loader;
    }

    public Date getRecviceDate() {
        return recviceDate;
    }

    public void setRecviceDate(Date recviceDate) {
        this.recviceDate = recviceDate;
    }

    public boolean getIsVaild() {
        return isVaild;
    }

    public void setIsVaild(boolean isVaild) {
        this.isVaild = isVaild;
    }

    public Long getTerminalTime() {
        return terminalTime;
    }

    public void setTerminalTime(Long terminalTime) {
        this.terminalTime = terminalTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getDevice() {
        return device;
    }

    public void setDevice(Long device) {
        this.device = device;
    }

    public Integer getSchMode() {
        return schMode;
    }

    public void setSchMode(Integer schMode) {
        this.schMode = schMode;
    }

    public Integer getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(Integer dispatchMode) {
        this.dispatchMode = dispatchMode;
    }
}
