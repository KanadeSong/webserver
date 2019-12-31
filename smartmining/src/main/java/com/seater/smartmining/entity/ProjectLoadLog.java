package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "carId", "timeLoad"}, name = "repeatCheck")})
public class ProjectLoadLog implements Serializable {
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
    private Long excavatCurrent = 0L;

    @Column
    private Long excavatNext = 0L;

    @Column
    private String uid = "";

    @Column
    private String m1fare = "";

    @Column
    private Long loader = 0L;

    @Column
    private Date recviceDate = new Date();

    @Column
    private boolean valid = true;

    @Column
    private Long device = 0L;

    @Column
    private Long timeStay = 0L;

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

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getDevice() {
        return device;
    }

    public void setDevice(Long device) {
        this.device = device;
    }

    public Long getTimeStay() {
        return timeStay;
    }

    public void setTimeStay(Long timeStay) {
        this.timeStay = timeStay;
    }
}
